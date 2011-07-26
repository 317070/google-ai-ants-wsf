
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
final public class Ant implements HasTile {

    private Order order = null;
    private Tile tile;
    private Path path;
    private ArrayList<Tile> memory = new ArrayList<Tile>();
    private boolean dead = false;
    
    Ant(Tile tile) {
        this.tile = tile;
        memory.add(tile);
        path = null;
    }
    
    private void goIntoDirection(Aim aim){
        if(order != null){
            order.cancel();
        }
        if(aim == null){
            return;//no order
        }
        order = new Order(tile, aim);
        OrderManager.addOrder(order);
        memory.add(tile);
    }
    
    public void setPath(Path path){
        if(this.path != null){
            GameData.cancelPath(this.path);//remove the old path
        }
        this.path = path;
        if(path != null){
            Logger.log("ant at "+tile+" has the goal "+path.getLastTile());
            GameData.reservePath(this, path);
        }
    }
    public Path getPath(){
        return path;
    }
    public boolean hasPath(){
        return path!=null;
    }
    
    public static void checkAllMyAntsForPaths(){
        for(Ant a:GameData.getMyAnts()){
           a.checkPath();
        }
    }
    public void checkPath(){
        if(isDead())return;
        if(path==null){
            Logger.log("Ant on "+tile+" has no further path. He's wondering around");
            setMinimalPath();//een mier gaat als hij geen pad meer heeft, vanzelf niets meer doen, hij gaat echter ook niet in de weg lopen.
        }
    }
    
    static void updateAllMyAnts() {
        for(Ant ant:GameData.getMyAnts()){
            ant.update();
        }
    }
    public void update(){
        if(isDead())return;
        if(GameData.isPassable(path.getNextTile())){
            goIntoDirection(path.getNextAim());
            tile = path.getNextTile();
            path = path.pop();
        }else{
            //throw new RuntimeException("The ant at "+this.getTile()+ " is trying to walk on water/food");
        }
    }

    public Tile getTile() {
        return tile;
    }
    
    void cancel(){
        if(dead)return;
        Logger.log("Cancelling path of ant at "+tile);
        setPath(null);
    }
    
    void die() {
        dead = true;
        Logger.log("died :( "+tile);
        this.setPath(null);
        this.tile = null;
    }
    
    public boolean equals(Ant a){
        return this.tile.equals(a.tile);
    }
    
    @Override
    public int hashCode(){
        return memory.get(0).hashCode();
    }
    
    boolean isBusy() {
        return path.hasSteps();
    }

    boolean isDead() {
        return dead;
    }
    
    public void fleeFrom(ArrayList<HasTile> list){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
        Path p = new Path(tile);
        // 1) zoek de dichtste mier
        for(HasTile ant: list){
            if(this.getTile().equals(ant.getTile()))continue;//je moet niet van jezelf wegvluchten
            if(tile.getEuclidDistanceTo(ant.getTile())<dist){
                dist = tile.getEuclidDistanceTo(ant.getTile());
                closestFriend = ant.getTile();
            }
        }
        
        // geen vriend, willekeurige richting
        if(closestFriend == null){
            for(Tile b : tile.getPassableBorderingTilesOnTurn(GameData.currentturn()+1)){
                setPath(p.push(b));
                return;
            }
        }
        
        // 2) ga daar zo ver mogelijk van
        dist = 0;
        Tile besttile = null;
        List<Tile> bordertiles = tile.getPassableBorderingTilesOnTurn(GameData.currentturn()+1);
        Collections.shuffle(bordertiles);
        for(Tile b : bordertiles){
            if(memory.contains(b))continue;
            if(b.getEuclidDistanceTo(closestFriend)>=dist){
                dist = b.getEuclidDistanceTo(closestFriend);
                besttile = b;
            }
        }
        if(besttile != null){
            setPath(p.push(besttile));
            return;
        }else{
            for(Tile b : bordertiles){
                if(GameData.isPassable(b)){
                    setPath(p.push(b));
                    return;
                }
            } 
        }
        path = null;
    }
    
    public void fleeToFriends(ArrayList<HasTile> list){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
        Path p = new Path(tile);
        // 1) zoek de dichtste mier
        for(HasTile ant: list){
            if(ant.getTile().equals(this.getTile()))continue;
            if(tile.getEuclidDistanceTo(ant.getTile())<dist){
                dist = tile.getEuclidDistanceTo(ant.getTile());
                closestFriend = ant.getTile();
            }
        }
        
        // geen vriend, willekeurige richting
        if(closestFriend == null){
            for(Tile b : tile.getPassableBorderingTilesOnTurn(GameData.currentturn()+1)){
                setPath(p.push(b));
                return;
            }
        }
        
        // 2) ga daar zo dicht mogelijk bij
        dist = Integer.MAX_VALUE;
        Tile besttile = null;
        List<Tile> bordertiles = tile.getPassableBorderingTilesOnTurn(GameData.currentturn()+1);
        Collections.shuffle(bordertiles);
        for(Tile b : bordertiles){
            if(memory.contains(b))continue;
            if(b.getEuclidDistanceTo(closestFriend)<=dist){
                dist = b.getEuclidDistanceTo(closestFriend);
                besttile = b;
            }
        }
        if(besttile != null){
            setPath(p.push(besttile));
            return;
        }else{
            for(Tile b : bordertiles){
                if(GameData.isPassableOnTurn(b, GameData.currentturn()+1)){
                    setPath(p.push(b));
                    return;
                }
            } 
        }
        path=null;
    }
    private static boolean nowheretogo = false;
    public void setMinimalPath(){
        nowheretogo = false;
        int nextturn = GameData.currentturn()+1;
        Path p = new Path(tile); 
        if(GameData.isPassableOnTurn(tile, nextturn)){
            p = p.push(tile);
            setPath(p);//anders, buur
            return;
        }else{
            for(Tile t:tile.getPassableBorderingTilesOnTurn(nextturn)){
                p = p.push(t);
                setPath(p);//anders, buur
                return;
            }
        }
        Logger.log("Ant at "+tile+" has nowhere to go...");
        nowheretogo=true;
        //probeer te bewegen i.p.v. stil te staan -> geen deadlocks!
        for(Tile t:tile.getPassableBorderingTiles()){
            Ant a = GameData.isThereAnAntThereOnThisTurn(t, nextturn);
            if(a.nowheretogo)continue;//zorg ervoor dat je binnen deze beurt iedere ant hoogstens 1 keer annuleert
            a.cancel();//smijt zijn pad weg
            p = p.push(t);
            setPath(p);//beweeg naar zijn plaats
            a.setMinimalPath();//geef hem een nieuw pad
            return;
        }
        //p = p.push(tile);//sta stil
        //return p; //this is already a dead ant
        Ant a = GameData.isThereAnAntThereOnThisTurn(tile, nextturn);
        a.cancel();//smijt zijn pad weg
        p = p.push(tile);
        setPath(p);//sta stil
        a.setMinimalPath();//geef hem een nieuw pad
        return;
    }

    public boolean updatePath() {
        Path p = getTile().shortestPath(getPath().getLastTile());
        setPath(p);
        return (p!=null);
    }
    
}
