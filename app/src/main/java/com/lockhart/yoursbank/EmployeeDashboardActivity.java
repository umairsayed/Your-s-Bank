package com.lockhart.yoursbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;

import org.json.JSONObject;

public class EmployeeDashboardActivity extends AppCompatActivity {

    JSONObject emp_data;

    Button register, debit_btn, credit_btn, check_all_btn;
    Intent emp_trans, check_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        register = findViewById(R.id.register);
        debit_btn = findViewById(R.id.debit_btn);
        credit_btn = findViewById(R.id.credit_btn);
        check_all_btn = findViewById(R.id.check_all);

        emp_trans = new Intent(this, EmpTransActivity.class);
        check_trans = new Intent(this, CheckTransaction.class);

        try {
            emp_data = new JSONObject(getIntent().getStringExtra("acc_data"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        register.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            intent.putExtra("login_type", 1);
            startActivity(intent);
        });

        debit_btn.setOnClickListener(v -> {
            emp_trans.putExtra("acc_data", emp_data.toString());
            emp_trans.putExtra("type", "debit");
            startActivity(emp_trans);
        });

        credit_btn.setOnClickListener(v -> {
            emp_trans.putExtra("acc_data", emp_data.toString());
            emp_trans.putExtra("type", "credit");
            startActivity(emp_trans);
        });

        check_all_btn.setOnClickListener(v -> {
            check_trans.putExtra("acc_data", emp_data.toString());
            startActivity(check_trans);
        });
    }
}