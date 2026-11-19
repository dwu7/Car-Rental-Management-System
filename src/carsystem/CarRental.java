
package carsystem;

  
import java.util.InputMismatchException;
import java.util.Scanner;

public class CarRental {

    static Scanner input = new Scanner(System.in);
    static SinglyLinkedList<Vehicle> fleet = new SinglyLinkedList<>();
    static LLQueue<Reservation> waitingList = new LLQueue<>();

    public static void main(String[] args) {
        seedDemoData();
        runConsole();
    }

    static void seedDemoData() {
        Vehicle v1 = new EconomyCar(101, "Toyota Yaris", 120, 10);
        Vehicle v2 = new EconomyCar(102, "Hyundai Accent", 110, 10);
        Vehicle v3 = new LuxuryCar(103, "Lexus ES", 450, 500);
        Vehicle v4 = new LuxuryCar(104, "Mercedes E200", 600, 800);

        fleet.addLast(v1);
        fleet.addLast(v2);
        fleet.addLast(v3);
        fleet.addLast(v4);

        v2.startRental(new Reservation(new Renter(201, "Sara"), "Economy", 3));
        v3.startRental(new Reservation(new Renter(202, "Noura"), "Luxury", 2));
        v4.startRental(new Reservation(new Renter(203, "Reem"), "Luxury", 5));

        waitingList.enqueue(new Reservation(new Renter(204, "Lama"), "Luxury", 2));
        waitingList.enqueue(new Reservation(new Renter(205, "Huda"), "Luxury", 4));
    }

    private static void runConsole() {
        System.out.println("*** Welcome To Car Rental Management System ***");
        int option = 0;
        do {
            try {
                System.out.print("\n"
                        + "--- Fleet ---\n"
                        + "1.  Add Vehicle\n"
                        + "2.  Display Fleet\n"
                        + "3.  Search Available Vehicles (type and price)\n"
                        + "4.  Remove Vehicle\n"
                        + "--- Rent and Return ---\n"
                        + "5.  Rent a Vehicle by Id\n"
                        + "6.  Rent the Cheapest Vehicle of a Type\n"
                        + "7.  Return a Vehicle\n"
                        + "--- Waiting List ---\n"
                        + "8.  Join the Waiting List\n"
                        + "9.  Display the Waiting List\n"
                        + "10. Change the Wanted Type (keep my place)\n"
                        + "11. Leave the Waiting List\n"
                        + "12. Exit\n"
                        + ">> ");
                option = input.nextInt();
                switch (option) {

                    case 1:
                        addVehicle();
                        break;

                    case 2:
                        System.out.println("---- Fleet (" + fleet.size() + " vehicles) ----");
                        fleet.printList();
                        break;

                    case 3:
                        System.out.print("Type (1 = Economy, 2 = Luxury, 3 = All): ");
                        int t = input.nextInt();
                        String type;
                        if (t == 1) {
                            type = "Economy";
                        } else if (t == 2) {
                            type = "Luxury";
                        } else {
                            type = "All";
                        }
                        System.out.print("Minimum price per day: ");
                        double min = input.nextDouble();
                        System.out.print("Maximum price per day: ");
                        double max = input.nextDouble();
                        int found = fleet.listAvailable(type, min, max);
                        System.out.println(found + " available vehicle(s) found.");
                        break;

                    case 4:
                        System.out.print("Vehicle id: ");
                        int id = input.nextInt();
                        Vehicle v = fleet.searchVehicle(id);
                        if (v == null) {
                            System.out.println("Vehicle " + id + " is not found.");
                            break;
                        }
                        if (!v.isAvailable()) {
                            System.out.println("Vehicle " + id + " is rented now, it can not be removed.");
                            break;
                        }
                        fleet.deleteVehicle(id);
                        System.out.println("Vehicle " + id + " has been removed.");
                        break;

                    case 5:
                        System.out.print("Vehicle id: ");
                        id = input.nextInt();
                        v = fleet.searchVehicle(id);
                        if (v == null) {
                            System.out.println("Vehicle " + id + " is not found.");
                            break;
                        }
                        if (!v.isAvailable()) {
                            System.out.println("Vehicle " + id + " is rented now. You can join the waiting list (option 8).");
                            break;
                        }
                        System.out.print("Renter id: ");
                        int renterId = input.nextInt();
                        input.nextLine();
                        System.out.print("Renter name: ");
                        String name = input.nextLine();
                        System.out.print("Number of days: ");
                        int days = input.nextInt();
                        Reservation r = new Reservation(new Renter(renterId, name), v.getType(), days);
                        v.startRental(r);
                        System.out.println("Rented: " + r);
                        break;

                    case 6:
                        System.out.print("Type (1 = Economy, 2 = Luxury): ");
                        if (input.nextInt() == 1) {
                            type = "Economy";
                        } else {
                            type = "Luxury";
                        }
                        v = fleet.cheapestAvailable(type);
                        if (v == null) {
                            System.out.println("All the " + type + " vehicles are rented. You can join the waiting list (option 8).");
                            break;
                        }
                        System.out.println("The cheapest available " + type + " vehicle: " + v);
                        System.out.print("Renter id: ");
                        renterId = input.nextInt();
                        input.nextLine();
                        System.out.print("Renter name: ");
                        name = input.nextLine();
                        System.out.print("Number of days: ");
                        days = input.nextInt();
                        r = new Reservation(new Renter(renterId, name), type, days);
                        v.startRental(r);
                        System.out.println("Rented: " + r);
                        break;

                    case 7:
                        System.out.print("Vehicle id: ");
                        id = input.nextInt();
                        v = fleet.searchVehicle(id);
                        if (v == null) {
                            System.out.println("Vehicle " + id + " is not found.");
                            break;
                        }
                        Reservation done = v.endRental();
                        if (done == null) {
                            System.out.println("Vehicle " + id + " is not rented.");
                            break;
                        }
                        System.out.println("Returned: " + done);
                        System.out.printf("Amount to pay: %.2f\n", done.getTotalPrice());

                        Reservation next = assignNext(v);
                        if (next != null) {
                            System.out.println("The first renter waiting for a " + v.getType() + " vehicle gets it: " + next);
                        }
                        break;

                    case 8:
                        System.out.print("Type (1 = Economy, 2 = Luxury): ");
                        if (input.nextInt() == 1) {
                            type = "Economy";
                        } else {
                            type = "Luxury";
                        }
                        if (fleet.cheapestAvailable(type) != null) {
                            System.out.println("A " + type + " vehicle is available now, you can rent it (option 6).");
                            break;
                        }
                        System.out.print("Renter id: ");
                        renterId = input.nextInt();
                        input.nextLine();
                        System.out.print("Renter name: ");
                        name = input.nextLine();
                        System.out.print("Number of days: ");
                        days = input.nextInt();
                        r = new Reservation(new Renter(renterId, name), type, days);
                        waitingList.enqueue(r);
                        System.out.println("Added to the waiting list at position " + waitingList.size() + ": " + r);
                        break;

                    case 9:
                        System.out.println("---- Waiting List (" + waitingList.size() + ") ----");
                        waitingList.Display();
                        break;

                    case 10:
                        System.out.print("Renter id: ");
                        renterId = input.nextInt();
                        System.out.print("New type (1 = Economy, 2 = Luxury): ");
                        if (input.nextInt() == 1) {
                            type = "Economy";
                        } else {
                            type = "Luxury";
                        }
                        Reservation changed = waitingList.changeWantedType(renterId, type);
                        if (changed == null) {
                            System.out.println("Renter " + renterId + " is not in the waiting list.");
                            break;
                        }
                        System.out.println("Changed: " + changed);
                        if (fleet.cheapestAvailable(type) != null) {
                            System.out.println("A " + type + " vehicle is available now, you can rent it (option 6).");
                        }
                        break;

                    case 11:
                        System.out.print("Renter id: ");
                        renterId = input.nextInt();
                        Reservation removed = waitingList.leaveLine(renterId);
                        if (removed == null) {
                            System.out.println("Renter " + renterId + " is not in the waiting list.");
                        } else {
                            System.out.println("Left the waiting list: " + removed);
                        }
                        break;

                    case 12:
                        System.out.println("Thank you!");
                        break;

                    default:
                        System.out.println("Invalid option!");
                }
            } catch (InputMismatchException ex) {
                System.out.println("invalid input!");
                input.nextLine();
            }

        } while (option != 12);

    }

    public static void addVehicle() {
        System.out.print("Vehicle id: ");
        int id = input.nextInt();
        if (fleet.searchVehicle(id) != null) {
            System.out.println("Vehicle " + id + " already exists.");
            return;
        }
        input.nextLine();
        System.out.print("Model: ");
        String model = input.nextLine();
        System.out.print("Price per day: ");
        double price = input.nextDouble();
        System.out.print("Type (1 = Economy, 2 = Luxury): ");
        int type = input.nextInt();
        Vehicle v;
        if (type == 1) {
            System.out.print("Discount for 7 days or more (%): ");
            v = new EconomyCar(id, model, price, input.nextDouble());
        } else {
            System.out.print("Deposit: ");
            v = new LuxuryCar(id, model, price, input.nextDouble());
        }
        fleet.addLast(v);
        System.out.println("Added: " + v);

        // a renter who waits for this type gets the new vehicle
        Reservation next = assignNext(v);
        if (next != null) {
            System.out.println("The first renter waiting for a " + v.getType() + " vehicle gets it: " + next);
        }
    }

    static Reservation assignNext(Vehicle vehicle) {
        Reservation next = waitingList.takeFirstFor(vehicle.getType());
        if (next != null) {
            vehicle.startRental(next);
        }
        return next;
    }
}
  
