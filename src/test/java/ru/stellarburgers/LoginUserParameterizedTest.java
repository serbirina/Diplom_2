package ru.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.UserLoginBody;
import ru.stellarburgers.model.request.UserRegistrationBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class LoginUserParameterizedTest extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    private final String name = "Pulsar";
    private String accessToken;

    private final String email;
    private final String password;
    private final int statusCode;

    public LoginUserParameterizedTest(String email, String password, int statusCode) {
        this.email = email;
        this.password = password;
        this.statusCode = statusCode;
    }

    @Parameterized.Parameters(name = "Authorization with email \"{0}\" and password \"{1}\" should return response code {2}")
    public static Object[][] getParameters() {
        return new Object[][]{
                {"email@mail.test", "interstellar", HTTP_OK},
                {"test@test.te", "", HTTP_UNAUTHORIZED},
                {"", "interstellar", HTTP_UNAUTHORIZED},
        };
    }

    @Before
    public void setUp() {
        UserRegistrationBody userRegistrationBody = new UserRegistrationBody("email@mail.test", "interstellar", name);

        ValidatableResponse response = userSteps.registrationUser(userRegistrationBody);
        response.statusCode(HTTP_OK).body("accessToken", notNullValue());

        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Should return appropriate response codes for complete and incomplete required fields.")
    @Description("1.Verify that a user can be authorized with valid required fields.\nExpected result: HTTP 200 response, The fields \"email\" and \"name\" contain data entered by the user when registering, The \"accessToken\" and \"refreshToken\" fields in the response body contain non-null values.\n2.Verify that a user cannot be authorized if one of the fields is missing\nExpected result: HTTP 401 response, response field \"success\" - false and an error message \"email or password are incorrect\" in response body.")
    public void shouldReturnCorrectStatusForAuthorization() {
        UserLoginBody userLoginBody = new UserLoginBody(email, password);

        ValidatableResponse response = userSteps.loginUser(userLoginBody);

        if (statusCode == HTTP_OK) {
            userSteps.assertThatUserLoggedIn(response, statusCode, email, name);
        } else {
            userSteps.assertThatUserIsNotLoggedIn(response, statusCode, "email or password are incorrect");
        }
    }

    @After
    public void cleanUp() {
        userSteps
                .deleteUser(accessToken)
                .statusCode(HTTP_ACCEPTED);
    }
}
