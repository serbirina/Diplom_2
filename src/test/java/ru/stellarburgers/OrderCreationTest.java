package ru.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.OrderCreationBody;
import ru.stellarburgers.model.request.UserRegistrationBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.step.OrderSteps;
import ru.stellarburgers.step.UserSteps;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreationTest extends BaseTest {
    private final UserRegistrationBody userRegistrationBody = new UserRegistrationBody("email@mail.test", "interstellar", "Pulsar");
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private OrderCreationBody orderCreationBody;
    private String accessToken;

    @Before
    public void setUp() {
        ValidatableResponse response = userSteps.registrationUser(userRegistrationBody);
        response.statusCode(HTTP_OK).body("accessToken", notNullValue());

        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Should return 200 when creating an order with valid required fields and a valid access token.")
    @Description("Verify that an order can be created with valid required fields and a valid access token.\nExpected result: HTTP 200 response, response field \"success\" - true, the \"name\" and \"number\" fields in the response body contain non-null values.")
    public void orderCreationWithAccessTokenAndIngredientsShouldReturn200() {
        orderCreationBody = new OrderCreationBody(new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa74"});
        ValidatableResponse response = orderSteps.createOrderWithAccessToken(orderCreationBody, accessToken);

        orderSteps.assertThatOrderCreated(response);
    }

    @Test
    @DisplayName("Should return 401 when creating an order with valid required fields but without an access token.")
    @Description("Verify that an order cannot be created without valid access token.\nExpected result: HTTP 401 response, response field \"success\" - false and an error message \"You should be authorised\" in response body.")
    public void orderCreationWithoutAccessTokenShouldReturn401() {
        orderCreationBody = new OrderCreationBody(new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa74"});
        ValidatableResponse response = orderSteps.createOrderWithoutAccessToken(orderCreationBody);

        orderSteps.assertThatOrderIsNotCreatedCode401(response);
    }

    @Test
    @DisplayName("Should return 400 when creating an order without ingredients.")
    @Description("Verify that an order cannot be created without ingredients.\nExpected result: HTTP 400 response, response field \"success\" - false and an error message \"Ingredient ids must be provided\" in response body.")
    public void orderCreationWithoutIngredientsShouldReturn400() {
        orderCreationBody = new OrderCreationBody(new String[]{});
        ValidatableResponse response = orderSteps.createOrderWithAccessToken(orderCreationBody, accessToken);

        orderSteps.assertThatOrderIsNotCreatedCode400(response);
    }

    @Test
    @DisplayName("Should return 500 when creating an order with an invalid ingredient hash.")
    @Description("Verify that an order cannot be created with an invalid ingredient hash.\nExpected result: HTTP 500 response.")
    public void orderCreationWithInvalidIngredientHashShouldReturn500() {
        orderCreationBody = new OrderCreationBody(new String[]{"2001bdaaa6d", "2001bdaaa6e", "2001bdaaa74"});
        ValidatableResponse response = orderSteps.createOrderWithAccessToken(orderCreationBody, accessToken);

        orderSteps.assertThatOrderIsNotCreatedCode500(response);
    }

    @After
    public void cleanUp() {
        userSteps
                .deleteUser(accessToken)
                .statusCode(HTTP_ACCEPTED);
    }
}
