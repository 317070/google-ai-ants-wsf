
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class PlanManager {
    private static HashMap<Tile,Plan> goalplan = new HashMap<Tile,Plan>();
    private static ArrayList<Plan> planlist = new ArrayList<Plan>();
    
    public static void addPlan(Plan p){
        goalplan.put(p.getGoal(), p);
        planlist.add(p);
    }
    public static void cancelPlan(Plan p){
        goalplan.remove(p.getGoal());
        planlist.remove(p);
    }
}
