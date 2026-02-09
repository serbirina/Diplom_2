package ru.stellarburgers;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.model.request.UserLoginBody;
import ru.stellarburgers.model.request.UserRegistrationBody;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    Faker faker = new Faker();

    private String accessToken;
    private String name;
    private String email;
    private String password;

    @Before
    public void setUp() {
        name = faker.name().firstName();
        email = faker.internet().safeEmailAddress();
        password = faker.internet().password(8, 16);

        UserRegistrationBody userRegistrationBody = new UserRegistrationBody(email, password, name);

        ValidatableResponse response = userSteps.registrationUser(userRegistrationBody);
        response.statusCode(HTTP_OK).body("accessToken", notNullValue());

        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Should return 200 when logging in a user with valid required fields")
    @Description("Verify that a user can log in with valid required fields.\nExpected result: HTTP 200 response, The fields \"email\" and \"name\" contain data entered by the user when registering, the \"accessToken\" and \"refreshToken\" fields in the response body contain non-null values.")
    public void loginUserWithValidRequiredFieldsShouldReturn200() {
        UserLoginBody userLoginBody = new UserLoginBody(email, password);

        ValidatableResponse response = userSteps.loginUser(userLoginBody);
        userSteps.assertThatUserLoggedIn(response, HTTP_OK, email, name);
    }

    @Test
    @DisplayName("Should return 200 when logging in a user without email")
    @Description("Verify that a user cannot be logged in without email\nExpected result: HTTP 401 response, response field \"success\" - false and an error message \"email or password are incorrect\" in response body.")
    public void loginUserWithoutEmailShouldReturn200() {
        UserLoginBody userLoginBody = new UserLoginBody("", password);

        ValidatableResponse response = userSteps.loginUser(userLoginBody);
        userSteps.assertThatUserIsNotLoggedIn(response, HTTP_UNAUTHORIZED, "email or password are incorrect");
    }

    @Test
    @DisplayName("Should return 200 when logging in a user without password")
    @Description("Verify that a user cannot be logged in without password\nExpected result: HTTP 401 response, response field \"success\" - false and an error message \"email or password are incorrect\" in response body.")
    public void loginUserWithoutPasswordShouldReturn200() {
        UserLoginBody userLoginBody = new UserLoginBody(email, "");

        ValidatableResponse response = userSteps.loginUser(userLoginBody);
        userSteps.assertThatUserIsNotLoggedIn(response, HTTP_UNAUTHORIZED, "email or password are incorrect");
    }

    @After
    public void cleanUp() {
        userSteps
                .deleteUser(accessToken)
                .statusCode(HTTP_ACCEPTED);
    }
}
