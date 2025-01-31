package com._errors.MovieMingle.dto;

import jakarta.validation.constraints.NotEmpty;

public class ResetPasswordDto {
    private String email;
    @NotEmpty(message = "Token cannot be empty")
    private String token;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Repeat password cannot be empty")
    private String repeatPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
