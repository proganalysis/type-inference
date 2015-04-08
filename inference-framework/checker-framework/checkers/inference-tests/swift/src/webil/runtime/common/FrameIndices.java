package webil.runtime.common;

public class FrameIndices {
    
    public FrameIndices() {
        boolLen = 0;
        intLen = 0;
        floatLen = 0;
        immutLen = 0;
        oidLen = 0;
    }
    
    // The lengths of the arrays
    public int boolLen; 
    public int intLen;
    public int floatLen;
    public int immutLen;
    public int oidLen;
    
    // The security restrictions on each element of each array
    // boolean 0: integrity level: should this variable be updated with a message
    //                  from the other host?
    // boolean 1: confidentiality level: should the value of this variable
    //                  be sent to the other host?
    // boolean 2: dirty status: has this variable been modified since last
    //                  message?
    public boolean[][] boolMask;
    public boolean[][] intMask;
    public boolean[][] floatMask;
    public boolean[][] immutMask;
    public boolean[][] oidMask;
    
    public String toString() {
        StringBuffer toReturn = new StringBuffer();
        toReturn.append(arrayToString(boolMask, "boolMask"));
        toReturn.append(arrayToString(intMask, "intMask"));
        toReturn.append(arrayToString(floatMask, "floatMask"));
        toReturn.append(arrayToString(immutMask, "immutMask"));
        toReturn.append(arrayToString(oidMask, "oidMask"));
        return toReturn.toString();
    }
    
    private static String arrayToString(boolean[][] array, String prefix) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        if (array != null) {
            for(int i = 0; i < array.length; i++) {
                sb.append("{" + array[i][0] + "," + array[i][1] + "}\n");
            }
        }
        else {
            sb.append("<empty>\n");
        }
        sb.append('\n');
        return sb.toString();
    }

    public void clearDirtyBits() {
        for(int i = 0; i < boolLen; i++) {
            boolMask[i][2] = false;
        }
        for(int i = 0; i < intLen; i++) {
            intMask[i][2] = false;
        }
        for(int i = 0; i < floatLen; i++) {
            floatMask[i][2] = false;
        }
        for(int i = 0; i < immutLen; i++) {
            immutMask[i][2] = false;
        }
        for(int i = 0; i < oidLen; i++) {
            oidMask[i][2] = false;
        }
        
    }
}
