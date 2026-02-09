package ru.stellarburgers.step;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.UserLoginBody;
import ru.stellarburgers.model.request.UserRegistrationBody;

import static ru.stellarburgers.config.RestConfig.*;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UserSteps {
    @Step("Registration user")
    public ValidatableResponse registrationUser(UserRegistrationBody user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUser(UserLoginBody user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .auth()
                .oauth2(accessToken)
                .when()
                .delete(DELETE_USER)
                .then();
    }

    @Step("Assert that user is registered")
    public void assertThatUserRegistered(ValidatableResponse response, String email, String name) {
        response
                .statusCode(HTTP_OK)
                .body("success", is(true))
                .body("user.email", is(email))
                .body("user.name", is(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Step("Assert that user is not registered")
    public void assertThatUserNotRegistered(ValidatableResponse response, int responseCode, String responseBody) {
        response
                .statusCode(responseCode)
                .body("success", is(false))
                .body("message", is(responseBody));
    }

    @Step("Assert that user is logged in")
    public void assertThatUserLoggedIn(ValidatableResponse response, int statusCode, String email, String name) {
        response
                .statusCode(statusCode)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", is(email))
                .body("user.name", is(name));
    }

    @Step("Assert that user is not logged in")
    public void assertThatUserIsNotLoggedIn(ValidatableResponse response, int responseCode, String responseBody) {
        response
                .statusCode(responseCode)
                .body("success", is(false))
                .body("message", is(responseBody));
    }

    public String getAccessToken(ValidatableResponse response) {
        return response
                .extract()
                .body()
                .jsonPath()
                .getString("accessToken")
                .replace("Bearer ", "").trim();
    }
}
