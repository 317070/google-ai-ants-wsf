/*
 * Store the ingame parameters
 */

/**
 * 
 * @author Jonas
 */
public class GameParam {
    final static long TIMEPERTURN = 1000;
    static int loadTime;
    static int turnTime;
    static int rows; 
    static int cols;
    static int turns;
    static int viewRadius2;
    static int attackRadius2;
    static int spawnRadius2;
    
    public static void setup(int lt, int tt, int r, int c, int trns, int viewRad, int attackRad,
                int spawnRad){
        loadTime = lt;
        turnTime = tt;
        rows = r;
        cols = c;
        turns = trns;
        viewRadius2 = viewRad;
        attackRadius2 = attackRad;
        spawnRadius2 = spawnRad;
    }
    /**
     * Returns timeout for initializing and setting up the bot on turn 0.
     * 
     * @return timeout for initializing and setting up the bot on turn 0
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * Returns timeout for a single game turn, starting with turn 1.
     * 
     * @return timeout for a single game turn, starting with turn 1
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * Returns game map height.
     * 
     * @return game map height
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns game map width.
     * 
     * @return game map width
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns maximum number of turns the game will be played.
     * 
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Returns squared view radius of each ant.
     * 
     * @return squared view radius of each ant
     */
    public int getViewRadius2() {
        return viewRadius2;
    }

    /**
     * Returns squared attack radius of each ant.
     * 
     * @return squared attack radius of each ant
     */
    public int getAttackRadius2() {
        return attackRadius2;
    }

    /**
     * Returns squared spawn radius of each ant.
     * 
     * @return squared spawn radius of each ant
     */
    public int getSpawnRadius2() {
        return spawnRadius2;
    }

}
