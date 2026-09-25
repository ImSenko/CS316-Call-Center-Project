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
        arrLock.newCondition();

    //Adding customer to greeting que
    public static void addgreet(int customerID){
        arrLock.lock();
        try{
            arrq.add(customerID);
            arrqNotEmpty.signal();
            System.out.println("Customer " +customerID+ " is being greeted!");

        }finally{
            arrLock.unlock();
        }
    }
    //Adding customer to service que and removing from greeting que
    public static void addserv(int customerID){
        int customerID;
        serLock.lock();
        try{
            while(serq.isEmpty()){
                serqNotEmpty.await();
            }
            customerID = arrq.remove();
        }finally{
            serLock.unlock();
        }
    }
    public static int takecall() throws Exception{
        int customerID;
        serLock.lock();
        try{
            while(serq.isEmpty()){
                serqNotEmpty.await();
            }
            customerID = serq.remove();
        }finally{
            serLock.unlock();
        }
        return customerID;

    }
public static void main(String[] args) throws Exception{
    ExecutorService agentPool = Executors.newFixedThreadPool(totalAgents);
    ExecutorService customerPool = Executors.newCachedThreadPool();
    agentPool.submit(new Greeter());
    for(int i=1; i<=totalAgents; i++){
        agentPool.submit(new Agent(i));
        Thread.sleep(ThreadLocalRandom.current().nextInt());
    }
    for(int x=1; x<= totalCustomers; x++){
        customerPool.submit(new Customer(x));
        Thread.sleep(ThreadLocalRandom.current().nextInt(10,100));
    }
    customerPool.shutdown();    
    agentPool.shutdown();
}
}
    

