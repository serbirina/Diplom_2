package ru.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.junit.Before;

import static ru.stellarburgers.config.RestConfig.BASE_URL;

public class BaseTest {

    @Before
    public void startUp() {
        RestAssured.baseURI = BASE_URL;

        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig
                        .logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());
    }
}
