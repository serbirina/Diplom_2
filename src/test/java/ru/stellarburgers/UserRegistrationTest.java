package ru.stellarburgers;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.model.request.UserRegistrationBody;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.notNullValue;

public class UserRegistrationTest extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    Faker faker = new Faker();

    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        name = faker.name().firstName();
        email = faker.internet().safeEmailAddress();
        password = faker.internet().password(8, 16);
    }

    @Test
    @DisplayName("Should return 200 when registering a user with valid required fields")
    @Description("Verify that a user can be registered with valid required fields.\nExpected result: HTTP 200 response, previously introduced data in the fields \"email\" and \"name\", the \"accessToken\" and \"refreshToken\" fields in the response body contain non-null values.")
    public void createUserWithValidRequiredFieldsShouldReturn200() {
        UserRegistrationBody user = new UserRegistrationBody(email, password, name);

        ValidatableResponse response = userSteps.registrationUser(user);

        userSteps.assertThatUserRegistered(response, email, name);

        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Should return 409 when trying to create two identical users")
    @Description("Verify that registering a user twice with the same data fails.\nExpected result: HTTP 403 response, response field \"success\" - false and an error message \"User already exists\" in response body.")
    public void createUserTwiceWithSameDataShouldReturn403() {
        UserRegistrationBody user = new UserRegistrationBody(email, password, name);

        ValidatableResponse response = userSteps.registrationUser(user);
        response
                .statusCode(HTTP_OK)
                .body("accessToken", notNullValue());
        accessToken = userSteps.getAccessToken(response);

        response = userSteps.registrationUser(user);
        userSteps.assertThatUserNotRegistered(response, HTTP_FORBIDDEN, "User already exists");
    }

    @Test
    @DisplayName("Should return 403 when registering a user without a name")
    @Description("Verify that a user cannot be registered without a name.\nExpected result: HTTP 403 response, response field \"success\" - false and an error message \"Email, password and name are required fields\" in response body.")
    public void createUserWithoutNameShouldReturn403() {
        UserRegistrationBody user = new UserRegistrationBody(email, password, "");

        ValidatableResponse response = userSteps.registrationUser(user);
        userSteps.assertThatUserNotRegistered(
                response, HTTP_FORBIDDEN, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Should return 403 when registering a user without an email")
    @Description("Verify that a user cannot be registered without an email.\nExpected result: HTTP 403 response, response field \"success\" - false and an error message \"Email, password and name are required fields\" in response body.")
    public void createUserWithoutEmailShouldReturn403() {
        UserRegistrationBody user = new UserRegistrationBody("", password, name);

        ValidatableResponse response = userSteps.registrationUser(user);
        userSteps.assertThatUserNotRegistered(
                response, HTTP_FORBIDDEN, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Should return 403 when registering a user without a password")
    @Description("Verify that a user cannot be registered without a password.\nExpected result: HTTP 403 response, response field \"success\" - false and an error message \"Email, password and name are required fields\" in response body.")
    public void createUserWithoutPasswordShouldReturn403() {
        UserRegistrationBody user = new UserRegistrationBody(email, "", name);

        ValidatableResponse response = userSteps.registrationUser(user);
        userSteps.assertThatUserNotRegistered(
                response, HTTP_FORBIDDEN, "Email, password and name are required fields");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps
                    .deleteUser(accessToken)
                    .statusCode(HTTP_ACCEPTED);
        }
    }
}
