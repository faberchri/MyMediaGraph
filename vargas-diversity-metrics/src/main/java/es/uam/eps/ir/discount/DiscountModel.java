package es.uam.eps.ir.discount;

/**
 *
 * @author saul
 */
public abstract class DiscountModel {

    private final String name;

    public DiscountModel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
            return name;
    }

    public abstract double discount(int k);
}
