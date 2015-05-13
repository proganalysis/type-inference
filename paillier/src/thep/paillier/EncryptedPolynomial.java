package thep.paillier;

import java.io.Serializable;
import java.math.BigInteger;

import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import thep.paillier.exceptions.SizesNotEqualException;

public class EncryptedPolynomial implements Serializable {

	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;
	private EncryptedInteger[] coefficients;
	private PublicKey pub;
	
	/**
	 * Constructs a single variable encrypted polynomial object of 
	 * the form f(x) = (c_0 + c_1*x + c_2*x^2 + ... + c_n*x^n)
	 * @param coefficients the coefficients of the polynomial 
	 * (c_0 + c_1*x + c_2*x^2 + ... + c_n*x^n)
	 * @param pub the public key associated with the polynomial
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedPolynomial(BigInteger[] coefficients, PublicKey pub) throws BigIntegerClassNotValid {
		this.pub = pub;
		this.coefficients = new EncryptedInteger[coefficients.length];
		
		// Encrypt each coefficient
		for (int i=0; i < this.coefficients.length; i++) {
			this.coefficients[i] = new EncryptedInteger(coefficients[i], 
					this.pub);
		}
	}
	
	/**
	 * Constructs a copy of the given encrypted polynomial
	 * @param other the encrypted polynomial to copy
	 */
	public EncryptedPolynomial(EncryptedPolynomial other) {
		this.pub = other.getPublicKey();
		this.coefficients = other.getCoefficients();
	}
	
	/**
	 * Evaluates an encrypted polynomial at the given point
	 * @param point the point at which to evaluate the polynomial
	 * @return an encrypted integer form of the polynomial evaluated at 
	 * the given point
	 * @throws PublicKeysNotEqualException
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedInteger evaluate(BigInteger point) throws 
			PublicKeysNotEqualException, BigIntegerClassNotValid {
		EncryptedInteger accum = new EncryptedInteger(BigInteger.ZERO, pub);
		
		for (int i=0; i < this.coefficients.length; i++) {
			accum = accum.add(this.coefficients[i].multiply(point.pow(i)));
		}
		
		return accum;
	}
	
	/**
	 * Adds two encrypted polynomials together
	 * @param other the other polynomial to add to this
	 * @return a new encrypted polynomial equal to the sum
	 * @throws PublicKeysNotEqualException
	 * @throws SizesNotEqualException
	 */
	public EncryptedPolynomial add(EncryptedPolynomial other) throws 
			PublicKeysNotEqualException, SizesNotEqualException {
		if (!this.pub.equals(other.getPublicKey()))
			throw new PublicKeysNotEqualException("Encrypted polynomials must " +
					"have same public key to be added");
		
		if (this.coefficients.length != other.getCoefficients().length)
			throw new SizesNotEqualException("Encrypted polynomials must " +
					"have same order to add");
		
		// Create temporary object which will be returned as the result
		EncryptedPolynomial tmp = new EncryptedPolynomial(this);
		EncryptedInteger[] tmp_coefficients = 
			new EncryptedInteger[this.coefficients.length];
		
		for (int i=0; i < tmp_coefficients.length; i++) {
			tmp_coefficients[i] = this.coefficients[i].add(
					other.getCoefficients()[i]);
		}
		
		tmp.setCoefficients(tmp_coefficients);
		return tmp;
	}
	
	/**
	 * Multiplies an encrypted polynomial by a constant
	 * @param constant the constant to multiply by
	 * @return an encrypted polynomial that is equal to the original multiplied
	 * by the constant
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedPolynomial multiply(BigInteger constant) throws BigIntegerClassNotValid {
		EncryptedPolynomial tmp = new EncryptedPolynomial(this);
		EncryptedInteger[] tmp_coefficients = 
			new EncryptedInteger[this.coefficients.length];
		
		for (int i=0; i < tmp_coefficients.length; i++) {
			tmp_coefficients[i] = this.coefficients[i].multiply(constant);
		}
		
		tmp.setCoefficients(tmp_coefficients);
		return tmp;
	}
	
	/**
	 * Multiplies an encrypted polynomial by a known polynomial
	 * @param plain_coefficients the coefficients of the plain text polynomial
	 * @return an encrypted polynomial equal to this multiplied by the given plain
	 * text polynomial
	 * @throws SizesNotEqualException
	 * @throws PublicKeysNotEqualException
	 * @throws BigIntegerClassNotValid 
	 */
	public EncryptedPolynomial multiply(BigInteger[] plain_coefficients) throws
			SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// Check sizes
		if (this.coefficients.length != plain_coefficients.length)
			throw new SizesNotEqualException("To multiply an encrypted " +
					"polynomial by a known polynomial, they must have the " +
					"same number of coefficients");
		
		EncryptedPolynomial tmp = new EncryptedPolynomial(this);
		EncryptedInteger[] tmp_coefficients = 
			new EncryptedInteger[this.coefficients.length*2-1];
		
		for (int i=1; i <= this.coefficients.length; i++) {
			for (int j=1; j <= plain_coefficients.length; j++) {
				int l = i+j-2;
				
				EncryptedInteger tmp2 = 
					this.coefficients[i-1].multiply(plain_coefficients[j-1]);
				
				if (tmp_coefficients[l] == null) {
					tmp_coefficients[l] = tmp2;
				}
				else {
					tmp_coefficients[l] = tmp_coefficients[l].add(tmp2);
				}
			}
		}
		
		tmp.setCoefficients(tmp_coefficients);
		return tmp;
	}
	
	/**
	 * Returns the public key associated with this encrypted polynomial
	 * @return the public key
	 */
	public PublicKey getPublicKey() {
		return this.pub;
	}
	
	/**
	 * Returns the encrypted coefficients which make up the polynomial
	 * @return the encrypted coefficients which make up the polynomial
	 */
	public EncryptedInteger[] getCoefficients() {
		return this.coefficients;
	}
	
	/*
	 * Setters
	 */
	private void setCoefficients(EncryptedInteger[] coefficients) {
		this.coefficients = coefficients;
	}
}
