package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Petsetup extends AppCompatActivity {
    int Preset=0,whatsex;
    FirebaseFirestore data;
    TextView nameID,gender,birthday,introduction;
    EditText setname,setintroduction;
    ImageView imageview;
    Button name,gen,bir,in,im,nameok,inok;
    String ID,uuid;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private Date date;
    StorageReference mountainsRef,storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petsetup);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();

        uuid = UUID.randomUUID().toString();
        storageReference= FirebaseStorage.getInstance().getReference();
        data = FirebaseFirestore.getInstance();

        final String petname=petAdapter.petname;
        nameID=findViewById(R.id.nameID);
        gender=findViewById(R.id.gender);
        birthday=findViewById(R.id.birthday);
        introduction=findViewById(R.id.introduction);
        imageview=findViewById(R.id.imageview);
        nameok=findViewById(R.id.nameok);
        nameok.setVisibility(View.GONE);
        inok=findViewById(R.id.inok);
        inok.setVisibility(View.GONE);
        setname=findViewById(R.id.setname);
        setname.setVisibility(View.GONE);
        setintroduction=findViewById(R.id.setintroduction);
        setintroduction.setVisibility(View.GONE);

        data.collection("pet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("nameID").equals(petname)){
                                    ID= document.getId();
                                    nameID.setText(document.getString("nameID"));
                                    gender.setText(document.getString("gender"));
                                    Date birth=document.getDate("birthday");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                    birthday.setText(sdf.format(birth));
                                    introduction.setText(document.getString("introduction"));
                                    /*Picasso.with(PetsetupActivity.this).load(document.getString("image"))
                                            .placeholder(R.mipmap.ic_launcher)
                                            .into(imageview);*/
                                    Glide.with(Petsetup.this).load(document.getString("image"))
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(imageview);
                                }
                            }
                        } else {
                            Log.w("000", "Error getting documents.", task.getException());
                        }

                    }
                });

        name=findViewById(R.id.name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameID.setVisibility(View.GONE);
                setname.setText(nameID.getText());
                setname.setVisibility(View.VISIBLE);
                name.setVisibility(View.GONE);
                nameok.setVisibility(View.VISIBLE);

                nameok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nameID.setText(setname.getText());
                        setname.setVisibility(View.GONE);
                        nameID.setVisibility(View.VISIBLE);
                        nameok.setVisibility(View.GONE);
                        name.setVisibility(View.VISIBLE);
                        Map<String, Object> upname = new HashMap<>();
                        upname.put("nameID",nameID.getText().toString());
                        data.collection("pet").document(ID)
                                .set(upname, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(Petsetup.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Petsetup.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

            }
        });
        gen=findViewById(R.id.gen);
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] strings={"男","女"};

                AlertDialog.Builder builder=new AlertDialog.Builder(Petsetup.this);
                builder.setSingleChoiceItems(strings, Preset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        whatsex=which;//把預設值改成選擇的
                        Log.d("22", String.valueOf(Preset));


                    }
                });
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(whatsex==0){
                            gender.setText("男");
                        }else if(whatsex==1){
                            gender.setText("女");
                        }
                        dialog.dismiss();//結束對話框
                        Map<String, Object> upsex = new HashMap<>();
                        upsex.put("gender",gender.getText().toString());
                        data.collection("pet").document(ID)
                                .set(upsex, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(Petsetup.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Petsetup.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.show();

            }
        });
        bir=findViewById(R.id.bir);
        bir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalenderDialog();
            }
        });
        in=findViewById(R.id.in);
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introduction.setVisibility(View.GONE);
                setintroduction.setText(introduction.getText());
                setintroduction.setVisibility(View.VISIBLE);
                in.setVisibility(View.GONE);
                inok.setVisibility(View.VISIBLE);

                inok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        introduction.setText(setintroduction.getText());
                        setintroduction.setVisibility(View.GONE);
                        introduction.setVisibility(View.VISIBLE);
                        inok.setVisibility(View.GONE);
                        in.setVisibility(View.VISIBLE);
                        Map<String, Object> upname = new HashMap<>();
                        upname.put("introduction",introduction.getText().toString());
                        data.collection("pet").document(ID)
                                .set(upname, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(Petsetup.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Petsetup.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

            }
        });
        im=findViewById(R.id.im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");//图片
                startActivityForResult(galleryIntent,1);
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

    private void showCalenderDialog(){
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String ca = year + "/" + (month + 1) + "/" + dayOfMonth ;
                birthday.setText(ca);
                Log.e("TAG", "calender : " + ca);

                date = new Date();
//注意format的格式要與日期String的格式相匹配
                DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    date = sdf.parse(birthday.getText().toString());
                    System.out.println(date.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                calendar.setTime(date);
                Map<String, Object> upbir = new HashMap<>();
                upbir.put("birthday",date);
                data.collection("pet").document(ID)
                        .set(upbir, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(Petsetup.this, "更新成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Petsetup.this, "更新失敗", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            Uri uri=data.getData();
            String newname=randomname();
            final String newurl="https://firebasestorage.googleapis.com/v0/b/lalala-c7bcf.appspot.com/o/pet%2F"+newname+".jpg"+"?alt=media";
            mountainsRef=storageReference.child("pet/"+newname+".jpg");
            mountainsRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Map<String, Object> photo = new HashMap<>();
                    photo.put("image",newurl);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("pet").document(ID)
                            .set(photo, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(Petsetup.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    Glide.with(Petsetup.this).load(newurl)
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(imageview);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Petsetup.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected String randomname(){
        int z;
        StringBuilder sb =new StringBuilder();
        int i;
        for(i=0;i<10;i++){
            z=(int)((Math.random()*7)%3);
            if(z==1){
                sb.append((int)(Math.random()*10)+48);
            }else if(z==2){
                sb.append((char)((Math.random()*26)+65));
            }else{
                sb.append((char)((Math.random()*26)+97));
            }
        }
        return sb.toString();
    }

}