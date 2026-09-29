import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Queue;


public class CallCenter {
    public final static int totalCustomers = 30;
    public final static int totalAgents = 3;

    //Making greeting que and lock
    private final static Queue<Integer> arrq =
            new LinkedList<>();
    private final static ReentrantLock arrLock =
            new ReentrantLock();
    private final static Condition arrqNotEmpty =
            arrLock.newCondition();

    //Making service que and lock
    private final static Queue<Integer> serq =
            new LinkedList<>();
    private final static ReentrantLock serLock =
            new ReentrantLock();
    private final static Condition serqNotEmpty =
            serLock.newCondition();

    //Adding customer to greeting que
    public static void arrq(int customerID){
        arrLock.lock();
        try{
            arrq.add(customerID);
            arrqNotEmpty.signal();
            System.out.println("Customer " +customerID+ " entered the arrival queue.");

        }finally{
            arrLock.unlock();
        }
    }

    public static int takegreet() throws InterruptedException{
        arrLock.lock();
        try{
            while(arrq.isEmpty()){
                arrqNotEmpty.await();
            }
            return arrq.remove();
        }finally{
            arrLock.unlock();
        }
    }
    //Adding customer to service que
    public static int addserv(int customerID){
        serLock.lock();
        try{
            serq.add(customerID);
            int position = serq.size();
            serqNotEmpty.signal();
            return position;
        }finally{
            serLock.unlock();
        }
    }
    public static int takeCall() throws InterruptedException{
        serLock.lock();
        try{
            while(serq.isEmpty()){
                serqNotEmpty.await();
            }
            return serq.remove();
        }finally{
            serLock.unlock();
        }
    }
    public static void main(String[] args) throws Exception{
        //for the totalAgent+1: Thread1->Greeter Thread2->Agent1 Thread3->2 Thread4->Agent3
        ExecutorService agentPool = Executors.newFixedThreadPool(totalAgents+1);
        ExecutorService customerPool = Executors.newCachedThreadPool();
        agentPool.submit(new Greeter());
        for(int i=1; i<=totalAgents; i++){
            agentPool.submit(new Agent(i));
        }
        for(int x=1; x<= totalCustomers; x++){
            customerPool.submit(new Customer(x));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10,101));
        }
        customerPool.shutdown();
        agentPool.shutdown();
    }
}



