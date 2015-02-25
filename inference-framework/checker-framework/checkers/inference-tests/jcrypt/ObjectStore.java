package webil.runtime.common;

import java.util.HashMap;

/**
 * @author kvikram The global map from object IDs to Objects Object IDs of
 *         confidential object references should be unpredictable There is one
 *         Object store for every session
 */

public class ObjectStore {

    private static class Ref {

        private final Object obj;

        public Ref(Object obj) {
            this.obj = obj;
        }
        
        public Object get() {
            return this.obj;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Ref)) {
                return false;
            }
            
            return obj == ((Ref) other).obj;
        }

        public int hashCode() {
            return obj.hashCode();
        }

    }

    private HashMap store;
    private HashMap reverseStore;
    private WilRuntime runtime;

    public ObjectStore(WilRuntime runtime) {
        this.runtime = runtime;
        store = new HashMap();
        reverseStore = new HashMap();
    }

    public WilObject getWilObject(ObjectID id) {
        return (WilObject) getObject(id);
    }

    public Object getObject(ObjectID id) {
        if (id == null) {
            return null;
        }

        return store.get(id);
    }
    
    private ObjectID getId(Object obj) {
        Ref ref = new Ref(obj);
        return (ObjectID) reverseStore.get(ref);
    }

    public ObjectID addToHeap(Object object) { // adds wilobject or java
                                                // objects to the heap
        if (object == null) return null;
        ObjectID id = getId(object);
        if (id == null) { // this object does not exist
            id = runtime.getUniqueObjectID();
            putObject(id, object);
        }
        return id;
        // add a java objectto the heap, make sure the addition is idempotent
    }

    public void putObject(ObjectID id, Object object) {
        store.put(id, object);
        reverseStore.put(new Ref(object), id);
    }

    public boolean containsID(ObjectID id) {
        return store.containsKey(id);
    }
    
}
