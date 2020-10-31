package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimesetupActivity extends AppCompatActivity {
    TimePickerDialog picker;
    Button time;
    ImageView logo;

    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesetup);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendar = Calendar.getInstance();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logo = findViewById(R.id.bg_logo);
        time = findViewById(R.id.button_time);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user").document(com.example.menu.MainActivity.ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        time.setText(document.getString("notice"));
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });





        time.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                showCalenderDialog();

            }


        });


    }

    private void showCalenderDialog(){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        // time picker dialog
        picker = new TimePickerDialog(TimesetupActivity .this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        if(sHour<10){
                            if(sMinute<10){
                                time.setText("0"+sHour + ":0" + sMinute);
                            }else{
                                time.setText("0"+sHour + ":" + sMinute);
                            }
                        }else{
                            if(sMinute<10){
                                time.setText(sHour + ":0" + sMinute);
                            }else{
                                time.setText(sHour + ":" + sMinute);
                            }
                        }

                        //  timer=time.getText().toString();
                        Map<String, Object> p = new HashMap<>();
                        p.put("notice",time.getText().toString());
                        FirebaseFirestore data = FirebaseFirestore.getInstance();
                        data.collection("user").document(com.example.menu.MainActivity.ID)////這邊記得改ID，目前寫死
                                .set(p, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(TimesetupActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TimesetupActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }


                                });
                        Animation shake;
                        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                        logo.startAnimation(shake);
                    }
                }, hour, minutes, true);
        picker.show();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}