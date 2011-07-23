
/**
 * Represents an order to be issued.
 * IMMUTABLE
 */
public class Order {

    private final Tile from;
    private final Tile to;
    private final Aim direction;
    /**
     * Creates new {@link Order} object.
     * 
     * @param tile map tile with my ant
     * @param direction direction in which to move my ant
     */
    public Order(Tile tile, Aim direction) {
        from = tile;
        this.direction = direction;
        to = from.getTile(direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "o " + from.getRow() + " " + from.getCol() + " " + direction.getSymbol();
    }

    void cancel() {
        OrderManager.cancel(this);
    }
    public Tile getTarget(){
        return to;
    }
    public Tile getFrom(){
        return from;
    }
}
