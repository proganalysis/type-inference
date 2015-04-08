package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author kvikram
 * An object of this class represents a stack frame.
 * Two arrays for each primitive, including a long ID for
 * object references
 * One set of arrays is exclusively on the currenthost, and
 * the other set consists values of shared local variables
 * Assume only boolean, int,float and Object types for now.
 *
 */

public class StackFrame {
    public StackFrame(FrameIndices indices, StackFrameID stackFrameID) {
        booleans = new boolean[indices.boolLen];
        integers = new int[indices.intLen];
        floats = new float[indices.floatLen];
        objectIDs = new Object[indices.oidLen]; // for all objects that have an object ID including java objects
        immutables = new Object[indices.immutLen]; // for those immutable objects
        
        this.stackFrameID = stackFrameID;
        this.indices = indices;
    }
    
    public Object self; // the this pointer
    public final StackFrameID stackFrameID;
    public final FrameIndices indices; // contains meta information about the argument arrays
    // one instance per stack frame instance

    public boolean[] booleans;
    public int[] integers;
    public float[] floats;
    public Object[] objectIDs;
    public Object[] immutables;
    
    public StackFrameUpdate getUpdate(HeapUpdate heapUpdate, WilRuntime runtime) throws WilRuntimeException {
        
        StackFrameUpdate toReturn = new StackFrameUpdate(stackFrameID);
        int j;
        int size;
        // copy bools
        size = getTrueSize(indices.boolMask, 1);
        toReturn.booleans = new boolean[size]; j = 0;
        toReturn.dirtyBooleans = new boolean[size];
        for(int i = 0; i < booleans.length; i++) {
            if(indices.boolMask[i][1]) {
                toReturn.booleans[j] = booleans[i];
                toReturn.dirtyBooleans[j] = indices.boolMask[i][2];
                indices.boolMask[i][2] = false; // resetting the dirty bit
                j++;
            }
        }
        
        // copy ints
        size = getTrueSize(indices.intMask, 1);
        toReturn.integers = new int[size]; j = 0;
        toReturn.dirtyIntegers = new boolean[size];
        for(int i = 0; i < integers.length; i++) {
            if(indices.intMask[i][1]) {
                toReturn.integers[j] = integers[i];
                toReturn.dirtyIntegers[j] = indices.intMask[i][2];
                indices.intMask[i][2] = false;
                j++;
            }
        }

        // copy floats
        size = getTrueSize(indices.floatMask, 1);
        toReturn.floats = new float[size]; j = 0;
        toReturn.dirtyFloats = new boolean[size];
        for(int i = 0; i < floats.length; i++) {
            if(indices.floatMask[i][1]) {
                toReturn.floats[j] = floats[i];
                toReturn.dirtyFloats[j] = indices.floatMask[i][2];
                indices.floatMask[i][2] = false;
                j++;
            }
        }

        // copy immuts
        size = getTrueSize(indices.immutMask, 1);
        toReturn.immutables = new IsSerializable[size]; j = 0;
        toReturn.dirtyImmutables = new boolean[size];
        for(int i = 0; i < immutables.length; i++) {
            if(indices.immutMask[i][1]) {
                toReturn.immutables[j] = WilRuntime.objectToSerializable(immutables[i]);
                toReturn.dirtyImmutables[j] = indices.immutMask[i][2];
                indices.immutMask[i][2] = false;
                j++;
            }
        }

        // copy objects while converting to object IDs
        size = getTrueSize(indices.oidMask, 1);
        toReturn.objectIDs = new IsSerializable[size]; j = 0;
        toReturn.dirtyObjectIDs = new boolean[size];
        for(int i = 0; i < objectIDs.length; i++) {
            if(indices.oidMask[i][1]) {
                toReturn.objectIDs[j] = runtime.referenceToSerializable(objectIDs[i], heapUpdate);
                toReturn.dirtyObjectIDs[j] = indices.oidMask[i][2];
                indices.oidMask[i][2] = false;
                j++;
            }
        }
        
        // copy this pointer XXX What if this is from a possibly malicious client? 
        toReturn.self = runtime.referenceToID(self, heapUpdate);
        return toReturn;
        
    }
    
    private int getTrueSize(boolean[][] arr, int column) {
        if (arr == null) {
            return 0;
        }
        
        int count = 0;
        
        for(int i = 0; i < arr.length; i++) {
            if(arr[i][column]) {
                count++;
            }
        }
        return count;
    }
    
    // apply this destructively
    public void applyUpdate(StackFrameUpdate update, WilRuntime runtime) throws WilRuntimeException {
        // first check if structure matches
        // also check if the right kind of update is sent (client or server)
        // so that only the appropriate variables are updated
        // copy bools
        for(int i = 0,j = 0; i < booleans.length; i++) {
            if(indices.boolMask[i][0]) {
                if(update.dirtyBooleans[j]) booleans[i] = update.booleans[j];
                j++;
            }
        }

        // copy ints
        for(int i = 0,j = 0; i < integers.length; i++) {
            if(indices.intMask[i][0]) {
                if(update.dirtyIntegers[j]) integers[i] = update.integers[j];
                j++;
            }
        }

        // copy floats
        for(int i = 0,j = 0; i < floats.length; i++) {
            if(indices.floatMask[i][0]) {
                if(update.dirtyFloats[j]) floats[i] = update.floats[j];
                j++;
            }
        }

        // copy immutable java objects
        for(int i = 0,j = 0; i < immutables.length; i++) {
            if(indices.immutMask[i][0]) {
                if(update.dirtyImmutables[j]) immutables[i] = WilRuntime.serializableToObject(update.immutables[j]);
                j++;
            }
        }

        // copy object IDs while converting them to objects
        for(int i = 0,j = 0; i < objectIDs.length; i++) {
            if(indices.oidMask[i][0]) {
                if(update.dirtyObjectIDs[j]) objectIDs[i] = runtime.serializableToReference(update.objectIDs[j]);
                j++;
            }
        }
        
        // copy this pointer
        if (self == null && update.self != null) {
            self = runtime.idToReference(update.self);
        }        
    }
    
    public void clearDirtyBits() {
        this.indices.clearDirtyBits();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("FrameIndices: ");
        sb.append(indices.toString());
        sb.append(arrayToString(booleans, "booleans: "));
        sb.append(arrayToString(integers, "integers: "));
        sb.append(arrayToString(floats, "floats: "));
        sb.append(arrayToString(objectIDs, "objectIDs: "));
        sb.append(arrayToString(immutables, "immutables: "));
        return sb.toString();
    }
    
    private static String arrayToString(Object array, String prefix) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        if(array instanceof boolean[]) {
            boolean[] arr = (boolean[])array;
            for(int i = 0; i < arr.length; i++) {
                sb.append(arr[i] + ", ");
            }
        } else if(array instanceof int[]) {
            int[] arr = (int[])array;
            for(int i = 0; i < arr.length; i++) {
                sb.append(arr[i] + ", ");
            }
        }  else if(array instanceof float[]) {
            float[] arr = (float[])array;
            for(int i = 0; i < arr.length; i++) {
                sb.append(arr[i] + ", ");
            }
        } else if(array instanceof Object[]) {
            Object[] arr = (Object[])array;
            for(int i = 0; i < arr.length; i++) {
                sb.append(arr[i] + ", ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}