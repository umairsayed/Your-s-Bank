package com.lockhart.yoursbank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.DatabaseHelper;

import org.json.JSONObject;

public class CustomerDashboardActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    JSONObject data;

    TextView curr_balance;
    Button send_money, check_trans;

    Intent debit, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        databaseHelper = new DatabaseHelper(this);

        curr_balance = findViewById(R.id.curr_balance);
        send_money = findViewById(R.id.debit_btn);
        check_trans = findViewById(R.id.trans_btn);

        debit = new Intent(this, SendMoneyActivity.class);
        check = new Intent(this, CheckTransaction.class);

        try {
            data = new JSONObject(getIntent().getStringExtra("acc_data"));
            setCurr_balance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        send_money.setOnClickListener(v -> {
            debit.putExtra("acc_data", data.toString());
            startActivity(debit);
        });

        check_trans.setOnClickListener(v -> {
            check.putExtra("acc_data", data.toString());
            startActivity(check);
        });

    }

    @Override
    protected void onResume() {
        updateData();
        super.onResume();
    }

    private void updateData() {
        try {
            data = databaseHelper.getCustAccount(data.getString("ACCOUNT_NO"), data.getString("PASSWORD"));
            setCurr_balance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurr_balance() {
        try {
            String bal = data.getString("BALANCE");
            curr_balance.setText(getString(R.string.rupee_sign) + bal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}