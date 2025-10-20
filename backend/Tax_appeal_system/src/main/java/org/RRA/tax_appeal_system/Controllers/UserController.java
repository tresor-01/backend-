package org.RRA.tax_appeal_system.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.UpdateUserRequest;
import org.RRA.tax_appeal_system.Services.UserInfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/auth/")
public class UserController {
    private final UserInfoService userInfoService;

    @Operation(summary = "Updating User")
    @PreAuthorize("hasAnyAuthority('COMMITTEE_LEADER')")
    @PutMapping("/user/{id}")
    public String updateUser(@PathVariable String id, @RequestBody UpdateUserRequest updateUser) {
        userInfoService.updateUserInfo(id,updateUser);
        return "User updated";
    }
}
