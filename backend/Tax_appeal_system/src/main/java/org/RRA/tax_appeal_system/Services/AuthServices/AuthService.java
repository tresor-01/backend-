package org.RRA.tax_appeal_system.Services.AuthServices;

import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.LoginRequest;
import org.RRA.tax_appeal_system.DTOS.requests.RegisterUser;
import org.RRA.tax_appeal_system.DTOS.responses.AuthResponse;
import org.RRA.tax_appeal_system.DTOS.responses.GenericResponse;
import org.RRA.tax_appeal_system.Exceptions.UserAlreadyExistsException;
import org.RRA.tax_appeal_system.Models.UserInfo;
import org.RRA.tax_appeal_system.Repositories.RefreshTokenRepository;
import org.RRA.tax_appeal_system.Repositories.UserInfoRepo;
import org.RRA.tax_appeal_system.Services.UserInfoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserInfoRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public boolean userExists(String email,int phoneNumber) {
        return userRepo.existsByEmail(email) || userRepo.existsByPhoneNumber(phoneNumber);
    }

    @Transactional
    public UserInfo registerUser(RegisterUser userinfo) {

        //check whether the user exists

        if (userExists(userinfo.email(), userinfo.phoneNumber())) {
            throw new UserAlreadyExistsException("user with " + userinfo.email() + " and " + userinfo
                    .phoneNumber() + " already exists !");
        }

        // User registration
        UserInfo user = UserInfo.builder()
                .fullName(userinfo.fullName())
                .email(userinfo.email())
                .title(userinfo.title())
                .committeeGroup(userinfo.committeeGroup())
                .phoneNumber(userinfo.phoneNumber())
                .password(passwordEncoder.encode(userinfo.password()))
                .committeeRole(userinfo.committeeRole())
                .build();

        // saving the user
        UserInfo savedUser = userRepo.save(user);
        System.out.println("User saved successfully");

        return savedUser;
    }

    public AuthResponse login (LoginRequest request) throws UsernameNotFoundException {
        //check whether even the userExists
        if(!userRepo.existsByEmail(request.getEmail())){
            throw new UsernameNotFoundException("user with " + request.getEmail() + " not found");
        }
        try {
            //Authenticating the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve the UserDetails object from the Authentication
            UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

            // Check if the user already has a valid token
            UserInfo userInfo = userRepo.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User with " + userDetails.getUsername() + " not found"));


            // 3. Generate a new token
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            // 4. Return the custom response with user data and the new token
            return new AuthResponse(userInfo.getFullName(), userInfo.getEmail(), userInfo.getCommitteeRole().toString(), accessToken, refreshToken);
        }catch (BadCredentialsException e){
            // If authentication fails
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<String>> logout(String userEmail) {
        try {

            // 1. Retrieve the UserInfo object
            System.out.println(" finding user with " + userEmail);
            UserInfo userInfo = userRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found for logout"));


            // 2. Delete the refresh token from the database
            // Convert UUID to String for the repository method
            refreshTokenRepository.deleteByUserInfo(userInfo);

            // 3. Clear the Security Context
            SecurityContextHolder.clearContext();

            // 4. Return consistent response
            GenericResponse<String> response = new GenericResponse<>(
                    200,
                    "Logout successful.",
                    null
            );

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            GenericResponse<String> errorResponse = new GenericResponse<>(
                    404,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            GenericResponse<String> errorResponse = new GenericResponse<>(
                    500,
                    "Logout failed",
                    null
            );
            System.out.println("ERROR: [ " + e.getMessage() + " ]");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Transactional
    public  AuthResponse refreshToken(String refreshToken) {

        return jwtService.findByToken(refreshToken)
                .map(token -> {
                    jwtService.verifyExpiration(token); // Check if expired

                    // Generate new Access Token
                    String newAccessToken = jwtService.generateAccessToken(token.getUserInfo().getEmail());
                    // Generate and save a new Refresh Token
                    String newRefreshToken = jwtService.generateRefreshToken(token.getUserInfo().getEmail());

                    // Return new tokens and user details
                    return new AuthResponse(
                            token.getUserInfo().getFullName(),
                            token.getUserInfo().getEmail(),
                            token.getUserInfo().getCommitteeRole().toString(),
                            newAccessToken,
                            newRefreshToken
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
