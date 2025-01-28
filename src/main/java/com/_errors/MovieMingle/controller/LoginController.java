package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.dto.ResetPasswordDto;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    public static final String LAST_USERNAME_KEY = "LAST_USERNAME";

    @GetMapping
    public String login(@RequestParam(value = "error", defaultValue = "false") boolean loginError,
                        @RequestParam(value = "invalid-session", defaultValue = "false") boolean invalidSession,
                        final Model model, HttpSession session) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // Redirecționează utilizatorul autentificat la pagina principală
            return "redirect:/homepage";
        }

        String userName = getUserName(session);

        // Adaugă "forgotPassword" în model, indiferent de condiții
        model.addAttribute("forgotPassword", new ResetPasswordDto());

        if (loginError) {
            if (StringUtils.isNotEmpty(userName)) {
                model.addAttribute("accountLocked", Boolean.TRUE);
                return "login";
            }
        }

        if (invalidSession) {
            model.addAttribute("invalidSession", "You already have an active session. We do not allow multiple active sessions");
        }

        model.addAttribute("accountLocked", Boolean.FALSE);
        return "login";
    }

    final String getUserName(HttpSession session) {
        final String username = (String) session.getAttribute(LAST_USERNAME_KEY);
        if (StringUtils.isNotEmpty(username)) {
            session.removeAttribute(LAST_USERNAME_KEY); // Elimină username-ul din sesiune
        }
        return username;
    }
}
