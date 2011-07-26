
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class HunterFormation extends Formation{
    
    Path p = null;
    
    public HunterFormation(List<Ant> lants, boolean[][] form, Tile goal){
        super(lants, form, goal);
    }
    
    @Override
    public boolean update() {
        if(super.update()){
            ArrayList<Tile> enemy = GameData.getEnemyAnts();
            if(enemy.isEmpty())return true;
            Collections.sort(enemy, new Comparator<Tile>(){
                public int compare(Tile t1, Tile t2) {
                    return t1.getManhattenDistanceTo(goal) - t2.getManhattenDistanceTo(goal);
                }
            });
            Path p = goal.shortestPath(enemy.get(0));
            this.setStep(p.getNextAim());
            return true;
        }
        return false;
    }
    
}
