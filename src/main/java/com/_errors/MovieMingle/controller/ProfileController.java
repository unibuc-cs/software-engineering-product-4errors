package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ProfileController {

    private final AppUserRepository appUserRepository;

    @Autowired
    public ProfileController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        AppUser currentUser = appUserRepository.findByEmail(userEmail);
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }

        return "profile";
    }

    @PostMapping("/profile/update-avatar")
    @ResponseBody
    public String updateAvatar(@RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        AppUser currentUser = appUserRepository.findByEmail(userEmail);

        if (currentUser != null && payload.containsKey("avatar")) {
            String avatarPath = payload.get("avatar");
            String avatarFileName = avatarPath.substring(avatarPath.lastIndexOf('/') + 1);

            currentUser.setAvatar(avatarFileName);
            appUserRepository.save(currentUser);
            return "Avatar actualizat cu succes!";
        }
        return "Eroare: Utilizatorul nu a fost găsit sau datele trimise sunt invalide.";
    }




}
