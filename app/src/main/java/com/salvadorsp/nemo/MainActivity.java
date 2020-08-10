package com.salvadorsp.nemo;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {
    TextView temperature, pH, turbidity, light, welcome, autotime, currtime;
    Button lightswitch, history, savestats, autolight;
    Integer lightstatus, statscount;
    Double tempstatus, phstatus, turbstatus;
    int tempgaugeval, phgaugeval, turbgaugeval, tHour, tMinute, cHour, cMinute;
    CustomGauge tempGauge, phGauge, turbGauge;
    DateFormat df = new SimpleDateFormat("MMM d K:mm");
    DateFormat tf = new SimpleDateFormat("hh:mm aa");
    String usertype;


    DatabaseReference dRef;

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
        welcome = findViewById(R.id.username);
        usertype = getIntent().getStringExtra("usertype");
        autolight = findViewById(R.id.autolight);
        autotime = findViewById(R.id.autotime);
        currtime = findViewById(R.id.currtime);

        //mock values for local user types to define permissions
        if(usertype.equals("admin")){
            welcome.setText("Admin's Aquarium");
            lightswitch.setEnabled(true);
            savestats.setEnabled(true);
        }else if(usertype.equals("user")){
            welcome.setText("User's Aquarium");
            lightswitch.setEnabled(false);
            savestats.setEnabled(false);
        }

        dRef=FirebaseDatabase.getInstance().getReference();
        dRef.addValueEventListener(new ValueEventListener() {
            //everytime the arduino sends a new value to firebase, the app retrieves it and displays it
            //accordingly, depending on the values set and level of danger.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempstatus=dataSnapshot.child("sensorvalues/temperature").getValue(Double.class);
                tempgaugeval=dataSnapshot.child("sensorvalues/temperature").getValue(int.class);
                temperature.setText(Double.toString(tempstatus));
                tempGauge.setValue(tempgaugeval);
                if(tempstatus>=22.22 && tempstatus<=27.77){
                    temperature.setTextColor(Color.parseColor("#28B463"));
                }else if((tempstatus>=20 && tempstatus<=22.21) || (tempstatus<=29.99 && tempstatus>=27.78)){
                    temperature.setTextColor(Color.parseColor("#F39C12"));
                }else if((tempstatus>=0 && tempstatus<=19.99) || (tempstatus>=30 && tempstatus<=50)){
                    temperature.setTextColor(Color.parseColor("#C0392B"));
                    tempnotification();
                }

                phstatus=dataSnapshot.child("sensorvalues/pH").getValue(Double.class);
                phgaugeval=dataSnapshot.child("sensorvalues/pH").getValue(int.class);
                pH.setText(Double.toString(phstatus));
                phGauge.setValue(phgaugeval);
                if(phstatus>=6.5 && phstatus<=7.5){
                    pH.setTextColor(Color.parseColor("#28B463"));
                }else if((phstatus>=5 && phstatus<=6.4) || (phstatus<=9 && phstatus>=7.6)){
                    pH.setTextColor(Color.parseColor("#F39C12"));
                }else if((phstatus>=0 && phstatus<=4.9) || (phstatus>=8.9 && phstatus<=14)){
                    pH.setTextColor(Color.parseColor("#C0392B"));
                    phnotification();
                }

                turbstatus=dataSnapshot.child("sensorvalues/turbidity").getValue(Double.class);
                turbgaugeval=dataSnapshot.child("sensorvalues/turbidity").getValue(int.class);
                turbidity.setText(Double.toString(turbstatus));
                turbGauge.setValue(turbgaugeval);
                if(turbstatus<=7.9){
                    turbidity.setTextColor(Color.parseColor("#28B463")); //green
                }else if(turbstatus>=8 && turbstatus<=10.99){
                    turbidity.setTextColor(Color.parseColor("#F39C12")); //yellow
                }else if(turbstatus>=11){
                    turbidity.setTextColor(Color.parseColor("#C0392B")); //red
                    turbnotification();
                }

                //gets status of light if turned on or off
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

        //Future update, automate the lights
        autolight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                tHour = hourOfDay;
                                tMinute = minute;
                                calendar.set(0, 0, 0, tHour, tMinute);
                                //autotime.setText(tf.format(calendar.getTime()));
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        automate(tHour, tMinute);
//                                    }
//                                }, 10000);
                            }
                        }, 12, 0, true
                );
                timePickerDialog.updateTime(tHour, tMinute);
                timePickerDialog.show();
            }
        });


        lightswitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                togglelight();
            }
        });

        savestats.setOnClickListener(new View.OnClickListener() {           //saves the current values and adds it to an array along with
            @Override                                                       //the date and time the save is made/clicked
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("statscount");
                DatabaseReference tempRef = database.getReference().child("tempvalues");
                DatabaseReference turbRef = database.getReference().child("turbvalues");
                DatabaseReference phRef = database.getReference().child("phvalues");
                DatabaseReference xAxisRef = database.getReference().child("xaxis");
                DatabaseReference labelRef = database.getReference().child("labeldate");
                String date = df.format(Calendar.getInstance().getTime());

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


        history.setOnClickListener(new View.OnClickListener() {         //opens the history page
            @Override
            public void onClick(View v) {
                openDataHistory();
            }
        });
    }


    public void togglelight(){                                          //the "lightswitch" for the led
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("light");

        //set value of light on firebase to 1 or 0 which means on and off respectively
        if(lightstatus==1){
            myRef.setValue(0)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Light is turned off.", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(MainActivity.this, "Cannot turn off light.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }else{
            myRef.setValue(1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Light is turned on.", Toast.LENGTH_SHORT).show();
                                lightswitch.setText("TURN OFF");
                            }else{
                                Toast.makeText(MainActivity.this, "Cannot turn on light.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    //notification for extreme temperature values
    private void tempnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("temp","temp", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "temp")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.new_logo)
                .setAutoCancel(true)
                .setContentText("Temperature is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    //notification for extreme ph values
    private void phnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("ph","ph", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ph")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.new_logo)
                .setAutoCancel(true)
                .setContentText("pH is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    //notification for extreme turbidity values
    private void turbnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("turb","turb", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "turb")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.new_logo)
                .setAutoCancel(true)
                .setContentText("Turbidity is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    public void openDataHistory(){
        Intent intent = new Intent(this, DataHistory.class);
        intent.putExtra("usertype", usertype);
        startActivity(intent);
    }

    //For future update, light automation
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void automate(int tHour, int tMinute){
        while (isAppRunning(MainActivity.this, "com.salvadorsp.nemo")){
            Calendar calendar1 = Calendar.getInstance();
            cHour = calendar1.get(Calendar.HOUR_OF_DAY);
            cMinute = calendar1.get(Calendar.MINUTE);
            currtime.setText(tf.format(calendar1.getTime()));
            if((tHour==cHour) && (tMinute==cMinute)){
                togglelight();
            }
        }
    }
}
