package com.nguyenquocthinh.k234112eapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenquocthinh.models.ListUserAccount;
import com.nguyenquocthinh.models.UserAccount;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private TextView txtMessage;
    private CheckBox chkSaveLogin;
    private RadioButton radAdmin;
    private RadioButton radEmployee;
    private Button btnLogin;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USER = "saved_user";
    private static final String KEY_PASS = "saved_pass";
    private static final String KEY_REMEMBER = "saved_remember";
    private static final String KEY_ROLE = "saved_role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views
        editUsername = findViewById(R.id.editusername);
        editPassword = findViewById(R.id.editPassword);
        txtMessage = findViewById(R.id.txtMessage);
        chkSaveLogin = findViewById(R.id.chkSaveLogin);
        radAdmin = findViewById(R.id.radAdmin);
        radEmployee = findViewById(R.id.radEmployee);
        btnLogin = findViewById(R.id.btnLogin);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            editUsername.setText(prefs.getString(KEY_USER, ""));
            editPassword.setText(prefs.getString(KEY_PASS, ""));
            chkSaveLogin.setChecked(true);
            String savedRole = prefs.getString(KEY_ROLE, "admin");
            if ("employee".equals(savedRole)) {
                radEmployee.setChecked(true);
            } else {
                radAdmin.setChecked(true);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText("Please enter username and password!");
            return;
        }

        UserAccount user = ListUserAccount.login(username, password);
        if (user == null) {
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText("Invalid username or password!");
            return;
        }

        // Determine correct selected role
        String selectedRole = radAdmin.isChecked() ? "admin" : "employee";

        if (!user.getRole().equalsIgnoreCase(selectedRole)) {
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText("User account does not belong to selected role: " + selectedRole);
            return;
        }

        // Login success!
        txtMessage.setVisibility(View.GONE);

        // Save prefs if remembered
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        if (chkSaveLogin.isChecked()) {
            editor.putString(KEY_USER, username);
            editor.putString(KEY_PASS, password);
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_ROLE, selectedRole);
        } else {
            editor.clear();
        }
        editor.apply();

        // Redirect according to role
        if ("admin".equals(selectedRole)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_LOGIN", user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, EmployeeManagementAdvancedActivity.class);
            intent.putExtra("USER_LOGIN", user);
            startActivity(intent);
        }
    }
}
