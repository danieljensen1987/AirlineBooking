package airlinebooking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Reservation {

    static Connection conn;

    public Reservation(String user, String pw) {
        try {
            conn = createConn(user, pw);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized int generateId() throws SQLException {
        Statement stmt = conn.createStatement();
        int newID = 1;
        try {
            ResultSet rs = stmt.executeQuery(
                    "Select reserved from (SELECT RESERVED FROM SEAT WHERE RESERVED IS NOT NULL ORDER BY RESERVED DESC) where rownum = 1");
            if (rs.next()) {
                newID = rs.getInt("RESERVED");
                newID++;
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return newID;
    }

    public Connection createConn(String user, String pw) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat", user, pw);
        } catch (SQLException e) {
            System.out.println("Connection error");
            e.printStackTrace();
            return connection;
        }
        if (connection != null) {
            System.out.println("Connected to database");
        } else {
            System.out.println("Connection error");
        }
        return connection;
    }

    public static synchronized String reserve(String plane_no, long id) throws SQLException {

        Statement stmt = conn.createStatement();
        String seatNumber = null;

        try {
            ResultSet rs = stmt.executeQuery("SELECT SEAT_NO FROM SEAT WHERE RESERVED is NULL AND BOOKED is NULL AND ROWNUM = 1");
            if (rs.next()) {
                seatNumber = rs.getString("SEAT_NO");
                Statement update = conn.createStatement();
                update.execute("UPDATE SEAT SET RESERVED = " + id + " WHERE SEAT_NO = '" + seatNumber + "'");
                update.execute("UPDATE SEAT SET BOOKING_TIME = " + System.currentTimeMillis() + " WHERE SEAT_NO = '" + seatNumber + "'");
                update.close();
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return seatNumber;
    }

    public static synchronized int book(String plane_no, String seat_no, long id) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT RESERVED, BOOKED, BOOKING_TIME FROM SEAT WHERE SEAT_NO = '" + seat_no + "'");
            if (rs.next()) {
                if (rs.getObject("BOOKED") != null) {
                    return -4;
                }
                if (rs.getObject("RESERVED") == null) {
                    return -1;
                }
                if ((System.currentTimeMillis() - rs.getLong("BOOKING_TIME")) > 5000) {
                    Statement update = conn.createStatement();
                    update.execute("UPDATE SEAT SET RESERVED = null WHERE SEAT_NO = '" + seat_no + "'");
                    return -3;
                }
                if ((int) id == rs.getInt("RESERVED")) {
                    Statement update = conn.createStatement();
                    update.execute("UPDATE SEAT SET BOOKED = " + id + " WHERE SEAT_NO = '" + seat_no + "'");
                    update.execute("UPDATE SEAT SET BOOKING_TIME = " + System.currentTimeMillis() + " WHERE SEAT_NO = '" + seat_no + "'");
                    update.close();
                    Stats.setSuccessBookings();
                    return 0;
                } else {
                    Stats.setBookingsWithoutReserv();
                    return -2;
                }
            } else {
                return -5;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return -5;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
