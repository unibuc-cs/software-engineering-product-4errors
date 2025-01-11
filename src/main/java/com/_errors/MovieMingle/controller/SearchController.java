package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.service.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class SearchController {

    @Autowired
    private AppUserService userService;

    @GetMapping("/search")
    public String searchPage(Model model, Principal principal) {
        AppUser user;

        if (principal == null) {
            // Crează un utilizator anonim cu avatarul implicit
            user = new AppUser();
            user.setAvatar("general_avatar.png");  // Folosește path-ul relativ
        } else {
            // Găsește utilizatorul pe baza email-ului
            user = userService.findByEmail(principal.getName());
            if (user.getAvatar() == null) {
                user.setAvatar("general_avatar.png");
            }
        }

        // Adaugă utilizatorul la model
        model.addAttribute("user", user);

        return "search"; // Asigură-te că există un fișier `search.html` în directorul `templates`
    }
}
