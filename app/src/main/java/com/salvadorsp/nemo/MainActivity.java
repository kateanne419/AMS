package com.salvadorsp.nemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView temperature, pH, turbidity, light, buttoncount;
    Button lightswitch, history, savestats;
    Integer lightstatus, statscount;
    Double tempstatus, phstatus, turbstatus;
    List<Double> tempValues;

    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempValues = new ArrayList<>();


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
                tempstatus=dataSnapshot.child("temperature").getValue(Double.class);
                temperature.setText(Double.toString(tempstatus));

                phstatus=dataSnapshot.child("pH").getValue(Double.class);
                pH.setText(Double.toString(phstatus));

                turbstatus=dataSnapshot.child("turbidity").getValue(Double.class);
                turbidity.setText(Double.toString(turbstatus));

                lightstatus=dataSnapshot.child("light").getValue(Integer.class);
                if(lightstatus==1){
                    light.setText("Light is on");
                    lightswitch.setText("TURN OFF");
                }else{
                    light.setText("Light is off");
                    lightswitch.setText("TURN ON");
                }

                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
                buttoncount.setText(Integer.toString(statscount));  //TODO delete after. for debug onli, real @ datahistoryjava

//                for(DataSnapshot dss:dataSnapshot.getChildren()){
//                    tempRef=dss.getValue(Double.class);
//                    tempValues.add(tempRef);
//                }
                    //TODO retrieve array. put to tempValues matic ba magaappend pag may child na napush?
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
                myRef.setValue(statscount+1);
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
