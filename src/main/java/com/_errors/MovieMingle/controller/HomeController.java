package com._errors.MovieMingle.controller;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import com._errors.MovieMingle.dto.RegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MovieService movieService;

    @GetMapping({"", "/"})
    public String home(Model model, HttpSession session) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(loggedUser);

        Boolean isNewLogin = (Boolean) session.getAttribute("isNewLogin");


        //refresh the list only if it is a new login
        if (isNewLogin == null || isNewLogin) {
            List<Movie> bestRated = movieService.getBestRated(12);
            model.addAttribute("randomBestRated", bestRated);
            session.setAttribute("isNewLogin", false);
        }
        else {

            List<Movie> bestRated = (List<Movie>) session.getAttribute("cachedBestRated");
            if (bestRated == null) {

                bestRated = movieService.getBestRated(12);
                model.addAttribute("randomBestRated", bestRated);
            } else {
                model.addAttribute("randomBestRated", bestRated);
            }
        }


        model.addAttribute("username", user.getFirstName());
        model.addAttribute("quizCompleted", user.isQuizCompleted());

        session.setAttribute("cachedBestRated", model.getAttribute("randomBestRated"));

        return "index";
    }


    @GetMapping("/quiz-page")
    public String quizPage() {
        return "quiz";
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}

