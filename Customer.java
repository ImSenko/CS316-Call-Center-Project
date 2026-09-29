public class Customer implements Runnable{
    private final int ID;

    public Customer(int ID){
        this.ID = ID;
    }
    public void run(){
        CallCenter.arrq(ID);
    }
}