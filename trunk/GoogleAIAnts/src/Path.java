import java.util.ArrayList;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */

//Path is immutable!
public class Path {
    private ArrayList<Tile> path;
    
    public Path(Tile t){
        path = new ArrayList<Tile>();
        path.add(t);
    }
    public Path(Tile t, Aim aim){
        path = new ArrayList<Tile>();
        path.add(t);
        path.add(t.getTile(aim));
    }
    Path(Path p){
        path = new ArrayList<Tile>(p.path);
    }
    //Path is immutable!
    //behaves as a FILO-stack
    public Path push(Tile t){
        Path res = new Path(this);
        res.path.add(t);
        return res;
    }
    //Path is immutable!
    //behaves as a FILO-stack
    Path pop() {
        if(path.size()>2){
            Path res = new Path(this);
            res.path.remove(0);
            return res;
        }else{
            return null; //Path is finished
        }
    }
    
    ArrayList<Tile> getTiles(){
        return new ArrayList<Tile>(path);
    }
    
    Tile getCurrentTile() {
        return path.get(0);
    }
    
    Tile getNextTile() {
        if(path.size()>1){
            return path.get(1);
        }else{
            return getCurrentTile();
        }
    }

    Aim getNextAim() {
        if(!getNextTile().equals(path.get(0))){
            return getCurrentTile().getAimTo(getNextTile());
        }else{
            return null;
        }
    }
    
    Tile getTileOnTurn(int turn){
        return path.get(turn-GameData.currentturn());
    }
    //return if this is a stand-still path, the minimum-default path
    boolean hasSteps(){
        if(path.size() < 2)return false;
        if(path.size() > 2)return true;
        if(path.get(0).equals(path.get(1)))return false;
        return true;
    }
    
    public int length(){
        return path.size()-1;
    }
    
    boolean contains(Tile tile){
        return path.contains(tile);
    }
    
    int getDistance(){
        return path.size()-1;
    }

    Tile getLastTile() {
        return path.get(path.size()-1);
    }

    Path withoutLastTile() {
        Path res = new Path(this);
        res.path.remove(path.size()-1);
        if(res.path.size()==1){
            res.path.add(res.path.get(0));//blijf stilstaan
        }
        return res;
    }
}
