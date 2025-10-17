package org.RRA.tax_appeal_system.Services.AuthServices;

import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.LoginRequest;
import org.RRA.tax_appeal_system.DTOS.requests.RegisterUser;
import org.RRA.tax_appeal_system.DTOS.responses.AuthResponse;
import org.RRA.tax_appeal_system.Exceptions.UserAlreadyExistsException;
import org.RRA.tax_appeal_system.Models.UserInfo;
import org.RRA.tax_appeal_system.Repositories.RefreshTokenRepository;
import org.RRA.tax_appeal_system.Repositories.UserInfoRepo;
import org.RRA.tax_appeal_system.Services.UserInfoDetails;
import org.springframework.beans.factory.annotation.Autowired;
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
        //Authenticating the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Retrieve the UserDetails object from the Authentication
            UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

            // Check if the user already has a valid token
            UserInfo userInfo = userRepo.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 3. Generate a new token
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            // 4. Return the custom response with user data and the new token
            return new AuthResponse(userInfo.getFullName(),userInfo.getEmail(),userInfo.getCommitteeRole().toString(),accessToken,refreshToken);
        } else {
            // If authentication fails
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    // In AuthService

        @Transactional // Use @Transactional to ensure the deletion is committed
        public ResponseEntity<String> logout(String userEmail) {
            // 1. Retrieve the UserInfo object
            UserInfo userInfo = userRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found for logout"));

            // 2. Delete the refresh token from the database
            // This effectively ends the user's persistent session
            refreshTokenRepository.deleteByUserInfo(userInfo.getId());

            // 3. Clear the Security Context (optional, but good practice for immediate logouts)
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("Logout successful. Refresh token revoked.");
        }



}
