package com.salvadorsp.nemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DataHistory extends AppCompatActivity {
    Button resetstats;
    Integer statscount;
    TextView buttoncount;

    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_history);

        resetstats=(Button) findViewById(R.id.resetbutton);
        buttoncount=(TextView) findViewById(R.id.buttoncount2);

        dref=FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
                buttoncount.setText(Integer.toString(statscount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        resetstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("statscount");
                myRef.setValue(0);
                Toast.makeText(DataHistory.this, "Successfully reset history!", Toast.LENGTH_LONG).show();

            }
        });
    }
}
