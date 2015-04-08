package webil.runtime.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import webil.runtime.common.UniqueIdGenerator;

/**
 * Creates unpredictable IDs. You should use a large enough ID length so that
 * collisions are unlikely, since there is no guarantee that all IDs produced
 * will indeed be unique.
 */
public class SecureIdGenerator extends UniqueIdGenerator {
    
    protected BigInteger secureSeed;
    protected final MessageDigest sha;

    public SecureIdGenerator(int idLength, BigInteger secureSeed) {
        super(idLength);
        
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(
                "Could not create SecureIdGenerator");
        }
        
        setSeed(secureSeed);
    }
    
    public void setSeed(BigInteger secureSeed) {
        this.secureSeed = secureSeed;
    }
    
    /**
     * Please use setSeed(BigInteger) to set a secure seed.
     * @deprecated
     */
    public void setSeed(int seed) {
        setSeed(new BigInteger(Integer.toString(seed)));
    }
    
    public BigInteger getSecureSeed() {
        return secureSeed;
    }
    
    /**
     * Please use getSecureSeed instead.
     * @deprecated
     */
    public int getSeed() {
        throw new UnsupportedOperationException("Use getSecureSeed instead");
    }

    public byte[] getNextId() {
        byte[] hash = sha.digest(secureSeed.toByteArray());
        secureSeed = secureSeed.add(BigInteger.ONE);
        
        byte[] result = new byte[idLength];
        int len = Math.min(idLength, hash.length);
        
        for (int i = 0; i < len; i++) {
            result[i] = hash[i];
        }
        
        return result;
    }

}
