# Hotel Automation System 🏨

A comprehensive desktop application developed for the Software Engineering / System Design Lab. This project automates the core activities of a 5-star hotel, including room management, guest reservations, catering services, and billing.

The system is built using **Java Swing** for the graphical user interface and **PostgreSQL** for robust, relational database management, strictly adhering to the Model-View-Controller (MVC) architectural pattern.

## 👥 Team Members
* **Suyash Pandey** (2023UG1100)
* **Himanshu Raj** (2023UG1092)
* **Suraj Kumar** (2023UG1106)
* **Satyendra Kumar** (2023UG1105)

## ✨ Core Features
1. **Dynamic Dashboard:** Displays real-time hotel occupancy rates and allows the manager to globally revise room tariffs by a specific percentage based on demand.
2. **Booking & Reservations:** Enables receptionists to assign rooms (Single/Double, AC/Non-AC) to guests, record advance payments, and automatically generate a unique Guest Token Number upon successful allocation.
3. **Catering Integration:** Allows the catering manager to log food and beverage orders directly to a specific guest's token number.
4. **Automated Checkout & Billing:** Aggregates room charges (based on duration and base rate) and catering expenses. Automatically applies a 10% discount for frequent guests holding an Identity Number, deducts advance payments, and generates a formatted final receipt while freeing up the room in the database.

## 🛠️ Tech Stack
* **Language:** Java (JDK 17+)
* **GUI Framework:** Java Swing / AWT
* **Database:** PostgreSQL
* **Build Tool & Dependency Management:** Maven
* **Database Connectivity:** JDBC (`org.postgresql:postgresql`)

## 🏗️ Project Architecture (MVC)
The codebase is strictly separated to ensure maintainability and traceability to the Software Requirements Specification (SRS):
* `com.hotel.models`: Core data entities.
* `com.hotel.views`: Swing UI components (`MainFrame`, `BookingPanel`, etc.).
* `com.hotel.controllers`: Business logic processing and database query execution.
* `com.hotel.db`: Centralized JDBC connection management.

## 🚀 Setup & Installation Instructions

### 1. Database Configuration
1. Install PostgreSQL and pgAdmin.
2. Create a new database named `hotel_db`.
3. Open a Query Tool in `hotel_db` and execute the schema provided in `database_schema.sql` (or refer to the `CREATE TABLE` commands used in the controllers) to generate the `rooms`, `guests`, and `catering_orders` tables.
4. Insert mock data into the `rooms` table to initialize the hotel inventory.

### 2. Application Configuration
1. Clone this repository: `git clone <repository-url>`
2. Open the project in your preferred Java IDE (IntelliJ IDEA / Eclipse).
3. Navigate to `src/main/java/com/hotel/db/DatabaseHelper.java`.
4. Update the `PASSWORD` variable with your local PostgreSQL password.

### 3. Execution
Run the `Main.java` file located in `src/main/java/com/hotel/Main.java`. Maven will automatically resolve the required PostgreSQL JDBC driver, and the Swing GUI will launch.
