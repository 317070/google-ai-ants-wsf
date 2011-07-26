
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
    public static boolean hasGoal(Tile goal){
        return goalplan.containsKey(goal);
    }
    public static void update(){
        ArrayList<Plan> copy = new ArrayList<Plan>(planlist);
        for(Plan plan: copy){//planlist zelf kan veranderen in de forloop, nieuwe plannen worden nog niet ge-updatet
            plan.update();
        }
    }
    
    //don't call this method, use Plan.cancel() instead
    static void cancelPlan(Plan p){
        goalplan.remove(p.getGoal());
        planlist.remove(p);
    }
    
    //don't call this method, use Plan.finish() instead
    //this might someday do something else than just cancelling the plan.
    static void finishPlan(Plan p){
        cancelPlan(p);
    }

    static Plan getPlan(Tile goal) {
        return goalplan.get(goal);
    }
    
    //TODO: too slow for what it should do at the moment, keep this information stored somehow
    static boolean isBusy(Ant a){
        for(Plan p:planlist){
            if(p.ants.contains(a))
                return true;
        }
        return false;
    }
    
    static ArrayList<Ant> getAvailableAnts(){
        ArrayList<Ant> res = new ArrayList<Ant>(GameData.getMyAnts());
        ArrayList<Ant> bin = new ArrayList<Ant>();
        for(Ant a:res){
            if(isBusy(a))
                bin.add(a);
        }
        res.removeAll(bin);
        return res;
    }
}
