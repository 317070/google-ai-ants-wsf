
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {

    /**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args command line arguments
     * 
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        try {
            Logger.log("start");
            new MyBot().readSystemInput();
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    HashSet<Tile> taken = new HashSet<Tile>();
    @Override
    public void doTurn() {
        Logger.log("Started thinking about turn "+currentturn);
        
        int length = 0;
        HashMap<Ant,List<Tile>> missions = new HashMap<Ant,List<Tile>>();
        HashSet<Tile> checked = new HashSet<Tile>();
        for (Ant myAnt : GameData.getMyBusyAnts()) {
            List<Tile> goals = myAnt.getTile().getTilesUnderAWalkingDistance(10);
            length = goals.size(); // zijn toch allemaal even groot
            missions.put(myAnt, goals);
        }
        for (int i = 0; i<length; i++){
            for (Ant myAnt : GameData.getMyBusyAnts()){
                Tile goal = missions.get(myAnt).get(i);
                if(checked.contains(goal))continue;//check every tile only once
                checked.add(goal);
                if(taken.contains(goal))continue;
                boolean free = true;
                Ilk ilk = GameData.getIlk(goal);
                if(ilk == null)continue;//don't know, don't care
                if(ilk==Ilk.FOOD){
                    for (Ant otherAnt : GameData.getMyAnts()){
                        if(otherAnt.getPath().contains(goal)){
                            free = false;
                            break;
                        }
                    }
                    if(free){
                        //sounds like a plan :D
                        Path sp = myAnt.getTile().shortestPath(goal);
                        if(sp==null)continue; //er bestaat geen pad
                        sp = sp.withoutLastTile();
                        myAnt.setPath(sp);
                        taken.add(goal);
                    }
                }else if(ilk==Ilk.ENEMY_ANT && GameData.isVisible(goal)){
                    Path sp = myAnt.getTile().shortestPath(goal);
                    if(sp==null)continue; //er bestaat geen pad
                    myAnt.setPath(sp);
                    taken.add(goal);
                }                
            }
        }
        //nothing? Go explore
        for (Ant myAnt : GameData.getMyAnts()) {
            if(myAnt.getPath().hasSteps())continue;//hij is al bezig
            myAnt.fleeFromFriends();
        }
        Logger.log("finished thinking");
    }
}
