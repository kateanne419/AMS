package com.salvadorsp.nemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    Button loginbtn;
    EditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loginbtn = findViewById(R.id.loginbtn);
        user = findViewById(R.id.enteruser);
        pass = findViewById(R.id.enterpass);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getText().toString().equals("admin") && pass.getText().toString().equals("admin")) {
                    Toast.makeText(LogIn.this, "Signing in as admin...", Toast.LENGTH_SHORT).show();
                    //TODO redirect to main activity with everything enabled
                    //"Welcome, admin"
                }else if(user.getText().toString().equals("user") && pass.getText().toString().equals("user")){
                    Toast.makeText(LogIn.this, "Signing in as user...", Toast.LENGTH_SHORT).show();
                    //TODO redirect to main activity with buttons disabled
                }else{
                    Toast.makeText(LogIn.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
