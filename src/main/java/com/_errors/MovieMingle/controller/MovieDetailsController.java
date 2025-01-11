package com._errors.MovieMingle.controller;
import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.service.MovieApiClient;
import com._errors.MovieMingle.service.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/movie-details")
public class MovieDetailsController {

    @Autowired
    private MovieApiClient movieApiClient;

    @Autowired
    private AppUserService userService;

    @GetMapping("/{id}")
    public String getMovieDetailsPage(@PathVariable Long id, Principal principal, Model model) {
        // Obține detaliile filmului
        MovieDto movie = movieApiClient.getMovie(id);
        System.out.println(movie);

        // Adaugă detaliile filmului în model pentru a fi accesibile în pagină
        model.addAttribute("movie", movie);

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

        return "movie-details"; // Va căuta `movie-details.html` în /templates
    }

}