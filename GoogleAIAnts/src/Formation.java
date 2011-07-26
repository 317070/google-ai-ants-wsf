
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class Formation extends Plan implements HasTile{
    final static boolean[][] TARGET = {
        {true, true, true, true, true},
        {true,false,false,false, true},
        {true,false, true,false, true},
        {true,false,false,false, true},
        {true, true, true, true, true}
    };
    final static boolean[][] WRITE317070 = {
        {true ,true ,false,false, true,false,true ,true ,false,true,true ,true,false,true ,true ,false,true,true ,true},
        {false,false,true, false, true,false,false,true ,false,true,false,true,false,false,true ,false,true,false,true},
        {false,true ,true, false, true,false,false,true ,false,true,false,true,false,false,true ,false,true,false,true},
        {false,false,true, false, true,false,true ,false,false,true,false,true,false,true ,false,false,true,false,true},
        {true ,true ,false,false, true,false,true ,false,false,true,true ,true,false,true ,false,false,true,true ,true}
    };
    final static boolean[][] SMALLSQUARE = {
        {true, true},
        {true, true}
    };
    
    private HashMap<Delta,Ant> deltaantmap = new HashMap<Delta,Ant>();
    private HashMap<Ant,Delta> antdeltamap = new HashMap<Ant,Delta>();
    private ArrayList<Delta> deltalist = new ArrayList<Delta>();
    protected boolean formed = false;
    
    //create a formation on the leader with the form using these ants
    public Formation(List<Ant> lants, boolean[][] form, Tile leader){
        this.ants = new ArrayList<Ant>(lants);
        this.leader = leader;
        deltalist = generateDeltaList(form);
        this.ants = new ArrayList<Ant>(lants.subList(0, deltalist.size()));
    }

    protected Tile leader;
    
    @Override
    public boolean execute() {
        Logger.log("Build a formation at "+leader);
        //first, get the walking distances of the ants to the leader
        Logger.log("Looking for paths for ants "+ants.size());
        Logger.log("Spots for deltas "+deltalist.size());
        if(ants.size()!=deltalist.size()){
            cancel();
            return false;
        }
        ArrayList<IntAntPath> list = new ArrayList<IntAntPath>();
        for(Ant ant:ants){
            Path p = ant.getTile().shortestPath(leader);
            if(p==null){
                cancel();
                return false;
            }
            list.add(new IntAntPath(p.length(),ant,p));
        }
        Collections.sort(list, Collections.reverseOrder());
        //next, assign each Ant a location, beginning with the latest arriving ant
        for(IntAntPath iap:list){
            boolean found = false;
            for(Delta delta:deltalist){//ligt er een delta al op het pad
                if(iap.path.contains(delta.from(leader))){
                    if(!deltaantmap.containsKey(delta)){
                        deltaantmap.put(delta,iap.ant);
                        antdeltamap.put(iap.ant,delta);
                        found = true;
                        break;//doe verder met de volgende mier
                    }
                }
            }
            if(found)continue;//doe verder met de volgende mier
            
            for(Delta delta:deltalist){//zoek anders de delta die het dichtst bij ligt
                if(iap.path.contains(delta.from(leader))){
                    Delta minfreedelta = null;
                    int dist = Integer.MAX_VALUE;
                    for(Delta delta2:deltalist){
                        if(delta.getDistanceTo(delta2)<dist && !deltaantmap.containsKey(delta2)){
                            dist = delta.getDistanceTo(delta2);
                            minfreedelta = delta2;
                        }
                    }
                    deltaantmap.put(minfreedelta,iap.ant);
                    antdeltamap.put(iap.ant,minfreedelta);
                    found = true;
                    break;//doe verder met de volgende mier
                }
            }
            if(found)continue;//doe verder met de volgende mier
            
            for(Delta delta:deltalist){//neem de eerste de beste die nog overschiet
                if(!deltaantmap.containsKey(delta)){
                    deltaantmap.put(delta,iap.ant);
                    antdeltamap.put(iap.ant,delta);
                    found = true;
                    break;//doe verder met de volgende mier
                }
            }
        }
        
        //move all the ants to their corresponding places
        Logger.log("issuing roads");
        for(Delta delta:deltaantmap.keySet()){
            Ant ant = deltaantmap.get(delta);
            Logger.log("I want an ant at " + delta.from(leader));
            Path p = ant.getTile().shortestPath(delta.from(leader));
            if(p==null){
                cancel();
                return false;
            }
            ant.setPath(p);
        }
        Logger.log("Finished formation");
        return true;
    }

    public Tile getTile() {
        return leader;
    }

    private static ArrayList<Delta> generateDeltaList(boolean[][] form) {
        ArrayList<Delta> deltas = new ArrayList<Delta>();
        int ci = form.length/2;
        int cj = form[ci].length/2;
        for(int i=0; i<form.length;i++){
            for(int j=0; j<form[i].length;j++){
                if(form[i][j]){
                    Delta nd = new Delta(i-ci,j-cj);
                    deltas.add(nd);
                }
            }
        }
        return deltas;
    }
    
    private class IntAntPath implements Comparable<IntAntPath>{
        public int length;
        public Ant ant;
        public Path path;
        public IntAntPath(int l, Ant a, Path p){
            this.length=l;
            this.ant = a;
            this.path=p;
        }
        @Override
        public int compareTo(IntAntPath o) {
            return this.length - o.length;
        }
        
    }
    @Override
    public void cancel(){
        Logger.log("cancelling formation");
        super.cancel();
    }
    
    @Override
    public boolean update() {
        for(Ant a:ants){
            if(a.isDead()){
                cancel();
                return false;
            }
        }
        if(!formed){
            boolean busy= false;
            for(Ant a:ants){
                if(!GameData.isThisPathStillPassable(a.getPath())){
                    if(!a.updatePath()){
                        cancel();
                        return false;
                    }
                }
                if(a.hasPath()){
                    busy = true;
                }else{
                    a.setPath(a.getTile().shortestComplexPath(antdeltamap.get(a).from(leader)));                    
                }
            }
            if(!busy){
                this.formed = true;
                return true;
            }else{
                return false;
            }
        }
        return true;
    }
    
    protected void moveToTile(Tile g) {
        leader = g;
        form();
    }
    
    final private static class Delta{
        private int dc;
        private int dr;
        public Delta(int dr, int dc){
            this.dc = dc;
            this.dr = dr;
        }
        public Tile from(Tile t){
            return new Tile(t.getRow()+dr, t.getCol()+dc);
        }
        public int getDistanceTo(Delta d){
            return Math.abs(this.dc-d.dc) + Math.abs(this.dr-d.dr);
        }
    }
    
    public static boolean isPossible(Tile center, boolean[][] form){
        ArrayList<Delta> deltas = generateDeltaList(form);
        for(Delta delta:deltas){
            if(!GameData.isPassable(delta.from(center)))
                return false;
        }
        return true;
    }
    public boolean isPossible(Tile t){
        for(Delta delta:deltalist){
            if(!GameData.isPassable(delta.from(t)))
                return false;
        }
        return true;
    }
    public boolean isPossible(){
        return isPossible(leader);
    }
    public boolean isFormed(){
        for(Delta delta:deltalist){
            if(!delta.from(leader).equals(deltaantmap.get(delta).getTile()))
                return false;
        }
        return true;
    }
    
    public boolean form(){
        for(Ant ant:ants){
            Tile antleader = antdeltamap.get(ant).from(leader);
            //Logger.log("Ant at "+ant.getTile()+ " has a path?"+ant.hasPath());
            if(ant.hasPath()){
                if(!ant.getPath().getLastTile().equals(antleader)){
                    Path p = ant.getTile().shortestComplexPath(antleader);
                    if(p==null){
                        return false;
                    }
                    ant.setPath(p);
                }
            }else{
                if(!ant.getTile().equals(antleader)){
                    Path p = ant.getTile().shortestComplexPath(antleader);
                    if(p==null){
                        return false;
                    }
                    ant.setPath(p);
                }
            }
        }
        return true;
    }
    
}
