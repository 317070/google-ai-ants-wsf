
import java.util.ArrayList;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class AttackPlan extends Plan{
    
    public AttackPlan(Ant ant, Tile food){
        this.goal = food;
        this.ants.add(ant);
    }
    public AttackPlan(List<Ant> ants, Tile food){
        this.goal = food;
        this.ants.addAll(ants);
    }
    public void addAnt(Ant ant){
        this.ants.add(ant);
        Logger.log("added ant at "+ant.getTile()+ " to attackplan with goal "+goal);
    }
    @Override
    public boolean execute() {
        Logger.log("Executing plan to get enemy on tile "+goal);
        ArrayList<Ant> bin = new ArrayList<Ant>();
        for(Ant ant : ants){
            Path p = ant.getTile().shortestPath(goal);
            if(p==null){//geen pad mogelijk, sta deze beurt stil
                p = new Path(ant.getTile());
            }
            ant.setPath(p);
        }
        ants.removeAll(bin);
        return true;
    }

    @Override
    public void update() {
        Logger.log("Updating attackplan with goal "+goal);
        ArrayList<Ant> bin = new ArrayList<Ant>();
        for(Ant a:ants){
            if(a.isDead()){
                bin.add(a);
            }
        }
        ants.removeAll(bin);
        if(ants.isEmpty()){
            cancel();
        }
        if(!GameData.getIlk(goal).equals(Ilk.ENEMY_ANT)){
            Logger.log("The enemy moved!");
            for(Tile tile:goal.getBorderingTiles()){
                if(Ilk.ENEMY_ANT.equals(GameData.getIlk(tile)) && GameData.isVisible(tile)){
                    //cancel this plan and make a new plan going to the correct tile
                    Logger.log("Cancel this plan and make a new plan going to the correct tile on "+tile);
                    cancel();
                    AttackPlan np = new AttackPlan(ants,tile);
                    PlanManager.addPlan(np);
                    np.execute();
                    return;
                }
            }
            //hij heeft de vijand niet terug gevonden
            Logger.log("Couldn't find enemy ant back expected on "+goal);
            cancel();
        }
    }
}
