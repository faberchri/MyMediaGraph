package es.uam.eps.ir.discount;

/**
 *
 * @author saul
 */
public class LogDiscount extends DiscountModel {

    public LogDiscount() {
        super("log");
    }

    @Override
    public double discount(int k) {
        return 1.0 / Math.log(k + 2) * Math.log(2);
    }

    public String toString(int k) {
        return "LOG";
    }
}
