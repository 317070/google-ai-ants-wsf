
/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class Ant {
    private Order order = null;
    private Tile tile;
    private Path path;

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
        Logger.log("next:"+path.getNextTile());
        Logger.log("goal:"+path.getLastTile());
    }
    
    public void setPath(Path path){
        this.path = path;
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

    void die() {
        Logger.log("died :(");
        this.setPath(new Path(tile));//remove the path
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
            if(tile.getDistanceTo(ant.getTile())<dist){
                dist = tile.getDistanceTo(ant.getTile());
                closestFriend = ant.getTile();
            }
        }
        
        if(closestFriend == null){
            for(Tile b : tile.getBorderingTiles()){
                if(!GameData.isPassable(b))continue;
                path = path.push(b);
            }
        }
        
        dist = 0;
        Tile besttile = null;
        for(Tile b : tile.getBorderingTiles()){
            if(!GameData.isPassable(b))continue;
            if(b.getDistanceTo(closestFriend)>=dist){
                dist = b.getDistanceTo(closestFriend);
                besttile = b;
            }
        }
        path = path.push(besttile); 
    }
    
}
