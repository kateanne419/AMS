package com.salvadorsp.nemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {
    TextView temperature, pH, turbidity, light;
    Button lightswitch, history, savestats;
    Integer lightstatus, statscount;
    Double tempstatus, phstatus, turbstatus;
    int tempgaugeval, phgaugeval, turbgaugeval;
    CustomGauge tempGauge, phGauge, turbGauge;
    DateFormat df = new SimpleDateFormat("MMM d K:mm");
    String date = df.format(Calendar.getInstance().getTime());



    DatabaseReference dRef, tempDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperatureval);
        pH = findViewById(R.id.phval);
        turbidity = findViewById(R.id.turbidityval);
        light = findViewById(R.id.lightstatus);
        lightswitch = findViewById(R.id.lightswitch);
        history = findViewById(R.id.historybutton);
        savestats = findViewById(R.id.savebutton);
        tempGauge = findViewById(R.id.tempgauge);
        phGauge = findViewById(R.id.phgauge);
        turbGauge = findViewById(R.id.turbgauge);

        dRef=FirebaseDatabase.getInstance().getReference();
        tempDbRef=FirebaseDatabase.getInstance().getReference().child("tempvalues"); //TODO remove after debug
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempstatus=dataSnapshot.child("temperature").getValue(Double.class);
                tempgaugeval=dataSnapshot.child("temperature").getValue(int.class);
                temperature.setText(Double.toString(tempstatus));
                tempGauge.setValue(tempgaugeval);

                phstatus=dataSnapshot.child("pH").getValue(Double.class);
                phgaugeval=dataSnapshot.child("pH").getValue(int.class);
                pH.setText(Double.toString(phstatus));
                phGauge.setValue(phgaugeval);

                turbstatus=dataSnapshot.child("turbidity").getValue(Double.class);
                turbgaugeval=dataSnapshot.child("turbidity").getValue(int.class);
                turbidity.setText(Double.toString(turbstatus));
                turbGauge.setValue(turbgaugeval);

                lightstatus=dataSnapshot.child("light").getValue(Integer.class);
                if(lightstatus==1){
                    light.setText("Light is on");
                    lightswitch.setText("TURN OFF");
                }else{
                    light.setText("Light is off");
                    lightswitch.setText("TURN ON");
                }

                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lightswitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("light");

                if(lightstatus==1){
                    myRef.setValue(0);

                }else{
                    myRef.setValue(1);
                    lightswitch.setText("TURN OFF");
                }
            }
        });

        savestats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("statscount");
                DatabaseReference tempRef = database.getReference().child("tempvalues");
                DatabaseReference turbRef = database.getReference().child("turbvalues");
                DatabaseReference phRef = database.getReference().child("phvalues");
                DatabaseReference xAxisRef = database.getReference().child("xaxis");
                DatabaseReference labelRef = database.getReference().child("labeldate");

                myRef.setValue(statscount+1);
                labelRef.push().setValue(date);
                xAxisRef.push().setValue(statscount+1);
                turbRef.push().setValue(turbstatus);
                phRef.push().setValue(phstatus);
                tempRef.push().setValue(tempstatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Successfully saved current stats! See history for more.", Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(MainActivity.this, "Failed to save current stats.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDataHistory();
            }
        });
    }

    public void openDataHistory(){
        Intent intent = new Intent(this, DataHistory.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
