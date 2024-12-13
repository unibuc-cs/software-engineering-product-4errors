package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.dto.RegisterDto;
import com._errors.MovieMingle.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class RegistrationController {
    @Autowired
    private AppUserRepository repo;

    @GetMapping("/register")
    public String register(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);

        model.addAttribute("succes", false);
        return "register";
    }

    @PostMapping("/register")
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

        AppUser appUser = repo.findByEmail(registerDto.getEmail());
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
            // If the registration has no errors, then we create a new account
            var bCryptEncoder = new BCryptPasswordEncoder();
            AppUser newUser = new AppUser();
            newUser.setFirstName(registerDto.getFirstName());
            newUser.setLastName(registerDto.getLastName());
            newUser.setEmail(registerDto.getEmail());
            newUser.setRole("user");
            newUser.setCreatedAt(new Date());
            newUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));

            repo.save(newUser);

            // Redirecționare către pagina de login cu un mesaj de succes
            redirectAttributes.addFlashAttribute("message", "The registration was successfully completed, please login to proceed.");
            return "redirect:/login";  // Redirecționăm utilizatorul la login
        } catch (Exception ex) {
            result.addError(
                    new FieldError("registerDto", "firstName", ex.getMessage())
            );
        }

        return "register";
    }
}
