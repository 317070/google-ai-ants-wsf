
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
    
    Ant(Tile tile) {
        this.tile = tile;
        this.path = new Path(tile);
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
        this.setPath(new Path(tile));//remove the path
    }
    
    void die() {
        Logger.log("died :( "+tile);
        cancel();
        this.tile = null;
    }
    
    boolean equals(Ant a){
        return this.tile.equals(a.tile);
    }
    
    public void fleeFromFriends(){
        Tile closestFriend = null;
        int dist = Integer.MAX_VALUE;
        for(Ant ant: GameData.getMyAnts()){
            if(ant.equals(this))continue;
            if(tile.getEuclidDistanceTo(ant.getTile())<dist){
                dist = tile.getEuclidDistanceTo(ant.getTile());
                closestFriend = ant.getTile();
            }
        }
        
        if(closestFriend == null){
            for(Tile b : tile.getPassableBorderingTiles()){
                path = path.push(b);
                return;
            }
        }
        
        dist = 0;
        Tile besttile = null;
        List<Tile> bordertiles = tile.getPassableBorderingTiles();
        Collections.shuffle(bordertiles);
        for(Tile b : bordertiles){
            if(memory.contains(b))continue;
            if(b.getEuclidDistanceTo(closestFriend)>=dist){
                dist = b.getEuclidDistanceTo(closestFriend);
                besttile = b;
            }
        }
        if(besttile != null){
            path = path.push(besttile);
            return;
        }else{
            for(Tile b : bordertiles){
                if(GameData.isPassable(b)){
                    path = path.push(b);
                    return;
                }
            } 
        }
    }

    boolean isBusy() {
        return path.hasSteps();
    }
    
}
