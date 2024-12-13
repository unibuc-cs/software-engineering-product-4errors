package com._errors.MovieMingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"","/"})
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
