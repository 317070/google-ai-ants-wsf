
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Gemaakt voor de WSF-deelname aan de google-AI contest
 * @author 317070 <erstaateenknolraapinmijntuin@gmail.com>
 */
public class Map<T> implements Cloneable {

    private final ArrayList<ArrayList<T>> map;

    public Map() {
        map = new ArrayList<ArrayList<T>>(GameParam.rows);
        for (int i = 0; i < GameParam.rows; i++) {
            map.add(new ArrayList<T>(GameParam.cols));
            for (int j = 0; j < GameParam.cols; j++) {
                map.get(i).add(null);
            }
        }
    }

    public T get(int i, int j) {
        return get(new Tile(i, j));
    }

    public T get(Tile t) {
        return map.get(t.getRow()).get(t.getCol());
    }

    public void set(int i, int j, T t) {
        this.set(new Tile(i, j), t);
    }

    public void set(Tile tile, T t) {
        map.get(tile.getRow()).set(tile.getCol(), t);
    }

    //shallow copy, T is better immutable
    @Override
    public Map<T> clone() {
        Map<T> result = new Map<T>();
        for (int i = 0; i < GameParam.rows; i++) {
            for (int j = 0; j < GameParam.cols; j++) {
                result.set(i, j, this.get(i, j));
            }
        }
        return result;
    }
}
