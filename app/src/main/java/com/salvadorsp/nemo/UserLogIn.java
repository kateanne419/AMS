package com.salvadorsp.nemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLogIn extends AppCompatActivity {
    Button loginbtn;
    EditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loginbtn = findViewById(R.id.loginbtn);
        user = findViewById(R.id.enteruser);
        pass = findViewById(R.id.enterpass);

        //checking of login credentials
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getText().toString().equals("admin") && pass.getText().toString().equals("admin")) {
                    Toast.makeText(UserLogIn.this, "Signing in as admin...", Toast.LENGTH_SHORT).show();
                    loginAsAdmin();
                }else if(user.getText().toString().equals("user") && pass.getText().toString().equals("user")){
                    Toast.makeText(UserLogIn.this, "Signing in as user...", Toast.LENGTH_SHORT).show();
                    loginAsUser();
                }else{
                    Toast.makeText(UserLogIn.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginAsAdmin(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usertype", "admin");
        startActivity(intent);
    }

    public void loginAsUser(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usertype", "user");
        startActivity(intent);
    }
}
