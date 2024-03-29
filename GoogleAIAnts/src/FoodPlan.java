/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class FoodPlan extends Plan{
    public FoodPlan(Ant ant, Tile food){
        this.goal = food;
        this.ants.add(ant);
    }
    
    @Override
    public boolean execute(){
        Logger.log("Executing plan to get food on tile "+goal);
        Path p = ants.get(0).getTile().shortestPath(goal);
        if(p==null){
            cancel();
            return false;
        }
        ants.get(0).setPath(p);
        return true;
    }
    
    @Override
    public boolean update(){
        if(ants.get(0).isDead()){
            this.ants.remove(0);
            cancel();
            return false;
        }
        if( !ants.get(0).hasPath()){
            Logger.log("foodplan finished with goal "+goal);
            finish();
            return true;
        }
        if(! GameData.isThisPathStillPassable(ants.get(0).getPath())){
            Path p = ants.get(0).getTile().shortestPath(goal);
            if(p==null){
                cancel();
            }
            ants.get(0).setPath(p);
        }
        return false;
    }
}
