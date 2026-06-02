package com.nguyenquocthinh.models;

import java.util.ArrayList;
import java.util.List;

public class DataWareHouse {
    public static List<Category> categories = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<Customer> customers = new ArrayList<>();
    public static List<Department> departments = new ArrayList<>();
    public static List<Employee> employees = new ArrayList<>();
    public static List<Order> orders = new ArrayList<>();
    public static List<OrderDetail> orderDetails = new ArrayList<>();

    static {
        // Build Categories
        categories.add(new Category("C001", "Phone"));
        categories.add(new Category("C002", "Laptop"));
        categories.add(new Category("C003", "Accessory"));

        // Build Departments
        departments.add(new Department("D001", "Sales & Marketing"));
        departments.add(new Department("D002", "Technical Operations"));
        departments.add(new Department("D003", "Human Resources"));

        // Build Initial Employees
        employees.add(new Employee("E001", "Nguyen Van A", "D001", "Director", 2500.0));
        employees.add(new Employee("E002", "Tran Thi B", "D002", "Engineer", 2000.0));
        employees.add(new Employee("E003", "Le Van C", "D003", "Staff", 1500.0));

        // Build Customers
        customers.add(new Customer("CU001", "Nguyen Quoc Thinh", "0901234567", "Ho Chi Minh City"));
        customers.add(new Customer("CU002", "Tran Quoc Bao", "0912345678", "Ha Noi"));
        customers.add(new Customer("CU003", "Le Quang Sang", "0923456789", "Da Nang"));
        customers.add(new Customer("CU004", "Pham Minh Quan", "0934567890", "Can Tho"));
        customers.add(new Customer("CU005", "Vu Hoang Nam", "0945678901", "Hai Phong"));
        customers.add(new Customer("CU006", "Dang Viet Dung", "0956789012", "Nha Trang"));
        customers.add(new Customer("CU007", "Bui Thanh Long", "0967890123", "Hue"));
        customers.add(new Customer("CU008", "Do Gia Tri", "0978901234", "Vung Tau"));
        customers.add(new Customer("CU009", "Ngo Hoang Phuc", "0989012345", "Da Lat"));
        customers.add(new Customer("CU010", "Nguyen Van Toan", "0990123456", "Can Tho"));

        // Build Products
        products.add(new Product("P001", "iPhone 15 Pro Max", 1200.0, "C001"));
        products.add(new Product("P002", "Samsung Galaxy S24 Ultra", 1150.0, "C001"));
        products.add(new Product("P003", "Xiaomi 14 Ultra", 990.0, "C001"));
        products.add(new Product("P004", "Google Pixel 8 Pro", 899.0, "C001"));
        products.add(new Product("P005", "MacBook Pro M3", 1999.0, "C002"));
        products.add(new Product("P006", "Dell XPS 15", 1799.0, "C002"));
        products.add(new Product("P007", "Asus ROG Strix", 1499.0, "C002"));
        products.add(new Product("P008", "HP Spectre x360", 1350.0, "C002"));
        products.add(new Product("P009", "AirPods Pro 2", 249.0, "C003"));
        products.add(new Product("P010", "Anker PowerBank 24K", 149.0, "C003"));
        products.add(new Product("P011", "Keychron K2 Keyboard", 99.0, "C003"));
        products.add(new Product("P012", "Logitech MX Master 3S", 99.0, "C003"));

        // Build 50 Orders and Order Details
        String[] statuses = {"Complete", "Not Payment", "On Logistic", "Customer Complaint"};
        for (int i = 1; i <= 50; i++) {
            String orderId = String.format("O%03d", i);
            
            // Year: alternating between 2025 and 2026
            int year = (i % 2 == 0) ? 2025 : 2026;
            // Month: distributed between 1 and 12
            int month = (i % 12) + 1;
            // Day: distributed between 1 and 28
            int day = (i % 28) + 1;
            String dateStr = String.format("%04d-%02d-%02d", year, month, day);

            // Assign Customer and Employee deterministically
            String custId = String.format("CU%03d", (i % 10) + 1);
            String empId = String.format("E%03d", (i % 3) + 1);
            String status = statuses[i % 4];

            orders.add(new Order(orderId, dateStr, custId, empId, status));

            // Generate 1-2 details for each order
            int detailsCount = (i % 2) + 1; // 1 or 2 products
            for (int d = 0; d < detailsCount; d++) {
                int prodIndex = (i + d) % 12;
                Product product = products.get(prodIndex);
                int qty = ((i + d) % 4) + 1; // 1 to 4
                orderDetails.add(new OrderDetail(orderId, product.getId(), qty, product.getPrice()));
            }
        }
    }

    public static List<Order> getOrders() {
        return orders;
    }

    public static List<OrderDetail> getOrderDetailsForOrder(String orderId) {
        List<OrderDetail> list = new ArrayList<>();
        for (OrderDetail od : orderDetails) {
            if (od.getOrderId().equals(orderId)) {
                list.add(od);
            }
        }
        return list;
    }

    public static Product getProductById(String prodId) {
        for (Product p : products) {
            if (p.getId().equals(prodId)) {
                return p;
            }
        }
        return null;
    }

    public static Customer getCustomerById(String custId) {
        for (Customer c : customers) {
            if (c.getId().equals(custId)) {
                return c;
            }
        }
        return null;
    }

    public static Employee getEmployeeById(String empId) {
        for (Employee e : employees) {
            if (e.getId().equals(empId)) {
                return e;
            }
        }
        return null;
    }

    public static Department getDepartmentById(String depId) {
        for (Department d : departments) {
            if (d.getId().equals(depId)) {
                return d;
            }
        }
        return null;
    }

    public static List<Department> getDepartments() {
        return departments;
    }

    public static List<Employee> getEmployees() {
        return employees;
    }

    public static void addEmployee(Employee emp) {
        employees.add(emp);
    }

    public static void removeEmployee(String empId) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(empId)) {
                employees.remove(i);
                break;
            }
        }
    }
}
