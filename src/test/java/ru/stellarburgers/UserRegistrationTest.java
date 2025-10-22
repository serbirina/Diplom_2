package ru.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.UserRegistrationBody;
import org.junit.After;
import org.junit.Test;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.notNullValue;

public class UserRegistrationTest extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    private final String email = "email@mail.test";
    private final String password = "interstellar";
    private final String name = "Pulsar";
    private String accessToken;

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

    @After
    public void cleanUp() {
        userSteps
                .deleteUser(accessToken)
                .statusCode(HTTP_ACCEPTED);
    }
}
