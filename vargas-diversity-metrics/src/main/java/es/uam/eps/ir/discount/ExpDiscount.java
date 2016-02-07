package es.uam.eps.ir.discount;

/**
 *
 * @author saul
 */
public class ExpDiscount extends DiscountModel {

    private double p;

    public ExpDiscount(double beta) {
        super("exp" + beta);
        this.p = beta;
    }

    @Override
    public double discount(int k) {
        return Math.pow(p, k);
    }

    public String toString(int k) {
        return "EXP";
    }
}
