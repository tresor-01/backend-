package org.RRA.tax_appeal_system.Services;

import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.UpdateUserRequest;
import org.RRA.tax_appeal_system.Models.UserInfo;
import org.RRA.tax_appeal_system.Repositories.UserInfoRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class UserInfoService {
    private final UserInfoRepo userRepo;

    public boolean doesUserExist(String userId) {
        return userRepo.existsById(userId);
    }

    @Transactional
    public UserInfo updateUserInfo(String id, UpdateUserRequest userInfo) throws UsernameNotFoundException {

            UserInfo user = userRepo.findById(id).orElseThrow(()->new IllegalArgumentException("User with "+ id +" not found"));

            // updating user
            //checking whether the names are not empty
            if (userInfo.fullName() != null && !userInfo.fullName().trim().isEmpty()) {
                user.setFullName(userInfo.fullName());
            };

            //checkin whether the email is not empty
            if(userInfo.email() != null && !userInfo.email().trim().isEmpty()) {
                user.setEmail(userInfo.email());
            }

            user.setCommitteeRole(userInfo.committee_role());
            user.setTitle(userInfo.title());

            //checking whether committee group is not empty
            if (userInfo.committeeGroup() != null && !userInfo.committeeGroup().toString().trim().isEmpty()) {
                user.setCommitteeGroup(userInfo.committeeGroup());
            }
            //checking that phone number is not empty
            user.setPhoneNumber(userInfo.phoneNumber());

            userRepo.save(user);

            System.out.println("User Updated successfully");

            return user;
        }

}
