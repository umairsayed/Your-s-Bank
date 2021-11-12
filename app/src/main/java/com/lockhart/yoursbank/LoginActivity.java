package com.lockhart.yoursbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.DatabaseHelper;
import com.lockhart.yoursbank.Support.PasswordHash;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    int type;
    DatabaseHelper databaseHelper;
    PasswordHash passwordHash;

    EditText acc_no, password;
    Button login;
    TextView register;

    String account_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        type = getIntent().getIntExtra("login_type", -1);
        databaseHelper = new DatabaseHelper(this);
        passwordHash = new PasswordHash();

        acc_no = findViewById(R.id.account_no_in);
        password = findViewById(R.id.password_in);
        login = findViewById(R.id.login_btn);
        register = findViewById(R.id.register_link);

        if (type == 0) {
            acc_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        } else if (type == 1) {
            acc_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            register.setVisibility(View.GONE);
        }

        login.setOnClickListener(v -> {
            if (checkInput()) {
                switch (type) {
                    case 0:
                        empLogin();
                        break;
                    case 1:
                        custLogin();
                        break;
                }
            }
        });

        register.setOnClickListener(v -> doRegistration());
    }

    //check for input constraints
    private boolean checkInput() {
        boolean result = false;

        account_no = acc_no.getText().toString();
        if (type == 0) {
            if (account_no.length() == 8) { //8 digit account number of employees
                result = true;
            } else {
                Toast.makeText(this, "Enter a valid Account No.", Toast.LENGTH_SHORT).show();
                acc_no.requestFocus();
            }
        } else if (type == 1) {
            if (account_no.length() == 10) { //10 digit account number of employees
                result = true;
            } else {
                Toast.makeText(this, "Enter a valid Account No.", Toast.LENGTH_SHORT).show();
                acc_no.requestFocus();
            }
        }

        String pass = password.getText().toString();
        if (pass.length() >= 8) {
            result = true;
        } else {
            Toast.makeText(this, "Enter a valid Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }

        return result;
    }

    //do Employee login
    private void empLogin() {
        JSONObject login_data = databaseHelper.getEmpAccount(account_no, passwordHash.passwordHashed(password.getText().toString()));
        try {
            switch (login_data.getString("status")) {
                case "failed":
                    Toast.makeText(this, "Error, try again later", Toast.LENGTH_SHORT).show();
                    break;
                case "no_account":
                    Toast.makeText(this, "Account doesn't exist, check account no.", Toast.LENGTH_SHORT).show();
                    acc_no.setText("");
                    password.setText("");
                    acc_no.requestFocus();
                    break;
                case "invalid_password":
                    Toast.makeText(this, "Wrong password, try again", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    password.requestFocus();
                    break;
                case "success":
                    Intent intent = new Intent(this, EmployeeDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);//To finish all previously running activities to prevent going back.
                    intent.putExtra("acc_data", login_data.toString());
                    String name = login_data.getString("NAME");
                    Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //do Customer Login
    private void custLogin() {
        JSONObject login_data = databaseHelper.getCustAccount(account_no, passwordHash.passwordHashed(password.getText().toString()));
        try {
            switch (login_data.getString("status")) {
                case "no_account":
                    Toast.makeText(this, "Account doesn't exist, check account no.", Toast.LENGTH_SHORT).show();
                    acc_no.setText("");
                    password.setText("");
                    acc_no.requestFocus();
                    break;
                case "invalid_password":
                    Toast.makeText(this, "Wrong password, try again", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    password.requestFocus();
                    break;
                case "success":
                    Intent intent = new Intent(this, CustomerDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);//To finish all previously running activities to prevent going back.
                    intent.putExtra("acc_data", login_data.toString());
                    String name = login_data.getString("NAME");
                    Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d("Login Error", login_data.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //open Registration
    private void doRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("login_type", type);
        startActivity(intent);
    }
}