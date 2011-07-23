
import java.util.ArrayList;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class OrderManager {
    private final static ArrayList<Order> orders = new ArrayList<Order>();
    
    public static void addOrder(Order o){
        orders.add(o);
    }
    
    public static void issueAllOrders(){
        for(Order ord:orders){
            System.out.println(ord);
        }
        System.out.println("go");
        System.out.flush();
        orders.clear();
        check();
    }

    static void cancel(Order order) {
        orders.remove(order);
    }
    
    static private void check(){
        if(!Logger.isLogging())return;
        for(Order o1:orders){
            for(Order o2:orders){
                if(o2 == o1)continue;
                if(o1.getTarget().equals(o2.getTarget())){
                    throw new RuntimeException( "SUICIDE WATCH NOTICED "+ o1 + o2);
                }
                if(o1.getFrom().equals(o2.getFrom())){
                    throw new RuntimeException( "DUBBLE ORDER NOTICED "+ o1 + o2);
                }
            }
        }
    }
}
