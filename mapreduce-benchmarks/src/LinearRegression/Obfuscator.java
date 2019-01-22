package LinearRegression;

import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

public class Obfuscator {
    // TODO: might change this logic to house phi lambda and phi_lambda
    private BigDecimal decimal;
    private EncodedNumber encoded;
    private EncryptedNumber encrypted;
    private SecureRandom rand;
    private PaillierContext context;
    private boolean is_aggregate;

    public Obfuscator(PaillierContext context, ObfuscatorOperations op) {
        this.rand = new SecureRandom();
        this.context = context;
        this.is_aggregate = false;
        generate(op);
    }

    public Obfuscator(Obfuscator a, Obfuscator b, PaillierContext context, ObfuscatorOperations op) {
        this.context = context;
        this.is_aggregate = true;
        switch (op) {
            case MULTIPLY:
                decimal = a.getDecimal().multiply(b.getDecimal());
                break;
            case DIVIDE:
                double ans = b.getDecimal().doubleValue() / a.getDecimal().doubleValue();
                decimal = new BigDecimal(ans);
                break;
        }
        decimal = round(decimal);
        encoded = context.encode(decimal);
        encrypted = context.encrypt(encoded);
    }

    private BigDecimal round(BigDecimal value) { ;
        return value.setScale(Constants.PLACES, RoundingMode.HALF_UP);
    }

    private void generate(ObfuscatorOperations op) {
        if(!is_aggregate) {
            double value = rand.nextFloat() * Constants.OBFUSCATOR_MULTI_CONST_DOUBLE;
            if(op == ObfuscatorOperations.COMPARE) {
                value = 1.0 / value;
            }
            decimal = new BigDecimal(Double.toString(value));
            decimal = round(decimal);
            try {
                encoded = context.encode(decimal);
            } catch (ArithmeticException e) {
                System.err.println("ERROR: " + e.getMessage());
                decimal = new BigDecimal(Double.toString(rand.nextInt() % Constants.OBFUSCATOR_MOD_CONST_INT));
                decimal = round(decimal);
                encoded = context.encode(decimal);
            }
            encrypted = context.encrypt(encoded);
        }
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public EncodedNumber getEncoded() {
        return encoded;
    }

    public EncryptedNumber getEncrypted() {
        return encrypted;
    }


}
