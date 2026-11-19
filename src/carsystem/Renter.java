
package carsystem;


public class Renter {

    private final int ID;
    private String name;

    public Renter() {
        this(0, "");
    }

    public Renter(int id, String name) {
        this.ID = id;
        setName(name);
    }

    public int getId() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }
}
