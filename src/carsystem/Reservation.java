
package carsystem;


public class Reservation {

    private static int nextId = 1;
    private final int ID;
    private Renter renter;
    private String wantedType;
    private Vehicle vehicle;
    private int days;
    private String status;

    public Reservation() {
        this(null, "", 0);
    }

    public Reservation(Renter renter, String wantedType, int days) {
        this.ID = nextId;
        nextId++;
        setRenter(renter);
        setWantedType(wantedType);
        setVehicle(null);
        setDays(days);
        setStatus("Waiting");
    }

    public double getTotalPrice() {
        if (vehicle == null) {
            return 0;
        }
        return vehicle.calcRentalPrice(days);
    }

    public int getId() {
        return ID;
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public String getWantedType() {
        return wantedType;
    }

    public void setWantedType(String wantedType) {
        this.wantedType = wantedType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String text = String.format("Reservation %d | %s | %s | %d day(s) | %s", getId(), getRenter(), getWantedType(), getDays(), getStatus());
        if (vehicle != null) {
            text += String.format(" | vehicle #%d | total %.2f", vehicle.getId(), getTotalPrice());
        }
        return text;
    }
}
