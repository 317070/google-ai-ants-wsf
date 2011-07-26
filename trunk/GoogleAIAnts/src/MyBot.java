
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        }catch (Exception e) {
            Logger.log(e);
        }finally{
            Logger.log("ties gedaan");
            Logger.close();
        }
    }

    /**
     * Here, the actual macro-scale logic happens
     */
    HashSet<Tile> taken = new HashSet<Tile>();
    @Override
    public void doTurn() {
        Logger.log("");
        Logger.log("*******************************");
        Logger.log("Started thinking about turn "+GameData.currentturn());
        Logger.log("*******************************");
        Logger.log(" Action:");
        Logger.log("---------");
        PlanManager.update();//make all plans execute update();
        
        int length = 0;
        HashMap<Ant,List<Tile>> missions = new HashMap<Ant,List<Tile>>();
        HashSet<Tile> checked = new HashSet<Tile>();
        for (Ant myAnt : PlanManager.getAvailableAnts()) {
            List<Tile> goals = myAnt.getTile().getTilesUnderAWalkingDistance(10);
            length = goals.size(); // zijn toch allemaal even groot
            missions.put(myAnt, goals);
        }
        for (int i = 0; i<length; i++){
            for (Ant myAnt : PlanManager.getAvailableAnts()){
                Tile goal = missions.get(myAnt).get(i);
                if(checked.contains(goal))continue;//check every tile only once
                checked.add(goal);
                if(taken.contains(goal))continue;
                Ilk ilk = GameData.getIlk(goal);
                if(ilk == null)continue;//don't know what this tile is, so don't care
                if(ilk==Ilk.FOOD){
                    if(!PlanManager.hasGoal(goal)){
                        //sounds like a plan :D
                        Plan plan = new FoodPlan(myAnt, goal);
                        PlanManager.addPlan(plan);
                        plan.execute();
                    }
                }else if(ilk==Ilk.ENEMY_ANT && GameData.isVisible(goal)){
                    if(PlanManager.getAvailableAnts().size()>50 && GameData.currentturn()>150){
                        if(!PlanManager.hasGoal(goal)){
                            Plan plan = new AttackPlan(myAnt,goal);
                            PlanManager.addPlan(plan);
                            plan.execute();
                        }else{
                            Plan plan = PlanManager.getPlan(goal);
                            if(plan instanceof AttackPlan){
                                AttackPlan att = (AttackPlan) plan;
                                att.addAnt(myAnt);
                            }else{
                                Logger.log("(Mybot) This is a wrong kind of plan:"+plan);
                            }
                        }
                    }
                }                
            }
        }
        //nothing? Go explore
        ArrayList<Ant> freeants = PlanManager.getAvailableAnts();
        while(freeants.size()>=20){
            Tile goal = freeants.get(0).getTile();
            boolean[][] form = Formation.SMALLSQUARE;
            final Tile fgoal = goal;
            
            Collections.sort(freeants,new Comparator<Ant>(){
                public int compare(Ant a1, Ant a2) {
                    return a1.getTile().getManhattenDistanceTo(fgoal) - a2.getTile().getManhattenDistanceTo(fgoal);
                }
            });
            
            
            if(Formation.isPossible(goal,form)){
                HunterFormation plan = new HunterFormation(freeants,form,goal);
                PlanManager.addPlan(plan);
                plan.execute();
                freeants = new ArrayList<Ant>(freeants.subList(4, freeants.size()-1));
            }else{
                freeants.remove(0);
            }
        }
        ArrayList<HasTile> creeps = new ArrayList<HasTile>(GameData.getMyAnts());
        if(PlanManager.getAvailableAnts().size()<50 || GameData.currentturn()<75){
            creeps.addAll(GameData.getEnemyAnts());
        }
        
        for (Ant myAnt : PlanManager.getAvailableAnts()) {
            myAnt.fleeFrom(creeps);
        }
        Logger.log("");
        Logger.log(" Result:");
        Logger.log("---------");
        Logger.flush();
    }
}
