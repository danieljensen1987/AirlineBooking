package airlinebooking;

import static airlinebooking.Reservation.conn;
import static airlinebooking.Reservation.reserve;
import java.sql.Statement;
import java.util.Random;

public class UserThread implements Runnable {

    private String id;

    public UserThread(String s) {
        this.id = s;
    }

    @Override
    public void run() {
        try {
            Stats.setUserThreads();
            int id = Reservation.generateId();
            String seatNumber = reserve("CR9", id);
            Thread.sleep((long) (Math.random() * 10000) + 1000);
            if (new Random().nextInt(100) < 75) {
                System.out.println(Thread.currentThread().getName() + ": " + Reservation.book("CR9", seatNumber, id));
            } else {
                System.out.println(Thread.currentThread().getName() + ": Canceled");
                Statement update = conn.createStatement();
                update.execute("UPDATE SEAT SET RESERVED = null WHERE SEAT_NO = '" + seatNumber + "'");
                update.close();
                Stats.setIncorrectBookings();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return this.id;
    }

}
