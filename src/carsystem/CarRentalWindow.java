package carsystem;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public final class CarRentalWindow extends Application {
    private Stage window;
    private static final String ECONOMY = "Economy";
    private static final String LUXURY = "Luxury";

    private final TableView<Object[]> fleet = table("Vehicle ID", "Vehicle", "Category", "Daily Rate", "Status");
    private final TableView<Object[]> rentals = table("Vehicle ID", "Vehicle", "Renter", "Days");
    private final TableView<Object[]> waiting = table("Position", "Renter ID", "Renter", "Category", "Days");
    private final Label fleetCount = new Label();
    private final Label rentalCount = new Label();
    private final Label waitingCount = new Label();
    private String filterType;
    private double minPrice;
    private double maxPrice;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        window = stage;
        CarRental.seedDemoData();
        Label title = new Label("CarSystem  |  Car Rental Management");

        TabPane tabs = new TabPane(
                new Tab("Fleet", page(fleet, fleetCount,
                        button("Add Vehicle", this::addVehicle),
                        button("Search by Category and Price", this::filterFleet),
                        button("Show All", () -> filterType = null),
                        button("Remove Selected Vehicle", this::removeVehicle))),
                new Tab("Rentals and Returns", page(rentals, rentalCount,
                        button("Rent by Vehicle ID", this::rentById),
                        button("Rent Cheapest by Category", this::rentCheapest),
                        button("Return Selected Vehicle", this::returnVehicle))),
                new Tab("Waiting List", page(waiting, waitingCount,
                        button("Add Request", this::joinWaiting),
                        button("Change Selected Category", this::changeType),
                        button("Remove Selected Request", this::leaveWaiting))));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox root = new VBox(14, title, tabs);
        root.setPadding(new Insets(18, 22, 18, 22));
        VBox.setVgrow(tabs, Priority.ALWAYS);
        refresh();
        stage.setTitle("CarSystem | Car Rental Management");
        stage.setMinWidth(760);
        stage.setMinHeight(380);
        stage.setScene(new Scene(root, 940, 510));
        stage.show();
    }

    private static TableView<Object[]> table(String... titles) {
        TableView<Object[]> table = new TableView<>();
        table.setFixedCellSize(35);
        table.setPlaceholder(new Label("No records to display"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        for (int index = 0; index < titles.length; index++) {
            int columnIndex = index;
            TableColumn<Object[], Object> column = new TableColumn<>(titles[index]);
            column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue()[columnIndex]));
            column.setSortable(false);
            table.getColumns().add(column);
        }
        return table;
    }

    private static VBox page(TableView<Object[]> table, Label count, Button... buttons) {
        FlowPane actions = new FlowPane(8, 8, buttons);
        VBox panel = new VBox(12, count, actions, table);
        panel.setPadding(new Insets(14, 12, 12, 12));
        VBox.setVgrow(table, Priority.ALWAYS);
        return panel;
    }

    private Button button(String label, Runnable action) {
        Button button = new Button(label);
        button.setOnAction(event -> {
            try {
                action.run();
                refresh();
            } catch (IllegalArgumentException error) {
                message(error.getMessage());
            }
        });
        return button;
    }

    private void refresh() {
        ObservableList<Object[]> fleetRows = fleet.getItems();
        ObservableList<Object[]> rentalRows = rentals.getItems();
        ObservableList<Object[]> queueRows = waiting.getItems();
        fleetRows.clear();
        rentalRows.clear();
        queueRows.clear();
        int available = 0;
        for (SinglyLinkedList.Node<Vehicle> node = CarRental.fleet.head;
                node != null; node = node.getNext()) {
            Vehicle vehicle = node.getElement();
            if (vehicle.isAvailable()) {
                available++;
            } else {
                Reservation rental = vehicle.getCurrentReservation();
                rentalRows.add(new Object[]{vehicle.getId(), vehicle.getModel(),
                    rental.getRenter(), rental.getDays()});
            }
            if (matchesFilter(vehicle)) {
                fleetRows.add(new Object[]{vehicle.getId(), vehicle.getModel(),
                    vehicle.getType(), amount(vehicle.getPricePerDay()),
                    vehicle.isAvailable() ? "Available" : "Rented"});
            }
        }
        int position = 1;
        for (LLQueue.Node<Reservation> node = CarRental.waitingList.front;
                node != null; node = node.getNext()) {
            Reservation request = node.getElement();
            queueRows.add(new Object[]{position++, request.getRenter().getId(),
                request.getRenter().getName(), request.getWantedType(), request.getDays()});
        }
        fleetCount.setText(filterType == null
                ? "Total vehicles: " + CarRental.fleet.size() + "   |   Available: " + available
                : "Matching available vehicles: " + fleetRows.size());
        rentalCount.setText("Active rentals: " + rentalRows.size());
        waitingCount.setText("Waiting requests: " + queueRows.size());
    }

    private boolean matchesFilter(Vehicle vehicle) {
        return filterType == null || (vehicle.isAvailable()
                && (filterType.equals("All") || filterType.equals(vehicle.getType()))
                && vehicle.getPricePerDay() >= minPrice && vehicle.getPricePerDay() <= maxPrice);
    }


    private void filterFleet() {
        ComboBox<String> type = typePicker("All", ECONOMY, LUXURY);
        TextField min = new TextField("0");
        TextField max = new TextField();
        form("Find an Available Vehicle", () -> {
            double lower = decimal(min, "Minimum Daily Rate");
            double upper = decimal(max, "Maximum Daily Rate");
            require(lower <= upper, "The maximum daily rate must be at least the minimum.");
            minPrice = lower;
            maxPrice = upper;
            filterType = type.getValue();
        }, "Category", type, "Minimum Daily Rate", min, "Maximum Daily Rate", max);
    }

    private void addVehicle() {
        TextField id = new TextField();
        TextField name = new TextField();
        TextField price = new TextField();
        ComboBox<String> type = types();
        TextField extra = new TextField();
        Label extraLabel = new Label("Discount for 7+ days (%)");
        type.setOnAction(event -> extraLabel.setText(type.getSelectionModel().getSelectedIndex() == 0
                ? "Discount for 7+ days (%)" : "Security Deposit"));
        form("Add Vehicle", () -> {
            int number = positiveInt(id, "Vehicle ID");
            require(CarRental.fleet.searchVehicle(number) == null, "This vehicle ID is already in use.");
            String vehicleModel = requiredText(name, "Vehicle Model");
            double dailyRate = decimal(price, "Daily Rate");
            double value = decimal(extra, extraLabel.getText());
            Vehicle vehicle;
            if (type.getSelectionModel().getSelectedIndex() == 0) {
                require(value <= 100, "The discount must be between 0% and 100%.");
                vehicle = new EconomyCar(number, vehicleModel, dailyRate, value);
            } else {
                vehicle = new LuxuryCar(number, vehicleModel, dailyRate, value);
            }
            CarRental.fleet.addLast(vehicle);
            Reservation next = CarRental.assignNext(vehicle);
            filterType = null;
            message("Vehicle added." + assignment(next));
        }, "Vehicle ID", id, "Vehicle Model", name, "Daily Rate", price,
                "Category", type, extraLabel, extra);
    }

    private void removeVehicle() {
        Vehicle vehicle = CarRental.fleet.searchVehicle(selectedId(fleet, 0));
        require(vehicle.isAvailable(), "Return this vehicle before removing it.");
        if (confirm("Remove " + vehicle.getModel() + "?")) {
            CarRental.fleet.deleteVehicle(vehicle.getId());
        }
    }

    private void rentById() {
        Vehicle selected = fleet.getSelectionModel().getSelectedIndex() < 0 ? null
                : CarRental.fleet.searchVehicle(selectedId(fleet, 0));
        TextField id = new TextField(selected == null ? "" : String.valueOf(selected.getId()));
        renterForm("Rent by Vehicle ID", "Vehicle ID", id, (renter, days) -> {
            Vehicle vehicle = CarRental.fleet.searchVehicle(positiveInt(id, "Vehicle ID"));
            require(vehicle != null, "Vehicle ID not found.");
            require(vehicle.isAvailable(), "This vehicle is currently rented. You can join the waiting list.");
            rent(vehicle, renter, days);
        });
    }

    private void rentCheapest() {
        ComboBox<String> type = types();
        renterForm("Rent Cheapest by Category", "Category", type, (renter, days) -> {
            Vehicle vehicle = CarRental.fleet.cheapestAvailable(type.getValue());
            require(vehicle != null, "No vehicles are available in this category. You can join the waiting list.");
            rent(vehicle, renter, days);
        });
    }

    private void rent(Vehicle vehicle, Renter renter, int days) {
        Reservation reservation = new Reservation(renter, vehicle.getType(), days);
        vehicle.startRental(reservation);
        message("Rental confirmed for " + vehicle.getModel() + "\nTotal: " + amount(reservation.getTotalPrice()));
    }

    private void returnVehicle() {
        Vehicle vehicle = CarRental.fleet.searchVehicle(selectedId(rentals, 0));
        if (confirm("Return " + vehicle.getModel() + "?")) {
            Reservation returned = vehicle.endRental();
            Reservation next = CarRental.assignNext(vehicle);
            message("Vehicle returned.\nAmount due: " + amount(returned.getTotalPrice()) + assignment(next));
        }
    }

    private void joinWaiting() {
        ComboBox<String> type = types();
        renterForm("Join Waiting List", "Requested Category", type, (renter, days) -> {
            String wanted = type.getValue();
            require(CarRental.fleet.cheapestAvailable(wanted) == null,
                    "A vehicle is available in this category. You can rent it now.");
            require(!isWaiting(renter.getId()), "This renter ID is already on the waiting list.");
            CarRental.waitingList.enqueue(new Reservation(renter, wanted, days));
            message("Request added at position " + CarRental.waitingList.size() + ".");
        });
    }

    private boolean isWaiting(int id) {
        for (LLQueue.Node<Reservation> node = CarRental.waitingList.front;
                node != null; node = node.getNext()) {
            if (node.getElement().getRenter().getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void changeType() {
        int id = selectedId(waiting, 1);
        ComboBox<String> type = types();
        type.setValue((String) waiting.getSelectionModel().getSelectedItem()[3]);
        form("Change Requested Category", () -> {
            String wanted = type.getValue();
            CarRental.waitingList.changeWantedType(id, wanted);
            String availability = CarRental.fleet.cheapestAvailable(wanted) == null ? ""
                    : "\nA vehicle is available in this category. You can rent it now.";
            message("Category updated. The request kept its place in line." + availability);
        }, "Requested Category", type);
    }

    private void leaveWaiting() {
        int id = selectedId(waiting, 1);
        if (confirm("Remove the request for renter #" + id + " from the waiting list?")) {
            CarRental.waitingList.leaveLine(id);
        }
    }

    @FunctionalInterface
    private interface RenterAction {
        void apply(Renter renter, int days);
    }

    private void renterForm(String title, String label, Node selector, RenterAction action) {
        TextField id = new TextField();
        TextField name = new TextField();
        TextField days = new TextField();
        form(title, () -> {
            Renter renter = new Renter(positiveInt(id, "Renter ID"), requiredText(name, "Renter Name"));
            action.apply(renter, positiveInt(days, "Rental Days"));
        }, label, selector, "Renter ID", id, "Renter Name", name, "Rental Days", days);
    }

    private void form(String title, Runnable submit, Object... fields) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(window);
        dialog.setTitle(title);
        DialogPane pane = dialog.getDialogPane();
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(save, cancel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        int row = 0;
        for (int index = 0; index < fields.length; index += 2) {
            Label label;
            if (fields[index] instanceof Label) {
                label = (Label) fields[index];
            } else {
                label = new Label((String) fields[index]);
            }
            Node field = (Node) fields[index + 1];
            label.setLabelFor(field);
            grid.addRow(row++, label, field);
        }
        Label error = new Label();
        error.setWrapText(true);
        VBox content = new VBox(10, grid, error);
        content.setPrefWidth(365);
        pane.setContent(content);
        pane.lookupButton(save).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                submit.run();
            } catch (IllegalArgumentException invalid) {
                error.setText(invalid.getMessage());
                event.consume();
            }
        });
        dialog.showAndWait();
    }

    private boolean confirm(String text) {
        ButtonType approve = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        return alert(Alert.AlertType.CONFIRMATION, text, approve, cancel)
                .showAndWait().filter(approve::equals).isPresent();
    }

    private void message(String text) {
        alert(Alert.AlertType.INFORMATION, text,
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE)).showAndWait();
    }

    private Alert alert(Alert.AlertType type, String text, ButtonType... buttons) {
        Alert alert = new Alert(type, text, buttons);
        alert.initOwner(window);
        alert.setTitle("CarSystem");
        alert.setHeaderText(null);
        return alert;
    }

    private static int selectedId(TableView<Object[]> table, int column) {
        Object[] selected = table.getSelectionModel().getSelectedItem();
        require(selected != null, "Select a row in the table first.");
        return (Integer) selected[column];
    }

    private static ComboBox<String> typePicker(String... options) {
        ComboBox<String> picker = new ComboBox<>();
        picker.getItems().addAll(options);
        picker.getSelectionModel().selectFirst();
        return picker;
    }

    private static ComboBox<String> types() {
        return typePicker(ECONOMY, LUXURY);
    }

    private static String assignment(Reservation next) {
        return next == null ? "" : "\nVehicle assigned to the first renter waiting for this category: "
                + next.getRenter().getName();
    }

    private static String amount(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private static String requiredText(TextField field, String label) {
        String text = field.getText().strip();
        require(!text.isEmpty(), "Enter " + label + ".");
        return text;
    }

    private static int positiveInt(TextField field, String label) {
        try {
            int value = Integer.parseInt(field.getText().strip());
            if (value > 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // The same validation message covers invalid and out-of-range values.
        }
        throw new IllegalArgumentException("Enter a positive whole number for " + label + ".");
    }

    private static double decimal(TextField field, String label) {
        StringBuilder number = new StringBuilder();
        for (char character : field.getText().strip().toCharArray()) {
            int digit = Character.digit(character, 10);
            number.append(digit >= 0 ? (char) ('0' + digit) : character == '٫' ? '.' : character);
        }
        try {
            double value = Double.parseDouble(number.toString());
            if (Double.isFinite(value) && value >= 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // The same validation message covers invalid and negative amounts.
        }
        throw new IllegalArgumentException("Enter a number of zero or more for " + label + ".");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
