import java.util.concurrent.ThreadLocalRandom;

public class Greeter implements Runnable {

    @Override
    public void run() {

        for (int i = 0; i < CallCenter.totalCustomers; i++) {

            try {

                int customerID = CallCenter.takegreet();

                Thread.sleep(
                        ThreadLocalRandom.current().nextInt(20, 201)
                );

                int position = CallCenter.addserv(customerID);

                System.out.println(
                        "Customer " + customerID
                                + " finished greeting and is position "
                                + position
                                + " in the service queue."
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}