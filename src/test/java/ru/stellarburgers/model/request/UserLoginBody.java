package ru.stellarburgers.model.request;

public class UserLoginBody {
    private String email;
    private String password;

    public UserLoginBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginBody() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserLoginBody setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserLoginBody setPassword(String password) {
        this.password = password;
        return this;
    }
}
