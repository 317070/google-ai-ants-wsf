
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Represents a tile of the game map.
 */
public class Tile {

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
        int row = this.row + aim.getRowDelta();
        int col = this.col + aim.getColDelta();
        return new Tile(row,col);
    }

    /**
     * Calculates Euclides^2 distance between two locations on the game map.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getDistanceTo(Tile tile) {
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
    
    public List<Tile> getTilesUnderADistance(int dist){
        ArrayList<Tile> result = new ArrayList<Tile>();
        for(int i=1; i<=dist;i++){
            result.addAll(this.getTilesAtADistance(i));
        }
        return result;
    }
    
    /**
     * Return all the tiles at a certain distance, 
     * the ones with the most possible paths in an empty terrain first
     * then according to NESW
     * @param dist
     * @return List of tiles
     */
    public List<Tile> getTilesAtADistance(int dist){
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
    Path shortestPath(Tile to) {
        Path p = new Path(this);
        ArrayList<IntPath> memory = new ArrayList<IntPath>();
        HashSet<Tile> beenthere = new HashSet<Tile>();
        beenthere.add(this);
        IntPath start = new IntPath(this.getManhattenDistanceTo(to), p);
        memory.add(start);
        while(true){
            Path shortest = memory.get(0).path;
            memory.remove(0);
            List<Tile> borders = shortest.getLastTile().getBorderingTiles();
            borders.removeAll(beenthere);
            Collections.shuffle(borders);
            for(Tile b:borders){
                if(!GameData.isPassable(b))
                    continue;
                Path newpath = shortest.push(b);
                if(b.equals(to)){
                    return newpath; //de eerste keer dat we uitkomen, hebben we bij benadering het beste pad
                }
                int estimatedtotaldistance = newpath.getDistance() + b.getManhattenDistanceTo(to);
                IntPath ip = new IntPath(estimatedtotaldistance, newpath);
                int insert = Collections.binarySearch(memory, ip);
                if(insert<0){
                    insert = -insert-1;
                }
                memory.add(insert, ip);
                beenthere.add(b);
            }
        }
            
        
    }
    private class IntPath implements Comparable<IntPath> {
        int dist;
        Path path;
        IntPath(int dist, Path p){
            this.dist=dist;
            this.path = p;
        }

        public int compareTo(IntPath ip) {
            return this.dist - ip.dist;
        }
    }

    public List<Tile> getBorderingTiles() {
        ArrayList<Tile> result = new ArrayList<Tile>();
        result.add(new Tile(this.row+1,this.col));
        result.add(new Tile(this.row,this.col+1));
        result.add(new Tile(this.row-1,this.col));
        result.add(new Tile(this.row,this.col-1));
        return result;
    }
    
    public boolean equals(Tile t){
        return (this.row==t.row)&&(this.col == t.col);
    }
    
    public Tile randomTile(){
        int row = (int)  (Math.random()*GameParam.rows);
        int col = (int)  (Math.random()*GameParam.cols);
        return new Tile(row,col);
    }
}
