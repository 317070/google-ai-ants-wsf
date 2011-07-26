
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class GameData {
    private static int currentturn;   
    
    private static Map<Ilk> world; //best known map
    private static ArrayList<Map<Ilk>> worldhistory = new ArrayList<Map<Ilk>>(); //keep track of the history of our maps
    private static Map<Boolean> explored; //what have we already seen?
    private static ArrayList<Map<Boolean>> exploration = new ArrayList<Map<Boolean>>(); //keep track of what we have seen when?
    private static Map<Boolean> see; //what do we see at the moment?
    private static ArrayList<Map<Boolean>> certain = new ArrayList<Map<Boolean>>(); //keep track of what we saw at what time?
    
    private static ArrayList<Ant> myants = new ArrayList<Ant>(); // all my current ants
    private static ArrayList<Tile> enemyants = new ArrayList<Tile>(); //all enemy ants currently visible
    private static ArrayList<Tile> foodtiles = new ArrayList<Tile>(); //all tiles containing food
    private static ArrayList<Map<Ant>> plannedfuture = new ArrayList<Map<Ant>>();
    
    public static void init(){
        world = new Map<Ilk>();
        worldhistory.add(world);
        explored = new Map<Boolean>();
        exploration.add(new Map<Boolean>());
        see = new Map<Boolean>();
        certain.add(see);
    }
    
    public static void beginUpdate(){
        //make copies of our new situation which will come in at the update
        world = world.clone();
        worldhistory.add(world);
        explored = explored.clone();
        exploration.add(explored);
        see = see.clone();
        certain.add(see);
        
        for(Tile enemy:enemyants){
            world.set(enemy, Ilk.LAND);//don't remember these, they don't get updated by the engine
        }
        for(Tile food:foodtiles){
            world.set(food, Ilk.LAND);//don't remember these, they don't get updated by the engine
        }
        enemyants.clear();
        foodtiles.clear();
        
    }
    /**
     * Updates game state information about new ants and food locations.
     * 
     * @param ilk ilk to be updated
     * @param tile location on the game map to be updated
     */
    private static HashSet<Ant> found = new HashSet<Ant>();
    
    public static void update(Ilk ilk, Tile tile) {
        world.set(tile, ilk);
        switch (ilk) {
            case FOOD:
                if (!foodtiles.contains(tile)) {
                    foodtiles.add(tile);
                }
                break;
            case MY_ANT:
                boolean OK = false;
                for(Ant ant: myants){
                    if(ant.getTile().equals(tile)){
                        found.add(ant);//make sure it is the same ant, so it keeps its memory
                        OK = true;
                        break;
                    }
                }
                if(!OK){
                    Logger.log("een nieuwe ant gevonden op "+tile);
                    found.add(new Ant(tile));
                }
                if (foodtiles.contains(tile)) {
                    foodtiles.remove(tile);
                }
                break;
            case ENEMY_ANT:
                enemyants.add(tile);
                if (foodtiles.contains(tile)) {
                    foodtiles.remove(tile);
                }
                break;
        }
    }

    public static void finishedUpdate(){
        Logger.log("finishing update");
        myants.removeAll(found);
        for(Ant ant:myants){
            ant.die();
        }
        myants = new ArrayList<Ant>(found);
        found.clear();
        Timer.tic();
        updateVisibility();
        Logger.log("it took "+Timer.toc()+"ms to update visibility");
    }

    private static void updateVisibility() {
        for(int i=0;i<GameParam.rows;i++){
            for(int j=0;j<GameParam.cols;j++){
                see.set(i,j, false);
            }
        }
        for(Ant ant:myants){
            for(Tile t:ant.getTile().getTilesUnderAViewingDistance(GameParam.viewRadius2)){
                see.set(t, true);
                explored.set(t, true);
            }
        }
    }   
    
    static void removeFood(Tile tile) {
        if (world.get(tile) == Ilk.FOOD) {
            world.set(tile, Ilk.LAND);
        }
        foodtiles.remove(tile);
    }

    static ArrayList<Ant> getMyAnts() {
        return new ArrayList<Ant>(myants);
    }
    
    static ArrayList<Tile> getEnemyAnts() {
        return new ArrayList<Tile>(enemyants);
    }

    static int currentturn() {
        return currentturn;
    }
    static void setturn(int turn) {
        currentturn = turn;
    }

    static Ilk getIlk(Tile tile) {
        return world.get(tile);
    }

    static boolean isPassable(Tile tile) {
        if(world.get(tile)==null)return true;//if we don't know, let's assume we can pass
        return world.get(tile).isPassable();
    }
    
    static boolean isPassableOnTurn(Tile tile, int turn) {
        return isPassable(tile) && (null == GameData.isThereAnAntThereOnThisTurn(tile, turn));
    }
    
    static boolean isVisible(Tile goal) {
        return see.get(goal);
    }
    static boolean isExplored(Tile goal) {
        return explored.get(goal);
    }
    
    static Ant isThereAnAntThereOnThisTurn(Tile t, int turn){
        try{
            return plannedfuture.get(turn).get(t);
        }catch(IndexOutOfBoundsException e){//The map doesn't exist? There hasn't been any reservation...
            return null;
        }
    }
    
    static void reservePath(Ant a, Path p){
        //Logger.log("Reserving a path with "+p.length()+" tiles");
        int turn = currentturn;
        for(Tile step:p.getTiles()){
            if(currentturn!=turn){//Tile waarop je nu staat moet je niet reserveren, die is al gereserveerd
                while(plannedfuture.size()<=turn){
                    plannedfuture.add(new Map<Ant>());
                }
                //Logger.log("reserving a tile:"+step +" on turn "+turn);
                Ant oldval = plannedfuture.get(turn).set(step, a);
                if(oldval != null){
                    throw new RuntimeException("Reserving a tile which is already reserved:"+step +" on turn "+turn);
                }
            }
            turn++;
        }
    }
    
    static void cancelPath(Path p){
        //Logger.log("Cancelling a path with "+p.length()+" tiles");
        int turn = currentturn;
        for(Tile step:p.getTiles()){
            /*while(plannedfuture.size()<=turn){
                plannedfuture.add(new Map<Ant>());
            }*/
            if(currentturn!=turn){//Tile waarop je nu staat moet je niet annuleren, die blijft gereserveerd
                //Logger.log("cancelling a tile:"+step +" on turn "+turn);
                plannedfuture.get(turn).set(step, null);
            }
            turn++;
        }
    }
    
    //kijk of het pad nog steeds bewandelbaar is, dit kan veranderd zijn doordat we nu meer tiles zien.
    //houdt geen rekening met reservaties!
    static boolean isThisPathStillPassable(Path path){
        if(path==null)return true;
        for(Tile step:path.getTiles()){
            if(!isPassable(step)){
                return false;
            }
        }
        return true;
    }
}
