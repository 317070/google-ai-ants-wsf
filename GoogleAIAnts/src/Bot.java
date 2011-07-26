
/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate() {
        Timer.start();//start the timer
        GameData.beginUpdate();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void addWater(int row, int col) {
        GameData.update(Ilk.WATER, new Tile(row, col) , 0);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void addAnt(int row, int col, int owner) {
        GameData.update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col), owner);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void addFood(int row, int col) {
        GameData.update(Ilk.FOOD, new Tile(row, col), 0);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void removeAnt(int row, int col, int owner) {
        GameData.update(Ilk.DEAD, new Tile(row, col), owner);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void removeFood(int row, int col) {
        GameData.removeFood(new Tile(row, col));
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate() {
        GameData.finishedUpdate();
    }
}
