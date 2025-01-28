package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.dto.ResetPasswordDto;
import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.service.user.AppUserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com._errors.MovieMingle.exception.UnknownIdentifierException;

@Controller
@RequestMapping("/password")
public class PasswordResetController {
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String MSG = "resetPasswordMsg";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AppUserAccountService userAccountService;


    @PostMapping("/request")
    public String resetPassword(final ResetPasswordDto forgotPasswordForm, RedirectAttributes redirAttr) {
        try {
            userAccountService.forgottenPassword(forgotPasswordForm.getEmail());

        } catch (UnknownIdentifierException e) {
            // log the error
        }
        redirAttr.addFlashAttribute(MSG,
                messageSource.getMessage("user.forgotpwd.msg", null, LocaleContextHolder.getLocale())
        );
        return REDIRECT_LOGIN;
    }

    @GetMapping("/change")
    public String changePassword(@RequestParam(required = false) String token, Model model, RedirectAttributes redirAttr) {
        if (token == null || token.isEmpty()) {
            redirAttr.addFlashAttribute("tokenError",
                    messageSource.getMessage("user.registration.verification.missing.token", null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setToken(token); // Include token-ul în obiect
        model.addAttribute("forgotPassword", resetPasswordDto); // Asigură-te că acest obiect există în model
        return "changePassword";
    }


    @PostMapping("/change")
    public String changePassword(@Valid @ModelAttribute("forgotPassword") ResetPasswordDto data,
                                 BindingResult result,
                                 Model model) {
        // Verifică dacă parolele nu coincid
        if (!data.getPassword().equals(data.getRepeatPassword())) {
            result.rejectValue("repeatPassword", "error.repeatPassword",
                    messageSource.getMessage("user.password.mismatch", null, LocaleContextHolder.getLocale()));
        }

        if (result.hasErrors()) {
            // Dacă există erori, afișează-le în formular
            return "changePassword";
        }

        try {
            userAccountService.updatePassword(data.getPassword(), data.getToken());
        } catch (InvalidTokenException e) {
            model.addAttribute("tokenError",
                    messageSource.getMessage("user.registration.verification.invalid.token", null, LocaleContextHolder.getLocale()));
            return "changePassword";
        } catch (UnknownIdentifierException e) {
            model.addAttribute("tokenError",
                    messageSource.getMessage("user.not.found.for.token", null, LocaleContextHolder.getLocale()));
            return "changePassword";
        }

        model.addAttribute("passwordUpdateMsg",
                messageSource.getMessage("user.password.updated.msg", null, LocaleContextHolder.getLocale()));
        return "login";
    }


    private boolean isUserLoggedIn() {
        // Verifică dacă utilizatorul este autentificat
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }


    private void setResetPasswordForm(final Model model, ResetPasswordDto data){
        model.addAttribute("forgotPassword",data);
    }
}
