package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetchooseActivity extends AppCompatActivity {
    private List<pet> bookList=new ArrayList<>();
    public static String[][] petdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petchoose);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView add=findViewById(R.id.addpet);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetchooseActivity.this, PetaddActivity.class);
                startActivity(intent);
            }
        });

        petdata=new String[100][2];
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        //資料寫死 記得改

        data.collection("pet").whereEqualTo("user","ck_is_so_cute")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name=document.getData().get("nameID").toString();
                                String image=document.getData().get("image").toString();
                                petdata[i][0]=name;
                                petdata[i][1]=image;
                                i++;
                            }
                            lay();
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void initBooks() {
        for (int i = 0; i < petdata.length; i++) {
            if(petdata[i][0]==null){
                break;
            }
            pet friends = new pet(petdata[i][0], petdata[i][1]);
            bookList.add(friends);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lay(){
        initBooks();
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.id_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        petAdapter adapter=new petAdapter(bookList,this);
        recyclerView.setAdapter(adapter);
    }
}