
package carsystem;



    public interface Hireable {

    public abstract boolean startRental(Reservation reservation);

    public abstract Reservation endRental();

    public abstract double calcRentalPrice(int days);
}


