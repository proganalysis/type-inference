import org.apache.commons.pool.KeyedPoolableObjectFactory;
import java.util.HashMap;

public class SimpleKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory {
    HashMap map = new HashMap();
    public Object makeObject(Object key) {
        int counter = 0;
        Integer Counter = (Integer)(map.get(key));
        if(null != Counter) {
            counter = Counter.intValue();
        }
        map.put(key,new Integer(counter + 1));
        return String.valueOf(key) + String.valueOf(counter);
    }
    public void destroyObject(Object key, Object obj) { }
    public boolean validateObject(Object key, Object obj) { return true; }
    public void activateObject(Object key, Object obj) { }
    public void passivateObject(Object key, Object obj) { }
}
