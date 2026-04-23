# Hotel Automation System 🏨

A comprehensive desktop application developed for the Software Engineering / System Design Lab. This project automates the core activities of a 5-star hotel, including room management, guest reservations, catering services, and billing.

The system is built using **Java Swing** for the graphical user interface and **PostgreSQL** for robust, relational database management, strictly adhering to the requirements outlined in our Software Requirements Specification (SRS) and Structured Analysis & Design (SA/SD) documents.

## 👥 Team Members
* **Suyash Pandey** (2023UG1100)
* **Suraj Kumar** (2023UG1106)
* **Himanshu Raj** (2023UG1092)
* **Satyendra Kumar** (2023UG1105)

## ✨ Core Features
1. **Manager / Admin Module:** Displays real-time hotel occupancy rates for any given month. Allows the manager to globally revise room tariffs by a percentage and maintains a historical audit log of all price changes.
2. **Booking & Reservations:** Enables receptionists to assign rooms (Single/Double, AC/Non-AC) to guests and record advance payments. Dynamically checks availability to prevent double-booking and automatically generates a unique Guest Token Number upon successful allocation.
3. **Catering Integration:** Fetches live menu items dynamically from the database. Allows the catering manager to validate active guest tokens and log food/beverage orders directly to a specific guest's account.
4. **Automated Checkout & Billing:** Aggregates room charges (based on duration and the specific tariff at the time of check-in) and catering expenses. Automatically applies custom discount percentages for frequent guests, deducts advance payments, generates a formatted final receipt, and frees up the room in the database.

## 🛠️ Tech Stack
* **Language:** Java (JDK 17+)
* **GUI Framework:** Java Swing
* **Database:** PostgreSQL
* **Build Tool:** Maven (for JDBC driver management)
* **Database Connectivity:** JDBC (`org.postgresql:postgresql`)

## 🏗️ Project Architecture (MVC)
The codebase is strictly modularized into distinct layers as derived from our Transaction Analysis (SA/SD):

```text
src/main/java/com/hotel/
├── app/                  # Application Entry Point (Main.java)
├── controller/           # Middleman routing & DTOs (DispatchTransaction.java)
├── db/                   # Database access & JDBC queries (DBAccessLayer.java)
├── logic/                # Core Business Logic (Booking, Checkout, Billing, Admin math)
├── model/                # Data Transfer Objects (Room, Reservation, CateringOrder)
└── ui/                   # Java Swing Panels (Reception, Catering, Checkout, Admin)