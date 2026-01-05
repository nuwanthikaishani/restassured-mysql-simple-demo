package demo;

import java.sql.*;
import java.time.LocalDate;

public class DbUtil {

    // Change these 3 values for your MySQL
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/restassured_demo?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root"; // change

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASS);
    }

    public static void upsertBookingAudit(int bookingId,
                                          String firstname,
                                          String lastname,
                                          int totalprice,
                                          boolean depositpaid,
                                          LocalDate checkin,
                                          LocalDate checkout) throws SQLException{

        String sql = """
                INSERT INTO booking_audit
                  (booking_id, firstname, lastname, totalprice, depositpaid, checkin, checkout)
                VALUES
                  (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                  firstname = VALUES(firstname),
                  lastname = VALUES(lastname),
                  totalprice = VALUES(totalprice),
                  depositpaid = VALUES(depositpaid),
                  checkin = VALUES(checkin),
                  checkout = VALUES(checkout)
                """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setString(2, firstname);
            ps.setString(3, lastname);
            ps.setInt(4, totalprice);
            ps.setBoolean(5, depositpaid);
            ps.setDate(6, Date.valueOf(checkin));
            ps.setDate(7, Date.valueOf(checkout));

            ps.executeUpdate();
        }

    }

    public static boolean bookingExists(int bookingId) throws SQLException{
        String sql = "SELECT 1 FROM booking_audit WHERE booking_id = ? LIMIT 1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static BookingRow getBookingRow(int bookingId) throws SQLException {
        String sql = """
                SELECT booking_id, firstname, lastname, totalprice, depositpaid, checkin, checkout
                FROM booking_audit
                WHERE booking_id = ?
                """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new BookingRow(
                        rs.getInt("booking_id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getInt("totalprice"),
                        rs.getBoolean("depositpaid"),
                        rs.getDate("checkin").toLocalDate(),
                        rs.getDate("checkout").toLocalDate()
                );
            }
        }
    }

    public record BookingRow(int bookingId, String firstname, String lastname, int totalprice,
                             boolean depositpaid, LocalDate checkin, LocalDate checkout) {}
}


