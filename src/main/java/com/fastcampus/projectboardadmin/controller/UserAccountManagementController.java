package com.fastcampus.projectboardadmin.controller;
import com.fastcampus.projectboardadmin.dto.response.UserAccountResponse;
import com.fastcampus.projectboardadmin.service.UserAccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/management/user-accounts")
@Controller
public class UserAccountManagementController {

    private final UserAccountManagementService userAccountManagementService;

    @GetMapping
    public String userAccounts(Model model) {
        model.addAttribute(
                "userAccounts",
                userAccountManagementService
                        .getUsers()
                        .stream()
                        .map(UserAccountResponse::from)
                        .toList()
        );

        return "management/user-accounts";
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public UserAccountResponse userAccount(@PathVariable String userId) {
        return UserAccountResponse.from(userAccountManagementService.getUser(userId));
    }

    @PostMapping("/{userId}")
    public String deleteUserAccount(@PathVariable String userId) {
        userAccountManagementService.deleteUser(userId);
        return "redirect:/management/user-accounts";
    }
}
