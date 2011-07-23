
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
        setPath(new Path(tile));
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
        this.path = path;
        Logger.log("ant at "+tile+" has the goal "+path.getLastTile());
        GameData.reservePath(this, path);
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
            this.path = new Path(tile);
        }
    }

    Tile getTile() {
        return tile;
    }
    
    void cancel(){
        if(isDead())return;
        GameData.cancelPath(this, path);
        this.setPath(new Path(tile));//remove the path
    }
    
    void die() {
        Logger.log("died :( "+tile);
        cancel();
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
                setPath(path.push(b));
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
            setPath(path.push(besttile));
            return;
        }else{
            for(Tile b : bordertiles){
                if(GameData.isPassable(b)){
                    setPath(path = path.push(b));
                    return;
                }
            } 
        }
    }
    
    public void fleeToFriends(){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
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
                setPath(path.push(b));
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
            setPath(path.push(besttile));
            return;
        }else{
            for(Tile b : bordertiles){
                if(GameData.isPassable(b)){
                    setPath(path = path.push(b));
                    return;
                }
            } 
        }
    }
    
}
