package thep.paillier;


import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class EncryptedInteger implements Serializable {
	/**
	 * The serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private BigInteger cipherval;
	private PublicKey	pub;
	private Random rng;
	@SuppressWarnings("rawtypes")
	transient private Constructor rngCons;
	@SuppressWarnings("rawtypes")
	transient private Constructor biCons;
	@SuppressWarnings("rawtypes")
	private Class bigi;

	/**
	 * Uses plainval encrypted with pub for the encrypted integer
	 * and BigInteger as the underlying class
	 * @param plainval
	 * @param pub
	 * @throws BigIntegerClassNotValid  
	 */
	public EncryptedInteger(BigInteger plainval, PublicKey pub) throws BigIntegerClassNotValid {
		this(plainval, pub, BigInteger.class);
	}
	
	/**
	 * Creates an empty encrypted integer with pub for the public key
	 * and BigInteger as the underlying class
	 * @param pub
	 * @throws BigIntegerClassNotValid  
	 */
	public EncryptedInteger(PublicKey pub) throws BigIntegerClassNotValid {
		this(BigInteger.ZERO, pub, BigInteger.class);
	}
	
	/**
	 * Constructor with no initial plaintext value
	 * @param pub the public key to encrypt with
	 * @param c the class to use for big integers
	 * @throws BigIntegerClassNotValid  
	 */
	public EncryptedInteger(PublicKey pub, Class<? extends BigInteger> c) throws BigIntegerClassNotValid {
		this(BigInteger.ZERO, pub, c);
	}
	
	/**
	 * Constructs an encrypted integer version of the given plaintext value
	 * with the given public key.
	 * 
	 * @param plainval the plaintext for the encrypted integer
	 * @param pub the public key which will be used to do the encrypting
	 * @param c the class to use for big integers
	 * @throws BigIntegerClassNotValid
	 */
	public EncryptedInteger(BigInteger plainval, PublicKey pub, Class<? extends BigInteger> c) throws BigIntegerClassNotValid {
		this.rng = new SecureRandom();
		this.pub = pub;
		this.rngCons = this.findRngCons(c);
		this.biCons = this.findBICons(c);
		this.bigi = c;
		this.set(plainval);
	}
	
	/**
	 * Constructs a copy of the other encrypted integer
	 * 
	 * @param other the other encrypted integer
	 */
	public EncryptedInteger(EncryptedInteger other) {
		this.rng = new SecureRandom();
		this.cipherval = other.getCipherVal();
		this.pub = other.getPublicKey();
		this.rngCons = other.rngCons;
		this.biCons = other.biCons;
	}
	
	/**
	 * Sets the encrypted integer to an encrypted version of the plaintext
	 * value. WARNING: The return value 'r' must be kept private for security.
	 * 
	 * @param plainval the new plaintext value that will be encrypted
	 * @return the random number used to encrypt plainval
	 * @throws BigIntegerClassNotValid 
	 */
	public BigInteger set(BigInteger plainval) throws BigIntegerClassNotValid {
		// Encrypt plainval and store it in cipherval
		BigInteger r = BigInteger.ZERO;
		BigInteger x;
		
		// Generate random blinding factor less than n
		do {
			try {
				r = (BigInteger) this.rngCons.newInstance(this.pub.getBits(), rng);
			} catch (Exception e) {
				throw new BigIntegerClassNotValid("Could not construct the given big integer class");
			}
		} while(r.compareTo(this.pub.getN()) >= 0);
		
		BigInteger g = this.pub.getG();
		try {
			if (this.biCons != null)
				g = (BigInteger) this.biCons.newInstance(this.pub.getG());
			
		} catch (Exception e) {
			throw new BigIntegerClassNotValid("Could not construct");
		}
		cipherval = g.modPow(plainval, this.pub.getNSquared());
		x = r.modPow(this.pub.getN(), this.pub.getNSquared());
		
		cipherval = cipherval.multiply(x);
		cipherval = cipherval.mod(this.pub.getNSquared());
		
		return r;
	}
	
	/**
	 * Adds one encrypted integer to this encrypted integer
	 * Note, if you are using r returned from the set(BigInteger) method,
	 * you can compute the r for the sum of two encrypted integers by multiplying
	 * the r values and taking the result mod pub.getNSquared().  
	 * 
	 * @param other the encrypted integer to add
	 * @return a new encrypted integer with the encrypted integer added to the current
	 * @throws PublicKeysNotEqualException
	 */
	public EncryptedInteger add(EncryptedInteger other) throws PublicKeysNotEqualException {
		if(!this.pub.equals(other.getPublicKey())) {
			throw new PublicKeysNotEqualException("Cannot add integers encrypted with different public keys");
		}
		EncryptedInteger tmp_int = new EncryptedInteger(this);
		BigInteger tmp = cipherval.multiply(other.getCipherVal());
		tmp = tmp.mod(pub.getNSquared());
		
		tmp_int.setCipherVal(tmp);
		
		return tmp_int;
	}
	
	/**
	 * Adds a constant to the encrypted integer
	 * 
	 * @param other the constant to be added
	 * @return a new encrypted integer with the constant added to the current 
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedInteger add(BigInteger other) throws BigIntegerClassNotValid {
		EncryptedInteger tmp_int = new EncryptedInteger(this);
		BigInteger g = this.pub.getG();
		try {
			if (this.biCons != null)
				g = (BigInteger) this.biCons.newInstance(this.pub.getG());

		} catch (Exception e) {
			throw new BigIntegerClassNotValid("Could not construct");
		}
		BigInteger tmp = cipherval.multiply(g.modPow(other, this.pub.getNSquared()));
		tmp = tmp.mod(this.pub.getNSquared());
		
		tmp_int.setCipherVal(tmp);
		
		return tmp_int;
	}
	
	/**
	 * Multiplies the encrypted integer by a constant
	 * 
	 * @param other the constant by which to multiply
	 * @return a new encrypted integer equal to the original times the constant
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedInteger multiply(BigInteger other) throws BigIntegerClassNotValid {
		EncryptedInteger tmp_int = new EncryptedInteger(this);
		BigInteger c = cipherval;
		try {
			if (this.biCons != null)
				c = (BigInteger) this.biCons.newInstance(cipherval);
			
		} catch (Exception e) {
			throw new BigIntegerClassNotValid("Could not construct");
		}
		BigInteger tmp = c.modPow(other, pub.getNSquared());
		
		tmp_int.setCipherVal(tmp);
		
		return tmp_int;
	}
	
	/**
	 * Rerandomizes the encrypted integer (without needing the private key)
	 * by using the homomorphic properties to add a randomly encrypted version
	 * of zero.
	 * 
	 * Rerandomization is useful so a server does not know that you are
	 * resubmitting a value they have already operated on. For example, if an
	 * untrusted server is given an encrypted integer and performs some math,
	 * then later is given that same ciphertext which resulted from earlier
	 * calculations, the untrusted server has gained some information (i.e. it
	 * is most likely the same value that was operated on earlier).
	 * Rerandomization prevents this.
	 * @throws BigIntegerClassNotValid 
	 */
	public void rerandomize() throws BigIntegerClassNotValid {
		BigInteger r = BigInteger.ZERO;
		try {
			r = (BigInteger) this.rngCons.newInstance(this.pub.getBits(), rng);
		} catch (Exception e) {
			throw new BigIntegerClassNotValid("Could not construct the given big integer class");
		}
		r = r.modPow(this.pub.getN(), this.pub.getNSquared());
		cipherval = cipherval.multiply(r);
		cipherval = cipherval.mod(this.pub.getNSquared());
	}
	
	/**
	 * Decrypts the current ciphertext value held by the class
	 * 
	 * @param priv the private key do use for decryption
	 * @return A BigInteger of the decrypted value
	 * @throws BigIntegerClassNotValid 
	 */
	public BigInteger decrypt(PrivateKey priv) throws BigIntegerClassNotValid {
		// Decrypt the encrypted value
		BigInteger plainval;
		BigInteger c = cipherval;
		try {
			if (this.biCons != null)
			c = (BigInteger) this.biCons.newInstance(cipherval);
			
		} catch (Exception e) {
			throw new BigIntegerClassNotValid("Could not construct");
		}
		
		plainval = c.modPow(priv.getLambda(), priv.getPublicKey().getNSquared());
		plainval = plainval.subtract(BigInteger.ONE);
		plainval = plainval.divide(priv.getPublicKey().getN());
		plainval = plainval.multiply(priv.getMu());
		plainval = plainval.mod(priv.getPublicKey().getN());
		
		return plainval;
	}
	
	/**
	 * Returns the ciphertext value
	 * 
	 * @return the ciphertext value
	 */
	public BigInteger getCipherVal() {
		return cipherval;
	}
	
	/**
	 * Returns the public key associated with this encrypted integer
	 * @return the public key
	 */
	public PublicKey getPublicKey() {
		return this.pub;
	}
	
	/*
	 * Sets the cipherval, should only be used in this package
	 */
	public void setCipherVal(BigInteger cipherval) {
		this.cipherval = cipherval;
	}
	
	@SuppressWarnings("rawtypes")
	private Constructor findRngCons(Class<? extends BigInteger> c) throws BigIntegerClassNotValid {
		Constructor cons = null;
		for (Constructor i : c.getConstructors()) {
			Class[] params = i.getParameterTypes();
			if (params.length == 2 && params[0].getCanonicalName().equals("int") &&
					params[1].getCanonicalName().equals("java.util.Random")) {
				cons = i;
				break;
			}
		}
		
		if (cons == null) {
			throw new BigIntegerClassNotValid("Could not find the int, Random constructor");
		}
		
		return cons;
	}
	
	@SuppressWarnings("rawtypes")
	private Constructor findBICons(Class<? extends BigInteger> c) {
		Constructor cons = null;
		for (Constructor i : c.getConstructors()) {
			Class[] params = i.getParameterTypes();
			if (params.length == 1 && params[0].getCanonicalName().equals("java.math.BigInteger")) {
				cons = i;
				break;
			}
		}
		
		return cons;
	}
	
	@SuppressWarnings("unchecked")
	private Object readResolve() throws ObjectStreamException, BigIntegerClassNotValid {
		this.rngCons = this.findRngCons(bigi);
		this.biCons = this.findBICons(bigi);
		
		return this;
	}
}
