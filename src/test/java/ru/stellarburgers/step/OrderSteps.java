package ru.stellarburgers.step;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.model.request.OrderCreationBody;

import static ru.stellarburgers.config.RestConfig.CREATE_ORDER;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OrderSteps {
    @Step("Create an order with accessToken")
    public ValidatableResponse createOrderWithAccessToken(OrderCreationBody orderCreationBody, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderCreationBody)
                .auth()
                .oauth2(accessToken)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Create an order without accessToken")
    public ValidatableResponse createOrderWithoutAccessToken(OrderCreationBody orderCreationBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderCreationBody)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Assert that an order is created")
    public void assertThatOrderCreated(ValidatableResponse response) {
        response
                .statusCode(HTTP_OK)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", is(true));
    }

    @Step("Assert that an order is not created and return 401")
    public void assertThatOrderIsNotCreatedCode401(ValidatableResponse response) {
        response
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Step("Assert that an order is not created and return 400")
    public void assertThatOrderIsNotCreatedCode400(ValidatableResponse response) {
        response
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Step("Assert that an order is not created and return 500")
    public void assertThatOrderIsNotCreatedCode500(ValidatableResponse response) {
        response
                .statusCode(HTTP_INTERNAL_ERROR);
    }
}
