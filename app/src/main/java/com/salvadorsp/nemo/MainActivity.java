package com.salvadorsp.nemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView temperature, pH, turbidity, light, buttoncount;
    Button lightswitch, history, savestats;
    Integer lightstatus, statscount;
    String tempstatus, phstatus, turbstatus;

    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature=(TextView) findViewById(R.id.temperatureval);
        pH=(TextView) findViewById(R.id.phval);
        turbidity=(TextView) findViewById(R.id.turbidityval);
        light=(TextView) findViewById(R.id.lightstatus);
        lightswitch=(Button) findViewById(R.id.lightswitch);
        history=(Button) findViewById(R.id.historybutton);
        savestats=(Button) findViewById(R.id.savebutton);
        buttoncount=(TextView) findViewById(R.id.buttoncount);


        dref=FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempstatus=dataSnapshot.child("temperature").getValue().toString();
                temperature.setText(tempstatus);

                phstatus=dataSnapshot.child("pH").getValue().toString();
                pH.setText(phstatus);

                turbstatus=dataSnapshot.child("turbidity").getValue().toString();
                turbidity.setText(turbstatus);

                lightstatus=dataSnapshot.child("light").getValue(Integer.class);
                if(lightstatus==1){
                    light.setText("Light is on");
                    lightswitch.setText("TURN OFF");
                }else{
                    light.setText("Light is off");
                    lightswitch.setText("TURN ON");
                }

                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
                buttoncount.setText(Integer.toString(statscount));  //for debug onli, use value for graph's x axis

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
                myRef.setValue(statscount+1);
                Toast.makeText(MainActivity.this, "Successfully saved current stats! See history for more.", Toast.LENGTH_LONG).show();

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
