package es.uam.eps.ir.discount;

/**
 *
 * @author saul
 */
public class NoDiscount extends DiscountModel {

    public NoDiscount() {
        super("no");
    }

    @Override
    public double discount(int k) {
        return 1;
    }

    public String toString(int k) {
        return "NODISC";
    }
}
