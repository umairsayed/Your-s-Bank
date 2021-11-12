package com.lockhart.yoursbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;

public class LauncherActivity extends AppCompatActivity {

    Button emp_login, cust_login;
    ImageButton bankLoc;
    TextView bankLocText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        intent = new Intent(this, LoginActivity.class);

        emp_login = findViewById(R.id.emp_login);
        cust_login = findViewById(R.id.cust_login);

        emp_login.setOnClickListener(v -> employeeLogin());

        cust_login.setOnClickListener(v -> customerLogin());


        bankLoc = (ImageButton) findViewById(R.id.bankLocation);
        bankLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this,LocActivity.class);
                startActivity(intent);
            }
        });

        bankLocText = (TextView) findViewById(R.id.ourHeadquarters);
        bankLocText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this,LocActivity.class);
                startActivity(intent);
            }
        });
    }


    private void employeeLogin() {
        intent.putExtra("login_type", 0);
        startActivity(intent);
    }

    private void customerLogin() {
        intent.putExtra("login_type", 1);
        startActivity(intent);
    }
}