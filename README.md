# PahanaEdu Billing System

A comprehensive **web-based billing and inventory management system** built for **PahanaEdu Bookshop**. This system streamlines billing, stock management, and reporting, with robust role-based access control for Admin and Cashier users.

**GitHub Repository**: [Pahana Billing System](https://github.com/UpekshikaS/pahana-billing-system.git)

---

## Table of Contents

- [Features](#features)  
- [Technologies Used](#technologies-used)  
- [Getting Started](#getting-started)  
- [Installation](#installation)  
- [Database Setup](#database-setup)  
- [Project Structure](#project-structure)  
- [Future Enhancements](#future-enhancements)  
- [License](#license)  
- [Author](#author)

---

## Features

### User Authentication & Authorization
- Secure login/logout system  
- Role-based access control (Admin & Cashier)

### Customer Management
- Full CRUD operations on customer records  
- Search functionality by customer ID or name

### Inventory Management
- Add, update, and track product inventory  
- Bulk stock upload via Excel  
- Real-time stock checks during billing

### Billing & Invoicing
- Invoice creation with dynamic product selection and quantity  
- Discount options and automatic total calculation  
- Invoice printing and Excel export support

### Payments
- Supports both cash and card payments  
- Automatic invoice status updates upon payment

### Returns & Notifications
- Manage product returns with stock adjustments  
- System alerts for low stock and other events

### Reporting
- Daily, monthly, and custom-range sales reports  
- Analytical insights for inventory and sales trends
---

## Technologies Used

This system is designed with a scalable and modular three-tier architecture:

### Backend
- **Java (Jakarta EE 10)** – RESTful APIs and business logic  
- **Maven** – Dependency and build management  

### Frontend
- **HTML5 & CSS3** – Responsive and accessible user interface  
- **JavaScript** – Dynamic UI behavior and event handling  

### Database
- **MySQL 8.0** – Relational database for storing persistent data  

### Server
- **GlassFish 7.0** – Jakarta EE application server  

### Design Patterns
- **MVC** – Separation of concerns (Model-View-Controller)  
- **DAO** – Data abstraction and encapsulation  
- **DTO** – Efficient data transfer between layers  

---

## Getting Started

To get a local development copy of the project up and running, follow these steps:

### Prerequisites

Ensure the following are installed on your machine:

- Java JDK 11 or higher  
- Apache Maven  
- MySQL Server 8.0 or higher  
- NetBeans IDE (with GlassFish 7.0 support)

---

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/UpekshikaS/pahana-billing-system.git
cd pahana-billing-system
```

### 2. Set Up the Database

- Create a new database in MySQL (e.g., `pahana_edu_bookshop`)
- Execute the SQL script located in `db queries/Pahana_Edu_Book_Shop.sql` to initialize tables and sample data

### 3. Open the Project in NetBeans

- Launch NetBeans IDE  
- Go to `File > Open Project`, then select the cloned folder  
- The IDE will recognize it as a Maven project

### 4. Configure and Start GlassFish Server

- Open the **Services** tab  
- Right-click on **GlassFish Server** > `Start`
  ![Run glass fish server](https://github.com/user-attachments/assets/beade3cb-e38e-47a4-a1af-4530bce4aaa5)
- Ensure the server is properly configured to connect to the project

### 5. Build and Run the Application

- Right-click on the **Server Project** > `Clean and Build`  
  ![Clean and Build](https://github.com/user-attachments/assets/60ed3193-bbbf-4310-a28f-c17112148b1c)

- Do the same for the **Web Project**  
- Right-click the **Web Project** > `Run` to deploy the application
  <img width="1908" height="1003" alt="Run server" src="https://github.com/user-attachments/assets/56c391cf-9c20-41a7-a135-1a368080ed55" />
  ![Run Client](https://github.com/user-attachments/assets/0d46d2ca-6b1c-4099-8713-c42c37be3616)
- The application will open in your default browser


---

## Future Enhancements

- Integration with online payment gateways  
- Barcode scanner support for faster billing  
- Advanced analytics dashboards using BI tools  
- Multi-factor authentication for improved security  
- Admin tools for database backup and restore

---


## Author

**Upekshika Sewwandi**  
[GitHub Profile](https://github.com/UpekshikaS)

---
