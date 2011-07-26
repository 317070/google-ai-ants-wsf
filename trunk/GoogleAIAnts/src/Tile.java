
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Represents a tile of the game map.
 * 
 * IMMUTABLE!
 */
public class Tile  implements HasTile{

    private final int row;
    private final int col;

    /**
     * Creates new {@link Tile} object.
     * 
     * @param row row index
     * @param col column index
     */
    public Tile(int row, int col) {
        row = row % GameParam.rows;
        col = col % GameParam.cols;
        if (row < 0) {
            row += GameParam.rows;
        }
        if (col < 0) {
            col += GameParam.cols;
        }
        this.row = row;
        this.col = col;
    }
    
    Tile getTile(Aim aim){
        if(aim==null)return this;
        int mrow = this.row + aim.getRowDelta();
        int mcol = this.col + aim.getColDelta();
        return new Tile(mrow,mcol);
    }

    /**
     * Calculates Euclides^2 distance between two locations on the game map.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getEuclidDistanceTo(Tile tile) {
        int rowDelta = Math.abs(this.getRow() - tile.getRow());
        int colDelta = Math.abs(this.getCol() - tile.getCol());
        rowDelta = Math.min(rowDelta, GameParam.rows - rowDelta);
        colDelta = Math.min(colDelta, GameParam.cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }
    
    /**
     * Calculates Manhatten distance between two locations on the game map.
     * (minimum number of steps)
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getManhattenDistanceTo(Tile tile) {
        int rowDelta = Math.abs(this.getRow() - tile.getRow());
        int colDelta = Math.abs(this.getCol() - tile.getCol());
        rowDelta = Math.min(rowDelta, GameParam.rows - rowDelta);
        colDelta = Math.min(colDelta, GameParam.cols - colDelta);
        return rowDelta + colDelta;
    }
    /**
     * Returns row index.
     * 
     * @return row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns column index.
     * 
     * @return column index
     */
    public int getCol() {
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return row * (GameParam.cols+1) + col;
    }

    /**
     * {@inheritDoc}
     * @param o 
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            Tile tile = (Tile) o;
            result = row == tile.row && col == tile.col;
        }
        return result;
    }
    


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return row + " " + col;
    }

    Aim getAimTo(Tile nextTile) {
        if(this.getTile(Aim.NORTH).equals(nextTile))
            return Aim.NORTH;
        if(this.getTile(Aim.SOUTH).equals(nextTile))
            return Aim.SOUTH;
        if(this.getTile(Aim.EAST).equals(nextTile))
            return Aim.EAST;
        if(this.getTile(Aim.WEST).equals(nextTile))
            return Aim.WEST;
        throw new RuntimeException("Tile "+this+" doesn't share an edge with "+nextTile);
    }
    //vroeger was dit een
    //waterfill algoritme voor alle punten binnen een cirkel
    //maar dit blijkt 7x sneller te zijn.
    public ArrayList<Tile> getTilesUnderAViewingDistance(int dist2){
        int r = (int) (Math.sqrt(dist2) + 0.5);//straal van de cirkel
        ArrayList<Tile>result = new ArrayList<Tile>();
        for(int i=-r;i<=r;i++){
            for(int j=-r; j<=r;j++){
                Tile t = new Tile(this.row+i,this.col+j);
                if(this.getEuclidDistanceTo(t)<=dist2){
                    result.add(t);
                }
            }
        }
        return result;
    }
    
    public ArrayList<Tile> getTilesUnderAWalkingDistance(int dist){
        ArrayList<Tile> result = new ArrayList<Tile>();
        for(int i=1; i<=dist;i++){
            result.addAll(this.getTilesAtAWalkingDistance(i));
        }
        return result;
    }
    
    /**
     * Return all the tiles at a certain distance, 
     * the ones with the most possible paths in an empty terrain first
     * @param dist
     * @return List of tiles
     */
    public ArrayList<Tile> getTilesAtAWalkingDistance(int dist){
        ArrayList<Tile> result = new ArrayList<Tile>();
        if(dist==0){
            result.add(this);
            return result;
        }
        for(int b=0; b<=dist/2;b++){
            result.add(new Tile(this.getRow()+dist/2+b+dist%2,this.getCol()+dist/2-b));
            if(b!=-(dist%2) && b!=dist/2)
                result.add(new Tile(this.getRow()+dist/2-b,this.getCol()+dist/2+b+dist%2));

            result.add(new Tile(this.getRow()-dist/2+b,this.getCol()+dist/2+b+dist%2));
            if(b!=-(dist%2) && b!=dist/2)
                result.add(new Tile(this.getRow()-dist/2-b-dist%2,this.getCol()+dist/2-b));

            result.add(new Tile(this.getRow()-dist/2-b-dist%2,this.getCol()-dist/2+b));
            if(b!=-(dist%2) && b!=dist/2)
                result.add(new Tile(this.getRow()-dist/2+b,this.getCol()-dist/2-b-dist%2));

            result.add(new Tile(this.getRow()+dist/2-b,this.getCol()-dist/2-b-dist%2));
            if(b!=-(dist%2) && b!=dist/2)
                result.add(new Tile(this.getRow()+dist/2+b+dist%2,this.getCol()-dist/2+b));
        }
        return result;
    }

    //A* implementatie
    //geeft het heuristisch kortste pad terug van deze tile naar een andere tile.
    //Als die andere tile passable is, eindigt het pad op die andere tile.
    //Als die andere tile niet-passable is, eindigt het pad naast die andere tile.
    //Indien geen zo'n pad bestaat, return null
    Path shortestPath(Tile to) {
        Path p = new Path(this);//vertek van deze node
        ArrayList<Tile> goals = new ArrayList<Tile>();
        if(GameData.isPassable(to)){
            goals.add(to);
        }else{
            goals.addAll(to.getPassableBorderingTiles());
        }
        if(goals.isEmpty()){//die tile is niet bereikbaar want ze is omringd door water
            return null;
        }
        if(goals.contains(this) && GameData.isPassableOnTurn(this, GameData.currentturn()+1)){
            p = p.push(this);//sta stil
            return p;
        }
        ArrayList<IntPath> memory = new ArrayList<IntPath>();
        HashSet<Tile> beenthere = new HashSet<Tile>();
        beenthere.add(this);
        IntPath start = new IntPath(this.getManhattenDistanceTo(to), p, GameData.currentturn()+1);
        memory.add(start);
        while(true){
            if(memory.isEmpty()){//die tile is niet bereikbaar want we zijn ervan afgesloten
                return null;
            }
            int turn = memory.get(0).nextturn;
            Path shortest = memory.get(0).path;
            memory.remove(0);
            List<Tile> borders = shortest.getLastTile().getPassableBorderingTilesOnTurn(turn);
            borders.removeAll(beenthere);
            Collections.shuffle(borders);
            for(Tile b:borders){
                Path newpath = shortest.push(b);
                if(goals.contains(b)){
                    //Logger.log( "memory:"+memory.size());
                    return newpath; //de eerste keer dat we uitkomen, hebben we bij benadering het beste pad
                }
                int estimatedtotaldistance = newpath.getDistance() + b.getManhattenDistanceTo(to);
                IntPath ip = new IntPath(estimatedtotaldistance, newpath, turn+1);
                int insert = Collections.binarySearch(memory, ip);
                if(insert<0){
                    insert = -insert-1;
                }
                memory.add(insert, ip);
                beenthere.add(b);
            }
        }
    }
    
    //A* implementatie
    //geeft het heuristisch kortste pad terug van deze tile naar een andere tile.
    //Als die andere tile passable is, eindigt het pad op die andere tile.
    //Als die andere tile niet-passable is, eindigt het pad naast die andere tile.
    //Deze kan verschillende keren over dezelfde tile passeren en kan stilstaan
    //Hij is dus geschikt voor beweging in complexe groepen
    
    //waarschuwing: overflow als er geen enkel pad bestaat! Dit is voorlopig onmogelijk.
    
    //Indien geen zo'n pad bestaat, return null
    Path shortestComplexPath(Tile to) {
        Path p = new Path(this);//vertek van deze node
        ArrayList<Tile> goals = new ArrayList<Tile>();
        if(GameData.isPassable(to)){
            goals.add(to);
        }else{
            Logger.log("doel niet bereikbaar");
            return null;
        }
        if(goals.contains(this) && GameData.isPassableOnTurn(this, GameData.currentturn()+1)){
            Logger.log("sta stil");
            p = p.push(this);//sta stil
            return p;
        }
        //TODO: build connectivity map
        
        ArrayList<IntPath> memory = new ArrayList<IntPath>();
        HashSet<IntTile> beenthere = new HashSet<IntTile>();
        beenthere.add(new IntTile(this, GameData.currentturn()));
        IntPath start = new IntPath(this.getManhattenDistanceTo(to), p, GameData.currentturn()+1);
        memory.add(start);
        while(true){
            if(memory.isEmpty()){//geen enkel mogelijk pad, zelfs niet stilstaan...
                Logger.log("Ik vind geen enkel mogelijk complex pad");
                return null;
            }
            int turn = memory.get(0).nextturn;
            Path shortest = memory.get(0).path;
            memory.remove(0);
            List<Tile> borders = shortest.getLastTile().getPassableBorderingTilesOnTurn(turn);
            if(GameData.isPassableOnTurn(shortest.getLastTile(), turn)){
                borders.add(shortest.getLastTile());
            }
            for(IntTile it:beenthere){
                if(it.turn==turn){
                    borders.remove(it.tile);
                }
            }
            Collections.shuffle(borders);
            for(Tile b:borders){
                Path newpath = shortest.push(b);
                if(goals.contains(b)){
                    //Logger.log( "memory:"+memory.size());
                    Logger.log("Doel bereikt");
                    return newpath; //de eerste keer dat we uitkomen, hebben we bij benadering het beste pad
                }
                int estimatedtotaldistance = newpath.getDistance() + b.getManhattenDistanceTo(to);
                IntPath ip = new IntPath(estimatedtotaldistance, newpath, turn+1);
                int insert = Collections.binarySearch(memory, ip);
                if(insert<0){
                    insert = -insert-1;
                }
                memory.add(insert, ip);
                beenthere.add(new IntTile(b, turn));
            }
        }
    }

    public Tile getTile() {
        return this;
    }
    
    private class IntTile implements Comparable<IntTile> {
        int turn;
        Tile tile;
        IntTile(Tile t, int turn){
            this.turn=turn;
            this.tile = t;
        }
        @Override
        public int compareTo(IntTile ip) {
            return this.turn - ip.turn;
        }
    }
    
    private class IntPath implements Comparable<IntPath> {
        int dist;
        Path path;
        int nextturn;
        IntPath(int dist, Path p, int nextturn){
            this.dist=dist;
            this.path = p;
            this.nextturn = nextturn;
        }
        @Override
        public int compareTo(IntPath ip) {
            return this.dist - ip.dist;
        }
    }

    public ArrayList<Tile> getBorderingTiles() {
        ArrayList<Tile> result = new ArrayList<Tile>();
        result.add(new Tile(this.row+1,this.col));
        result.add(new Tile(this.row,this.col+1));
        result.add(new Tile(this.row-1,this.col));
        result.add(new Tile(this.row,this.col-1));
        return result;
    }
    public ArrayList<Tile> getPassableBorderingTiles() {
        ArrayList<Tile> result = getBorderingTiles();
        HashSet<Tile> rem = new HashSet<Tile>();
        for(Tile t:result){
            if(!GameData.isPassable(t)){
                rem.add(t);
            }
        }
        result.removeAll(rem);
        return result;
    }
    public ArrayList<Tile> getPassableBorderingTilesOnTurn(int turn) {
        ArrayList<Tile> result = getPassableBorderingTiles();
        HashSet<Tile> rem = new HashSet<Tile>();
        for(Tile t:result){
            if(null != GameData.isThereAnAntThereOnThisTurn(t,turn)){
                rem.add(t);
            }
        }
        result.removeAll(rem);
        return result;
    }
    
    public boolean equals(Tile t){
        return (this.row==t.row)&&(this.col == t.col);
    }
    
    public static Tile randomTile(){
        int mrow = (int)  (Math.random()*GameParam.rows);
        int mcol = (int)  (Math.random()*GameParam.cols);
        return new Tile(mrow,mcol);
    }
}
