
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
        //TODO: do some checks to remove stupid mistakes, like 2 ants going to 1 tile
        for(Order ord:orders){
            System.out.println(ord);
        }
        System.out.println("go");
        System.out.flush();
        orders.clear();
    }

    static void cancel(Order order) {
        orders.remove(order);
    }
}
