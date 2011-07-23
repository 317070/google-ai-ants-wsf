
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class Ant {
    private Order order = null;
    private Tile tile;
    private Path path;
    private ArrayList<Tile> memory = new ArrayList<Tile>();
    private boolean dead = false;
    
    Ant(Tile tile) {
        this.tile = tile;
        setPath(getMinimalPath());//initieel plan = stilstaan
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
            GameData.cancelPath(this, this.path);//remove the old path
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
    
    public void update(){
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
        if(isDead())return;
        setPath(getMinimalPath());//sta stil
    }
    
    void die() {
        Logger.log("died :( "+tile);
        this.setPath(null);
        this.tile = null;
        dead = true;
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
        setPath(getMinimalPath());
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
        setPath(getMinimalPath());
    }
    
    public Path getMinimalPath(){
        int nextturn = GameData.currentturn()+1;
        Path p = new Path(tile); //initieel plan = stilstaan
        if(GameData.isPassableOnTurn(tile, nextturn)){
            p = p.push(tile);
            return p;
        }else{
            for(Tile t:tile.getPassableBorderingTilesOnTurn(nextturn)){
                p = p.push(t);
                return p;
            }
        }
        Logger.log("Ant at "+tile+" has nowhere to go...");
        return null; //this is already a dead ant
    }
    
}
