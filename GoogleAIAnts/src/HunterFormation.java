
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class HunterFormation extends Formation{
    
    public HunterFormation(List<Ant> lants, boolean[][] form, Tile leader){
        super(lants, form, leader);
    }
    
    @Override
    public boolean update() {
        if(super.update()){
            if(!isFormed() && isPossible()){
                form();
            }else{
                ArrayList<EnemyAnt> enemy = GameData.getEnemyAnts();
                if(enemy.isEmpty())return true;
                Collections.sort(enemy, new Comparator<EnemyAnt>(){
                    public int compare(EnemyAnt t1, EnemyAnt t2) {
                        return t1.getTile().getManhattenDistanceTo(leader) - t2.getTile().getManhattenDistanceTo(leader);
                    }
                });
                Path p = leader.shortestPath(enemy.get(0).getTile());
                if(p==null){
                    return true;
                }
                for(Tile t:p.getTiles()){
                    if(t.equals(leader))continue;
                    if(isPossible(t)){
                        this.moveToTile(t);//ga naar de eerste mogelijke tile
                        return true;
                    }
                }
                this.moveToTile(leader.getTile(p.getNextAim()));
                return true;
            }
        }
        return false;
    }
    
}
