package ru.stellarburgers.model.request;

public class UserRegistrationBody {
    private String email;
    private String password;
    private String name;

    public UserRegistrationBody(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserRegistrationBody() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public UserRegistrationBody setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserRegistrationBody setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationBody setName(String name) {
        this.name = name;
        return this;
    }
}
