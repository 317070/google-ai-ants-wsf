
import java.util.ArrayList;
import java.util.List;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public abstract class Plan {
    protected Tile goal;
    protected ArrayList<Ant> ants = new ArrayList<Ant>();
    protected int priority;
    
    public Tile getGoal(){
        return goal;
    }
    public List<Ant> getAnts(){
        return ants;
    }
    protected int getPriority(){
        return priority;
    }
    public boolean hasHigherPriorityThan(Plan p){
        return this.priority > p.getPriority();
    }
    
    public void cancel(){
        Logger.log("Cancelling plan");
        for(Ant a:ants){
            a.cancel();
        }
        PlanManager.cancelPlan(this);
    }
    //this get's executed once on the start of the execution of the plan
    //returns whether execution is possible
    abstract public boolean execute();
    //this get's executed every turn until the plan removes itself from the planpool
    abstract public boolean update();
    
    protected void finish(){
        cancel();
    }
}
