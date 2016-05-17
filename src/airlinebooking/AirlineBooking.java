package airlinebooking;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AirlineBooking {

    public static Stats stats;
    public final static AtomicBoolean db_full = new AtomicBoolean(false);

    public static void main(String[] args) throws SQLException {
        Reservation r = new Reservation("cphjp154", "cphjp154");
        stats = new Stats(0, 0, 0, 0, 0);

        while (true) {
            ExecutorService exe = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 10; i++) {
                Runnable user = new UserThread("" + i);
                exe.execute(user);

            }

            exe.shutdown();
            while (!exe.isTerminated()) {
            }
            System.out.println("DB full: " + Reservation.isAllBooked());
            if (Reservation.isAllBooked()) {
                System.out.println("ABANDON SHIP");
                break;
            }
        }

        System.out.println("All done!");
        System.out.println(Stats.twoString());

        //db.run();
    }
}
