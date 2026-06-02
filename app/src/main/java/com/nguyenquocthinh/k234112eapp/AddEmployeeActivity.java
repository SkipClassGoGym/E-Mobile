package com.nguyenquocthinh.k234112eapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Department;
import com.nguyenquocthinh.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText editEmpId;
    private EditText editEmpName;
    private Spinner spEmpDept;
    private EditText editEmpRole;
    private EditText editEmpSalary;
    private Button btnSaveEmployee;
    private Button btnCancelAdd;

    private List<String> deptNames = new ArrayList<>();
    private List<String> deptIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        editEmpId = findViewById(R.id.editEmpId);
        editEmpName = findViewById(R.id.editEmpName);
        spEmpDept = findViewById(R.id.spEmpDept);
        editEmpRole = findViewById(R.id.editEmpRole);
        editEmpSalary = findViewById(R.id.editEmpSalary);
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee);
        btnCancelAdd = findViewById(R.id.btnCancelAdd);

        // Populate department names spinner values
        for (Department dept : DataWareHouse.getDepartments()) {
            deptNames.add(dept.getName());
            deptIds.add(dept.getId());
        }

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deptNames);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmpDept.setAdapter(deptAdapter);

        // Cancel click operations
        btnCancelAdd.setOnClickListener(v -> finish());

        // Save operations details
        btnSaveEmployee.setOnClickListener(v -> performSaveEmployee());
    }

    private void performSaveEmployee() {
        String empId = editEmpId.getText().toString().trim().toUpperCase();
        String name = editEmpName.getText().toString().trim();
        String role = editEmpRole.getText().toString().trim();
        String salaryStr = editEmpSalary.getText().toString().trim();

        if (empId.isEmpty() || name.isEmpty() || role.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all employee fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate ID format (must match E00X or start with E)
        if (!empId.startsWith("E")) {
            Toast.makeText(this, "Employee ID must start with capital letter 'E'!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if employee ID already present to guarantee uniqueness
        if (DataWareHouse.getEmployeeById(empId) != null) {
            Toast.makeText(this, "An employee profile with ID " + empId + " already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary <= 0) {
                Toast.makeText(this, "Salary must be a positive decimal number!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid salary value! Please enter a decimal format numeric.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Grab selection department
        String selectedDeptId = deptIds.get(spEmpDept.getSelectedItemPosition());

        // Create and save item
        Employee newEmployee = new Employee(empId, name, selectedDeptId, role, salary);
        DataWareHouse.addEmployee(newEmployee);

        Toast.makeText(this, "Employee successfully registered!", Toast.LENGTH_SHORT).show();
        finish(); // Back to main hub list
    }
}
