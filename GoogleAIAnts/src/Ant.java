
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
final public class Ant {
    private Order order = null;
    private Tile tile;
    private Path path;
    private ArrayList<Tile> memory = new ArrayList<Tile>();
    private boolean dead = false;
    
    Ant(Tile tile) {
        this.tile = tile;
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
    public void setMinimalPath(){
        setPath(getMinimalPath());
    }
    public Path getPath(){
        return path;
    }
    public boolean hasPath(){
        return path!=null;
    }
    public void update(){
        if(isDead())return;
        if(path==null){
            Logger.log("Ant on "+tile+" has no further path. He's wondering around");
            setMinimalPath();//een mier gaat als hij geen pad meer heeft, vanzelf niets meer doen, hij gaat echter ook niet in de weg lopen.
        }
        if(isDead())return;
        if(GameData.isPassable(path.getNextTile())){
            goIntoDirection(path.getNextAim());
            tile = path.getNextTile();
            path = path.pop();
        }else{
            cancel();
        }
    }

    Tile getTile() {
        return tile;
    }
    
    void cancel(){
        if(dead)return;
        setPath(null);
    }
    
    void die() {
        dead = true;
        Logger.log("died :( "+tile);
        this.setPath(null);
        this.tile = null;
    }
    
    boolean equals(Ant a){
        return this.tile.equals(a.tile);
    }

    boolean isBusy() {
        return path.hasSteps();
    }

    boolean isDead() {
        return dead;
    }
    
    public void fleeFromFriends(){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
        Path p = new Path(tile);
        // 1) zoek de dichtste mier
        for(Ant ant: GameData.getMyAnts()){
            if(ant.equals(this))continue;
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
    
    public void fleeToFriends(){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
        Path p = new Path(tile);
        // 1) zoek de dichtste mier
        for(Ant ant: GameData.getMyAnts()){
            if(ant.equals(this))continue;
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
    
    private Path getMinimalPath(){
        int nextturn = GameData.currentturn()+1;
        Path p = new Path(tile); 
        if(GameData.isPassableOnTurn(tile, nextturn)){
            p = p.push(tile);
            return p;//initieel plan = stilstaan
        }else{
            for(Tile t:tile.getPassableBorderingTilesOnTurn(nextturn)){
                p = p.push(t);
                return p;//anders, buur
            }
        }
        Logger.log("Ant at "+tile+" has nowhere to go...");
        //p = p.push(tile);//sta stil
        //return p; //this is already a dead ant
        dead = true;//this is already a dead ant
        return null;
    }

    public boolean updatePath() {
        Path p = getTile().shortestPath(getPath().getLastTile());
        setPath(p);
        return (p!=null);
    }
    
}
