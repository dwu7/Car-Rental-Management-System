Car Rental Management System
A desktop car rental management application built with Java and JavaFX, featuring a graphical interface for managing a fleet, processing rentals and returns, and organizing customer waiting requests.
Features
Fleet management
- View vehicle details, categories, daily rental rates, and availability.
- Add economy and luxury vehicles.
- Search available vehicles by category and daily price range.
- Remove vehicles that are not currently rented.
Rentals and returns
- Rent an available vehicle using its ID.
- Select the cheapest available vehicle in a category.
- Return rented vehicles and calculate rental charges.
- Automatically assign a newly available vehicle to an eligible waiting request.
Waiting list
- Add a rental request when the requested vehicle category is unavailable.
- View waiting requests in order.
- Change the requested category without losing the request's position.
- Remove a waiting request.
Technologies
- Java — application logic and object-oriented design
- JavaFX — desktop graphical user interface (TableView, TabPane, forms, and dialogs)
- Singly Linked List — vehicle fleet management
- Linked Queue — waiting-list management
- Apache NetBeans / Ant — project configuration and build
Project structure
CarSystem/
├── src/carsystem/        # Java source files, including CarRentalWindow.java
├── lib/javafx/           # Bundled JavaFX libraries
├── nbproject/            # NetBeans project configuration
└── build.xml             # Ant build file
Run the application
1. Install a compatible JDK and Apache NetBeans.
2. Open the project folder in NetBeans.
3. Ensure the JavaFX libraries in lib/javafx are compatible with your operating system and JDK. Update the module path or libraries if needed.
4. Set carsystem.CarRentalWindow as the main class (already specified in the included NetBeans configuration).
5. Build and run the project.
The JavaFX entry point is carsystem.CarRentalWindow. A separate console interface is also present in carsystem.CarRental.
Demo data
The application starts with sample vehicles, active rentals, and waiting requests for demonstration. Data is kept in memory; this project does not include persistent database storage.
Notes
This repository is an educational project demonstrating object-oriented programming, custom data structures, and a JavaFX graphical interface.
