package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class passwdsetup extends AppCompatActivity {
    String pwd,ID;
    Button submit;
    EditText old,now;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwdsetup);
        submit=findViewById(R.id.confirm_button);
        old=findViewById(R.id.passwd);
        now=findViewById(R.id.newpasswd);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("HII", document.getId() + " => " + document.getLong("actid"));
                                if(document.getLong("uid")==1){
                                    ID=document.getId();
                                    pwd=document.getString("pwd");
                                }
                            }



                        } else {
                            Log.w("000", "Error getting documents.", task.getException());
                        }

                    }
                });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(old.getText().toString().equals("")||now.getText().toString().equals("")){
                    Toast.makeText(passwdsetup.this, "請確實填寫", Toast.LENGTH_SHORT).show();
                }else if(now.getText().toString().equals(old.getText().toString())){
                    Toast.makeText(passwdsetup.this, "新密碼不可與舊的相同", Toast.LENGTH_SHORT).show();
                }else if(!old.getText().toString().equals(pwd)){
                    Toast.makeText(passwdsetup.this, "密碼錯誤", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(PasswdsetupActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    FirebaseFirestore data = FirebaseFirestore.getInstance();
                    Map<String, Object> upsex = new HashMap<>();
                    upsex.put("pwd",now.getText().toString());
                    data.collection("user").document(ID)
                            .set(upsex, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(passwdsetup.this, "更改成功", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(passwdsetup.this, "更改失敗", Toast.LENGTH_SHORT).show();
                                }
                            });
                    Intent intent = new Intent(passwdsetup.this, login.class);
                    startActivity(intent);
                }
            }
        });
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