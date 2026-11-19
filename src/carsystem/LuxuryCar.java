
package carsystem;

 
public class LuxuryCar extends Vehicle {

    private double deposit;

    public LuxuryCar() {
        this(0, "", 0, 0);
    }

    public LuxuryCar(int id, String model, double pricePerDay, double deposit) {
        super(id, model, pricePerDay);
        setDeposit(deposit);
    }

    @Override
    public String getType() {
        return "Luxury";
    }

    // the deposit is paid once for every rental
    @Override
    public double calcRentalPrice(int days) {
        return getPricePerDay() * days + deposit;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | deposit %.2f", getDeposit());
    }
}
  

