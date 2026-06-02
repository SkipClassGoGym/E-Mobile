package com.nguyenquocthinh.models;

import java.io.Serializable;

public class Order implements Serializable {
    private String orderId;
    private String date; // format "yyyy-MM-dd"
    private String customerId;
    private String employeeId;
    private String status; // "Complete", "Not Payment", "On Logistic", "Customer Complaint"

    public Order(String orderId, String date, String customerId, String employeeId, String status) {
        this.orderId = orderId;
        this.date = date;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
