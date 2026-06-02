package com.nguyenquocthinh.k234112eapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Department;
import com.nguyenquocthinh.models.Employee;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementAdvancedActivity extends AppCompatActivity {

    private Spinner spDepartmentFilter;
    private Button btnAddEmployee;
    private Button btnBackEmployeeAdvanced;
    private ListView lvEmployees;

    private List<Employee> filteredEmployeesList = new ArrayList<>();
    private EmployeeAdapter adapter;

    // Spinner values mappings
    private List<String> deptNames = new ArrayList<>();
    private List<String> deptIds = new ArrayList<>();
    private String selectedDeptIdFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management_advanced);

        spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnBackEmployeeAdvanced = findViewById(R.id.btnBackEmployeeAdvanced);
        lvEmployees = findViewById(R.id.lvEmployees);

        // Populate department names spinner values
        deptNames.add("All Departments");
        deptIds.add("ALL");

        for (Department dept : DataWareHouse.getDepartments()) {
            deptNames.add(dept.getName());
            deptIds.add(dept.getId());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deptNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartmentFilter.setAdapter(spinnerAdapter);

        // Spinner list selection changes
        spDepartmentFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeptIdFilter = deptIds.get(position);
                refreshEmployeeList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDeptIdFilter = "ALL";
                refreshEmployeeList();
            }
        });

        // Add employee button actions
        btnAddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeManagementAdvancedActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // Handle Back button logout actions
        btnBackEmployeeAdvanced.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh values back on screen resumes
        refreshEmployeeList();
    }

    private void refreshEmployeeList() {
        filteredEmployeesList.clear();
        for (Employee emp : DataWareHouse.getEmployees()) {
            if (selectedDeptIdFilter.equals("ALL") || emp.getDepartmentId().equalsIgnoreCase(selectedDeptIdFilter)) {
                filteredEmployeesList.add(emp);
            }
        }

        if (adapter == null) {
            adapter = new EmployeeAdapter(this, filteredEmployeesList);
            lvEmployees.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    // Custom inner adapter rendering list items
    private class EmployeeAdapter extends ArrayAdapter<Employee> {
        private final Context context;
        private final List<Employee> list;
        private final DecimalFormat df = new DecimalFormat("$#,##0.00");

        public EmployeeAdapter(Context context, List<Employee> list) {
            super(context, R.layout.item_custom_employee, list);
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_employee, parent, false);
            }

            Employee emp = list.get(position);

            TextView tvEmpSymbol = convertView.findViewById(R.id.tvEmpSymbol);
            TextView tvEmpNameText = convertView.findViewById(R.id.tvEmpNameText);
            TextView tvEmpRoleDeptText = convertView.findViewById(R.id.tvEmpRoleDeptText);
            TextView tvEmpSalaryText = convertView.findViewById(R.id.tvEmpSalaryText);
            TextView btnEditRow = convertView.findViewById(R.id.btnEditRow);
            TextView btnDeleteRow = convertView.findViewById(R.id.btnDeleteRow);

            // Set initial symbol
            if (emp.getName() != null && !emp.getName().isEmpty()) {
                tvEmpSymbol.setText(String.valueOf(emp.getName().charAt(0)).toUpperCase());
            } else {
                tvEmpSymbol.setText("E");
            }

            tvEmpNameText.setText(emp.getName() + " [ID: " + emp.getId() + "]");
            
            Department dept = DataWareHouse.getDepartmentById(emp.getDepartmentId());
            String deptName = (dept != null) ? dept.getName() : "No Department Assigned";
            tvEmpRoleDeptText.setText(emp.getRole() + " • " + deptName);

            tvEmpSalaryText.setText("Salary: " + df.format(emp.getSalary()));

            // OnClick handles
            btnEditRow.setOnClickListener(v -> showEditDialog(emp));
            btnDeleteRow.setOnClickListener(v -> showDeleteConfirmDialog(emp));

            return convertView;
        }
    }

    private void showEditDialog(Employee emp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        EditText dialogEditName = dialogView.findViewById(R.id.dialogEditName);
        EditText dialogEditRole = dialogView.findViewById(R.id.dialogEditRole);
        Spinner dialogSpinnerDept = dialogView.findViewById(R.id.dialogSpinnerDept);
        EditText dialogEditSalary = dialogView.findViewById(R.id.dialogEditSalary);

        // Prefill values
        dialogEditName.setText(emp.getName());
        dialogEditRole.setText(emp.getRole());
        dialogEditSalary.setText(String.valueOf(emp.getSalary()));

        // Fill Spinner depts
        List<String> dialogDeptNames = new ArrayList<>();
        List<String> dialogDeptIds = new ArrayList<>();
        int selectIndex = 0;

        for (int i = 0; i < DataWareHouse.getDepartments().size(); i++) {
            Department d = DataWareHouse.getDepartments().get(i);
            dialogDeptNames.add(d.getName());
            dialogDeptIds.add(d.getId());
            if (d.getId().equalsIgnoreCase(emp.getDepartmentId())) {
                selectIndex = i;
            }
        }

        ArrayAdapter<String> dialogSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dialogDeptNames);
        dialogSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogSpinnerDept.setAdapter(dialogSpinnerAdapter);
        dialogSpinnerDept.setSelection(selectIndex);

        builder.setPositiveButton("Save Profile", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = dialogEditName.getText().toString().trim();
                String newRole = dialogEditRole.getText().toString().trim();
                String newSalaryStr = dialogEditSalary.getText().toString().trim();
                String selectedNewDeptId = dialogDeptIds.get(dialogSpinnerDept.getSelectedItemPosition());

                if (newName.isEmpty() || newRole.isEmpty() || newSalaryStr.isEmpty()) {
                    Toast.makeText(EmployeeManagementAdvancedActivity.this, "Inputs cannot be left empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double salary = Double.parseDouble(newSalaryStr);
                    // Update inside master shared repository
                    emp.setName(newName);
                    emp.setRole(newRole);
                    emp.setDepartmentId(selectedNewDeptId);
                    emp.setSalary(salary);

                    Toast.makeText(EmployeeManagementAdvancedActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    refreshEmployeeList();
                } catch (NumberFormatException e) {
                    Toast.makeText(EmployeeManagementAdvancedActivity.this, "Please enter a valid numeric salary!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showDeleteConfirmDialog(Employee emp) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Release")
                .setMessage("Are you absolutely sure you want to release " + emp.getName() + " from employment registry?")
                .setPositiveButton("Release Employee", (dialog, which) -> {
                    DataWareHouse.removeEmployee(emp.getId());
                    Toast.makeText(EmployeeManagementAdvancedActivity.this, "Employee successfully released.", Toast.LENGTH_SHORT).show();
                    refreshEmployeeList();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
