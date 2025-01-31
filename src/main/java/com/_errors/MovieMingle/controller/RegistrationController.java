package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UserAlreadyExistsException;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.dto.RegisterDto;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.service.user.DefaultAppUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final String REDIRECT_LOGIN= "redirect:/login";

    @Resource
    private MessageSource messageSource;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DefaultAppUserService userService;

    @GetMapping
    public String register(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // Redirecționează utilizatorul autentificat la pagina principală
            return "redirect:/homepage";
        }
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);

        model.addAttribute("succes", false);
        return "register";
    }

    @PostMapping
    public String register(
            Model model,
            @Valid @ModelAttribute RegisterDto registerDto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ){

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            result.addError(
                    new FieldError("registerDto", "confirmPassword",
                            "Password and Confirm Password do not match!")
            );
        }

        AppUser appUser = userRepository.findByEmail(registerDto.getEmail());
        if(appUser != null){
            result.addError(
                    new FieldError("registerDto","email",
                            "E-mail address is already used!")
            );
        }

        if (result.hasErrors()){
            return "register";
        }

        try {

            userService.register(registerDto);

        } catch (UserAlreadyExistsException e ) {
            result.rejectValue("email", "userData.email","An account already exists for this email.");
            model.addAttribute("registrationForm", registerDto);
            return "register";
        }
        model.addAttribute("registrationMsg", messageSource.getMessage("user.registration.verification.email.msg", null, LocaleContextHolder.getLocale()));
        return "register";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){

        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.missing.token", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.invalid.token", null,LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }

        redirAttr.addFlashAttribute("verifiedAccountMsg", messageSource.getMessage("user.registration.verification.success", null,LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }
}
