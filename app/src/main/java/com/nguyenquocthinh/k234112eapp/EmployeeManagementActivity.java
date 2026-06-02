package com.nguyenquocthinh.k234112eapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Department;
import com.nguyenquocthinh.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementActivity extends AppCompatActivity {

    private ListView lvSimpleEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        lvSimpleEmployees = findViewById(R.id.lvSimpleEmployees);

        findViewById(R.id.btnBackEmployee).setOnClickListener(v -> finish());

        // Fill simple details listing
        List<String> simpleList = new ArrayList<>();
        for (Employee emp : DataWareHouse.getEmployees()) {
            Department dept = DataWareHouse.getDepartmentById(emp.getDepartmentId());
            String deptName = (dept != null) ? dept.getName() : "None";
            simpleList.add(emp.getName() + " (" + emp.getRole() + " • " + deptName + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, simpleList);
        lvSimpleEmployees.setAdapter(adapter);
    }
}
