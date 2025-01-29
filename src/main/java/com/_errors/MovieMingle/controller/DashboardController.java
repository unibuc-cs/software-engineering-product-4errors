package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.dto.UserDashboardDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.service.DashboardService;
import com._errors.MovieMingle.service.user.AppUserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final AppUserService userService;

    public DashboardController(DashboardService dashboardService, AppUserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping
    public UserDashboardDto getDashboardData(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        // We extract the user based on the email (exactly like in the RecommendationController)
        AppUser user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Fetching dashboard data for user: " + user.getId());

        return dashboardService.getUserDashboardStats((long) user.getId());
    }
}
