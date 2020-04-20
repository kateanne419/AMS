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
    double tempValRef;
    int xValRef;
    List<Double> tempValues = new ArrayList<>();
    List<Integer> xValues = new ArrayList<>();

    //String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
    //double[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
    double[] yAxisData = new double[tempValues.size()];
    int[] axisData = new int[xValues.size()];
    LineChartView lineChartView;

    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_history);

        resetstats=(Button) findViewById(R.id.resetbutton);
        buttoncount=(TextView) findViewById(R.id.buttoncount2);
        lineChartView = findViewById(R.id.tempchart);

        for(int i=0;i<tempValues.size();i++){
            yAxisData[i]=tempValues.get(i);
        }

        for(int i=0;i<xValues.size();i++){
            axisData[i]=xValues.get(i);
        }

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(String.valueOf(axisData[i])));
        }

        for (int i = 0; i < yAxisData.length; i++){
            yAxisValues.add(new PointValue(i, (float) yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        //------------------------------------------graph end




        dref=FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
                buttoncount.setText(Integer.toString(statscount));

                if(dataSnapshot.exists()) {
                    tempValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("tempvalues").getChildren()) {
                        tempValRef = dss.getValue(double.class);
                        tempValues.add(tempValRef);
                    }

                    xValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("xaxis").getChildren()) {
                        xValRef = dss.getValue(int.class);
                        xValues.add(xValRef);
                    }

//                    StringBuilder stringBuilder = new StringBuilder();
//                    for(int i=0;i<tempValues.size();i++){
//                        stringBuilder.append(xValues.get(i)+" "+tempValues.get(i)+", ");
//                    }
//                    Toast.makeText(getApplicationContext(), stringBuilder.toString(),Toast.LENGTH_LONG).show();
                }
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
                DatabaseReference tempRef = database.getReference().child("tempvalues");
                DatabaseReference turbRef = database.getReference().child("turbvalues");
                DatabaseReference phRef = database.getReference().child("phvalues");
                DatabaseReference xAxisRef = database.getReference().child("xaxis");
                tempRef.removeValue();
                turbRef.removeValue();
                phRef.removeValue();
                xAxisRef.removeValue();
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
