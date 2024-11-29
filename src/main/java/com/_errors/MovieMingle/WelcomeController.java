package com._errors.MovieMingle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //to recognise the class as a web controller
public class WelcomeController {

    @GetMapping("/welcome") //set the endpoint name
    public String welcome(){
        return "welcome to 4errors!";
    }

}
