package com.nguyenquocthinh.models;

import java.io.Serializable;

public class Employee implements Serializable {
    private String id;
    private String name;
    private String departmentId; // maps to Department id
    private String role;
    private double salary;

    public Employee(String id, String name, String departmentId, String role, double salary) {
        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
        this.role = role;
        this.salary = salary;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
