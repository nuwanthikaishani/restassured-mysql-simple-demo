package demo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class BookingApiDbTest extends BaseTest{

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Test
    void createBooking_storeInMySql_thenVerifyDb_andVerifyApiMatchesDb() throws Exception {

        // 1) Create a booking on the practice API
        Map<String, Object> payload = Map.of(
                "firstname", "Isuru",
                "lastname", "Test",
                "totalprice", 123,
                "depositpaid", true,
                "bookingdates", Map.of(
                        "checkin", "2026-01-10",
                        "checkout", "2026-01-12"
                ),
                "additionalneeds", "Breakfast"
        );

        String responseBody =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        // Response shape: { "bookingid": 1, "booking": { ... } }
        JsonNode root = MAPPER.readTree(responseBody);
        int bookingId = root.get("bookingid").asInt();

        JsonNode booking = root.get("booking");
        String firstname = booking.get("firstname").asText();
        String lastname = booking.get("lastname").asText();
        int totalprice = booking.get("totalprice").asInt();
        boolean depositpaid = booking.get("depositpaid").asBoolean();
        LocalDate checkin = LocalDate.parse(booking.get("bookingdates").get("checkin").asText());
        LocalDate checkout = LocalDate.parse(booking.get("bookingdates").get("checkout").asText());

        // 2) Store into your local MySQL (this simulates "DB verification" pattern)
        DbUtil.upsertBookingAudit(bookingId, firstname, lastname, totalprice, depositpaid, checkin, checkout);

        // 3) DB verification: ensure row exists
        assertTrue(DbUtil.bookingExists(bookingId), "Booking must exist in MySQL booking_audit table");

        // 4) API verification: GET booking and compare with DB
        String getBody =
                given()
                        .when()
                        .get("/booking/" + bookingId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        JsonNode apiBooking = MAPPER.readTree(getBody);

        DbUtil.BookingRow dbRow = DbUtil.getBookingRow(bookingId);
        assertNotNull(dbRow);

        assertEquals(dbRow.firstname(), apiBooking.get("firstname").asText());
        assertEquals(dbRow.lastname(), apiBooking.get("lastname").asText());
        assertEquals(dbRow.totalprice(), apiBooking.get("totalprice").asInt());
        assertEquals(dbRow.depositpaid(), apiBooking.get("depositpaid").asBoolean());
        assertEquals(dbRow.checkin().toString(), apiBooking.get("bookingdates").get("checkin").asText());
        assertEquals(dbRow.checkout().toString(), apiBooking.get("bookingdates").get("checkout").asText());
    }




}
