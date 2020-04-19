package com.salvadorsp.nemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class DataHistory extends AppCompatActivity {
    Button resetstats;
    Integer statscount;
    TextView buttoncount;

    String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"};
    double[] yAxisData = {50.5, 20.7, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
    List yAxisValues = new ArrayList();
    List axisValues = new ArrayList();
    List lines = new ArrayList();
    LineChartData data = new LineChartData();
    Axis axis = new Axis();
    Axis yAxis = new Axis();



    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_history);

        resetstats=(Button) findViewById(R.id.resetbutton);
        buttoncount=(TextView) findViewById(R.id.buttoncount2);

        //----------------------------------------graph start
        LineChartView lineChartView = findViewById(R.id.tempchart);
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));
        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++){
            yAxisValues.add(new PointValue(i, (float) yAxisData[i]));
        }

        lines.add(line);
        data.setLines(lines);
        lineChartView.setLineChartData(data);

        axis.setValues(axisValues);
        data.setAxisXBottom(axis);
        data.setAxisYLeft(yAxis);
        //------------------------------------------graph end


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
                myRef.setValue(0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DataHistory.this, "Successfully reset history!", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(DataHistory.this, "Failed to reset history.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });
    }
}
