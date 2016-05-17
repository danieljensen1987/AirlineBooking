package airlinebooking;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AirlineBooking {
    public static Stats stats;    

    

    public static void main(String[] args) throws SQLException {
        Reservation r = new Reservation("cphdj74", "cphdj74");
        stats = new Stats(0, 0, 0, 0, 0);

        ExecutorService exe = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            Runnable user = new UserThread("" + i);
            exe.execute(user);
        }
        exe.shutdown();
        while(!exe.isTerminated()){}
        System.out.println("All done!");
        System.out.println(Stats.twoString());
        
        //db.run();
    }
}