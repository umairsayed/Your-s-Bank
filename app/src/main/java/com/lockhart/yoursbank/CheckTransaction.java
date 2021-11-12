package com.lockhart.yoursbank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.DatabaseHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckTransaction extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    JSONObject data;
    ArrayList<JSONObject> allTransactions;

    LinearLayout transaction_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_transaction);

        databaseHelper = new DatabaseHelper(this);

        transaction_list = findViewById(R.id.transaction_list);

        try {
            data = new JSONObject(getIntent().getStringExtra("acc_data"));

            allTransactions = new ArrayList<>();

            if (data.getString("ACCOUNT_NO").length() == 10) {
                //set for customer
                getCustTrans();
            } else if (data.getString("ACCOUNT_NO").length() == 8) {
                //set for employee
                getAllTrans();
            }

            if (!allTransactions.isEmpty()) {
                for (JSONObject object : allTransactions) {
                    transaction_list.addView(createItem(object));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "NO ENTRIES", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private View createItem(JSONObject object) throws Exception {
        TextView date, from, to, type, amount, comment;

        View view = View.inflate(this, R.layout.trans_layout, null);

        date = view.findViewById(R.id.date_column);
        from = view.findViewById(R.id.from_field);
        to = view.findViewById(R.id.to_field);
        type = view.findViewById(R.id.type_column);
        amount = view.findViewById(R.id.amount_column);
        comment = view.findViewById(R.id.comment_column);

        //date
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(object.getLong("TIMESTAMP"));
        date.setText(formatter.format(calendar.getTime()));

        //from
        if (data.getString("ACCOUNT_NO").length() == 10) {
            from.setVisibility(View.GONE);
        } else {
            from.setText("From: " + object.getString("FROM_ACC"));
        }

        //to
        to.setText("To: " + object.getString("TO_ACC"));

        //type
        if (object.getString("TRANSACTION_TYPE").equals("credit")) {
            type.setText("c");
        } else if (object.getString("TRANSACTION_TYPE").equals("debit")) {
            type.setText("d");
        }

        //amount
        amount.setText(getString(R.string.rupee_sign) + object.getString("AMOUNT"));

        //comment
        comment.setText(object.getString("COMMENTS"));

        return view;
    }

    private void getAllTrans() {
        allTransactions = databaseHelper.getAllTransactions();
    }

    private void getCustTrans() throws Exception {
        allTransactions = databaseHelper.getCustTransactions(data.getString("ACCOUNT_NO"));
    }
}