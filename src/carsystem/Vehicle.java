
package carsystem;


public abstract class Vehicle implements Hireable {

    private final int ID;
    private String model;
    private double pricePerDay;
    private Reservation currentReservation;

    public Vehicle() {
        this(0, "", 0);
    }

    public Vehicle(int id, String model, double pricePerDay) {
        this.ID = id;
        setModel(model);
        setPricePerDay(pricePerDay);
        setCurrentReservation(null);
    }

    public abstract String getType();

    public boolean isAvailable() {
        return currentReservation == null;
    }

    @Override
    public boolean startRental(Reservation reservation) {
        if (!isAvailable()) {
            return false;
        }
        currentReservation = reservation;
        reservation.setVehicle(this);
        reservation.setStatus("Active");
        return true;
    }

    @Override
    public Reservation endRental() {
        if (isAvailable()) {
            return null;
        }
        Reservation finished = currentReservation;
        finished.setStatus("Completed");
        currentReservation = null;
        return finished;
    }

    public int getId() {
        return ID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Reservation getCurrentReservation() {
        return currentReservation;
    }

    public void setCurrentReservation(Reservation currentReservation) {
        this.currentReservation = currentReservation;
    }

    @Override
    public String toString() {
        String state;
        if (isAvailable()) {
            state = "Available";
        } else {
            state = "Rented by " + currentReservation.getRenter() + " for " + currentReservation.getDays() + " day(s)";
        }
        return String.format("#%d %s (%s) | %.2f per day | %s", getId(), getModel(), getType(), getPricePerDay(), state);
    }
}
