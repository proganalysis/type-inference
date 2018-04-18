import org.apache.commons.pool.PoolableObjectFactory;

public class SimplePoolableObjectFactory implements PoolableObjectFactory {
    int counter = 0;
    public Object makeObject() { return String.valueOf(counter++); }
    public void destroyObject(Object obj) { }
    public boolean validateObject(Object obj) { return true; }
    public void activateObject(Object obj) { }
    public void passivateObject(Object obj) { }
}
