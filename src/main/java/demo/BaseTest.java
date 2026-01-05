package demo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    static void setup(){

        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

    }
}
