package com.ecom.ecommerce_backend.api.model;

import jakarta.validation.constraints.Email;

public class RegistrationBody {
    private String username;

    @Email
    private String email;
    private String password;
    private String first_name;
    private String last_name;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }
}
