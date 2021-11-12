package com.lockhart.yoursbank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.AmountFilter;
import com.lockhart.yoursbank.Support.DatabaseHelper;

import org.json.JSONObject;

public class SendMoneyActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    JSONObject data;
    TextView acc_no, acc_bal;
    EditText rec_acc, rec_amnt;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        databaseHelper = new DatabaseHelper(this);

        acc_no = findViewById(R.id.account_no);
        acc_bal = findViewById(R.id.account_balance);
        rec_acc = findViewById(R.id.rec_account);
        rec_amnt = findViewById(R.id.rec_money);

        try {
            data = new JSONObject(getIntent().getStringExtra("acc_data"));
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            if (checkInput()) {
                submit();
            }
        });
    }

    private boolean checkInput() {
        if (rec_acc.getText().toString().equals("") || rec_acc.getText().toString().length() < 10) {
            Toast.makeText(this, "Enter a valid account no", Toast.LENGTH_SHORT).show();
            acc_no.setText("");
            acc_no.requestFocus();
            return false;
        } else {
            if (databaseHelper.checkCustomer(rec_acc.getText().toString())) {
                Toast.makeText(this, "Enter a valid account no", Toast.LENGTH_SHORT).show();
                acc_no.setText("");
                acc_no.requestFocus();
                return false;
            }
        }

        if (rec_amnt.getText().toString().equals("")) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            rec_amnt.requestFocus();
            return false;
        }

        return true;
    }

    private void submit() {
        try {
            if (databaseHelper.doTransaction(data.getString("ACCOUNT_NO")
                    , rec_acc.getText().toString()
                    , "debit"
                    , rec_amnt.getText().toString()
                    , System.currentTimeMillis()
                    , "")) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setData() throws Exception {
        acc_no.setText(data.getString("ACCOUNT_NO"));
        acc_bal.setText(getString(R.string.rupee_sign) + " " + data.getString("BALANCE"));
        rec_amnt.setFilters(new AmountFilter[]{new AmountFilter(Long.parseLong(data.getString("BALANCE")))});
    }
}