package ru.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.UserRegistrationBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@RunWith(Parameterized.class)
public class UserRegistrationParameterizedTest extends BaseTest {
    private final UserSteps userSteps = new UserSteps();

    private final String email;
    private final String password;
    private final String name;

    public UserRegistrationParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name="Attempt to register user with email \"{0}\", password \"{1}\", name \"{2}\" should return response code 403")
    public static Object[][] getParameters() {
        return new Object[][]{
                {"email@mail.test", "interstellar", ""},
                {"email@mail.test", "", "Pulsar"},
                {"", "interstellar", "Pulsar"},
        };
    }

    @Test
    @DisplayName("Should return 403 when one of field is missing")
    @Description("Verify that a user cannot be registered if one of the fields is missing.\nExpected result: HTTP 403 response, response field \"success\" - false and an error message \"Email, password and name are required fields\" in response body.")
    public void createUserOneOfFieldsMissingShouldReturn403() {
        UserRegistrationBody user = new UserRegistrationBody(email, password, name);

        ValidatableResponse response = userSteps.registrationUser(user);
        userSteps.assertThatUserNotRegistered(
                response, HTTP_FORBIDDEN, "Email, password and name are required fields");
    }
}