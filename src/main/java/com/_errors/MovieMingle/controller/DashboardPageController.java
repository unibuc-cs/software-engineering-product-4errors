package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.service.user.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardPageController {

    private final AppUserService userService;

    public DashboardPageController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        AppUser user;

        if (principal != null) {
            user = userService.findByEmail(principal.getName());
            System.out.println("🔵 User found: " + user.getId());

            if (user.getAvatar() == null) {
                user.setAvatar("general_avatar.png");
            }
        } else {
            user = new AppUser();
            user.setAvatar("general_avatar.png");
        }

        model.addAttribute("user", user);
        return "dashboard";
    }
}
