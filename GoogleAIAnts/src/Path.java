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
    private Path(Path p){
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
        if(path.size()>1){
            Path res = new Path(this);
            res.path.remove(0);
            return res;
        }else{
            return this;
        }
    }
    
    Tile getNextTile() {
        if(path.size()>1){
            return path.get(1);
        }else{
            return path.get(0);
        }
    }

    Aim getNextAim() {
        if(!getNextTile().equals(path.get(0))){
            return path.get(0).getAimTo(getNextTile());
        }else{
            return null;
        }
    }
    
    Tile getTileOnTurn(int turn){
        return path.get(turn-GameData.currentturn());
    }
    
    boolean hasSteps(){
        return path.size()>1;
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
        return res;
    }
}
