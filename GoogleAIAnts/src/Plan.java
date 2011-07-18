
import java.util.ArrayList;
import java.util.List;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class Plan {
    private Tile goal;
    private ArrayList<Ant> ants = new ArrayList<Ant>();
    public Tile getGoal(){
        return goal;
    }
    public List<Ant> getAnts(){
        return ants;
    }
}
