
package carsystem;

 
public class EconomyCar extends Vehicle {

    private double weeklyDiscount;

    public EconomyCar() {
        this(0, "", 0, 0);
    }

    public EconomyCar(int id, String model, double pricePerDay, double weeklyDiscount) {
        super(id, model, pricePerDay);
        setWeeklyDiscount(weeklyDiscount);
    }

    @Override
    public String getType() {
        return "Economy";
    }

    // a rental of 7 days or more gets the weekly discount (percent)
    @Override
    public double calcRentalPrice(int days) {
        double total = getPricePerDay() * days;
        if (days >= 7) {
            total = total - total * weeklyDiscount / 100;
        }
        return total;
    }

    public double getWeeklyDiscount() {
        return weeklyDiscount;
    }

    public void setWeeklyDiscount(double weeklyDiscount) {
        this.weeklyDiscount = weeklyDiscount;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %.0f%% off for 7+ days", getWeeklyDiscount());
    }
}
 

