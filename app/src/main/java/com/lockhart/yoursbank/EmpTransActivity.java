package com.lockhart.yoursbank;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.AmountFilter;
import com.lockhart.yoursbank.Support.DatabaseHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class EmpTransActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    ArrayList<String> allAccounts;

    Spinner account_list;

    EditText amnt;
    Button submit;

    JSONObject emp_data;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_trans);

        databaseHelper = new DatabaseHelper(this);

        try {
            emp_data = new JSONObject(getIntent().getStringExtra("acc_data"));
            type = getIntent().getStringExtra("type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        allAccounts = databaseHelper.getAllAccountNo();

        account_list = findViewById(R.id.acc_list);
        amnt = findViewById(R.id.trans_amnt);
        submit = findViewById(R.id.submit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allAccounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_list.setAdapter(adapter);

        account_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("debit")) {
                    setData(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(v -> {
            if (amnt.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter the amount", Toast.LENGTH_SHORT).show();
                amnt.requestFocus();
            } else {
                doTransaction();
            }
        });
    }

    private void setData(int pos) {
        amnt.setText("");
        String bal = databaseHelper.getBalance(allAccounts.get(pos));
        if (bal != null) {
            amnt.setFilters(new AmountFilter[]{new AmountFilter(Long.parseLong(bal))});
        }
    }

    private void doTransaction() {
        try {
            if (databaseHelper.doTransaction(emp_data.getString("ACCOUNT_NO")
                    , allAccounts.get(account_list.getSelectedItemPosition())
                    , type
                    , amnt.getText().toString()
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
}