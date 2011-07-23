
/**
 * Represents type of tile on the game map.
 */
/**
 * Represents type of tile on the game map.
 */
public enum Ilk {
    /** Water tile. */
    WATER,
    
    /** Food tile. */
    FOOD,
    
    /** Land tile. */
    LAND,
    
    /** Dead ant tile. */
    DEAD,
    
    /** My ant tile. */
    MY_ANT,
    
    /** Enemy ant tile. */
    ENEMY_ANT;
    
    /**
     * Checks if this type of tile is passable, which means it is not a water tile.
     */
    public boolean isPassable() {
        switch(this){
            case WATER:
                return false;
            default:
                return true;
        }
        
    }
    
    /**
     * Checks if this type of tile is unoccupied, which means it is a land tile or a dead ant tile.
     * 
     * @return <code>true</code> if this is a land tile or a dead ant tile, <code>false</code>
     *         otherwise
     */
    public boolean isUnoccupied() {
        return this == LAND || this == DEAD;
    }
}
