package com.lockhart.yoursbank;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoursbank.R;
import com.lockhart.yoursbank.Support.DatabaseHelper;
import com.lockhart.yoursbank.Support.PasswordHash;

import java.util.ArrayList;
import java.util.Random;

//import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.Menu;
import android.view.View;
//import android.widget.Button;
import android.widget.ImageView;

public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    PasswordHash passwordHash;
    int type;
    EditText name, addr, email, phone, pass, conf_pass;
    ArrayList<EditText> data_in;
    Button submit;

    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        imageView = (ImageView) this.findViewById(R.id.userPhoto);
        Button photoButton = (Button) this.findViewById(R.id.addPhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }




        databaseHelper = new DatabaseHelper(this);
        passwordHash = new PasswordHash();

        type = getIntent().getIntExtra("login_type", -1);

        data_in = new ArrayList<>();

        name = findViewById(R.id.acc_name_in);
        addr = findViewById(R.id.acc_addr_in);
        email = findViewById(R.id.acc_email_in);
        phone = findViewById(R.id.acc_phone_in);
        pass = findViewById(R.id.acc_pass);
        conf_pass = findViewById(R.id.acc_pass_in);
        submit = findViewById(R.id.submit);

        data_in.add(name);
        data_in.add(addr);
        data_in.add(email);
        data_in.add(phone);

        submit.setOnClickListener(v -> {
            if (checkInput()) {
                if (checkPassword()) {
                    String account_no = genAccountNo();
                    createAccount(account_no);
                }
            }
        });
    }

    private void createAccount(String acc_no) {
        String password = passwordHash.passwordHashed(pass.getText().toString());
        String name = this.name.getText().toString();
        String addr = this.addr.getText().toString();
        String email = this.email.getText().toString();
        String phone = this.phone.getText().toString();
        if (type == 0) {
            databaseHelper.addEmployee(acc_no, name, addr, email, phone, password);
        } else if (type == 1) {
            databaseHelper.addCustomer(acc_no, name, addr, email, phone, password, "0");
        }

        new AlertDialog.Builder(this)
                .setTitle("Account Created")
                .setMessage("Account No: " + acc_no)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }

    private boolean checkPassword() {
        if (!pass.getText().toString().equals(conf_pass.getText().toString())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            pass.setText("");
            conf_pass.setText("");
            pass.requestFocus();
            return false;
        } else {
            if (pass.getText().toString().length() < 8) {
                Toast.makeText(this, "Passwords should be at least 8 characters", Toast.LENGTH_SHORT).show();
                pass.setText("");
                conf_pass.setText("");
                pass.requestFocus();
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean checkInput() {
        boolean result = true;
        for (EditText field : data_in) {
            if (field.getText().toString().equals("")) {
                result = false;
                Toast.makeText(this, "Enter all data", Toast.LENGTH_SHORT).show();
                field.requestFocus();
                break;
            }
        }
        return result;
    }

    private String genAccountNo() {
        boolean exit = false;
        long account_num = 0;
        while (!exit) {
            if (type == 0) {
                account_num = new Random().nextInt((int) (99999999L - 10000000L)) + 10000000L;
                exit = !databaseHelper.checkEmployee(String.valueOf(account_num));
            } else if (type == 1) {
                account_num = new Random().nextInt((int) (9999999999L - 1000000000L)) + 1000000000L;
                exit = databaseHelper.checkCustomer(String.valueOf(account_num));
            }
        }

        return String.valueOf(account_num);
    }
}