package es.uam.eps.ir.discount;

/**
 *
 * @author saul
 */
public class ZipfDiscount extends DiscountModel {

    public ZipfDiscount() {
        super("zipf");
    }

    @Override
    public double discount(int k) {
        return 1 / (double) (1 + k);
    }

    public String toString(int k) {
        return "ZIPF";
    }
}
