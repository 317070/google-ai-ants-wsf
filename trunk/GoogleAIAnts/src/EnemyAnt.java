
import java.util.ArrayList;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class EnemyAnt implements HasTile{
    private Tile tile;
    private ArrayList<Tile> memory = new ArrayList<Tile>();
    private boolean lost;
    private int kind;
    
    public EnemyAnt(Tile t, int k){
        this.tile = t;
        this.kind = k;
        memory.add(t);
        lost = false;
    }
    
    public Tile getTile() {
        return tile;
    }
    public void update(Tile t){
        tile = t;
        memory.add(t);
    }
    public void lost(){
        lost = true;
    }
    public int getKind(){
        return kind;
    }
    public boolean isLost(){
        return lost;
    }
}
