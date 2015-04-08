package webil.runtime.common;

public class UniqueIdGenerator {
    
    protected int idLength;
    protected int seed;
    
    public UniqueIdGenerator(int idLength) {
        this.idLength = idLength;
    }
    
    public void setSeed(int seed) {
        this.seed = seed;
    }
    
    public int getSeed() {
        return seed;
    }
    
    public byte[] getNextId() {
        int id = seed++;
        byte[] result = new byte[idLength];
        
        for (int i = 0; i < idLength; i++) {
            result[i] = (byte) id;
            id = id >> 8;
        }
        
        return result;
    }

}
