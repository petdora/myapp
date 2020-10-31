package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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

public class UserActivity extends AppCompatActivity {
    int Preset=0,whatsex;
    FirebaseFirestore data;
    EditText setname,setcontext;
    TextView nameID,gender,birthday,introduction;
    // ImageButton nameok;
    Button name,nameok,sex,context,contextok;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    public static final int CROP_PHOTO = 2;
    private ImageView showImage;
    String ID,image,uuid;
    StorageReference storageRef,mountainsRef,storageReference;
    private Uri imageUri;
    private static final int RESULT = 1;
    //  String[] whatsex = {"男","女"};
    //圖片名稱
    private String filename;
    Intent intent;
    boolean mCircleSeparator = false;
    private TextView lbl_imgpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        showImage = (ImageView) findViewById(R.id.iv_personal_icon);
        storageReference = FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Button photo = findViewById(R.id.photo);
        introduction = findViewById(R.id.introduction);
        setcontext = findViewById(R.id.setcontext);
        setname = findViewById(R.id.setname);
        birthday = findViewById(R.id.birthday);
        setname.setVisibility(View.GONE);
        nameID = findViewById(R.id.nameID);
        name = findViewById(R.id.name);
        nameok = findViewById(R.id.nameok);
        nameok.setVisibility(View.GONE);
        contextok = findViewById(R.id.contextok2);
        contextok.setVisibility(View.GONE);
        setcontext.setVisibility(View.GONE);
        uuid = UUID.randomUUID().toString();
        final FirebaseStorage storage = FirebaseStorage.getInstance("gs://lalala-c7bcf.appspot.com");//最外面的網址
        storageRef  = storage.getReference("user");//資料夾!
        StorageReference spaceRef = storageRef.child("images/"+uuid);
        String path = spaceRef.getPath();
        mountainsRef = storageRef.child(uuid+".jpg");
        data = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("HII", document.getId() + " => " + document.getLong("actid"));
                                //if(login.loginid==document.getLong("uid")){
                                if(document.getLong("uid")==1){
                                    ID= document.getId();
                                    nameID.setText(document.getString("name"));
                                    gender.setText(document.getString("sex"));
                                    Date birth=document.getDate("bir");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                    birthday.setText(sdf.format(birth));
                                    introduction.setText(document.getString("context"));
                                    image=document.getString("photo");
                                }

                            }


                            Log.d("0800", "att" + image);


                            Glide.with(UserActivity.this).load(image)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(showImage);

                        } else {
                            Log.w("000", "Error getting documents.", task.getException());
                        }

                    }
                });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");//图片
                startActivityForResult(galleryIntent,1);
            }

        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameID.setVisibility(View.GONE);
                setname.setVisibility(View.VISIBLE);
                setname.setText(nameID.getText());
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
                        upname.put("name",nameID.getText().toString());
                        data.collection("user").document(ID)
                                .set(upname, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //其他資料庫中的name也要一起改，最後再來看有哪些要改
                    }
                });

            }
        });
        sex=findViewById(R.id.sex);

        gender=findViewById(R.id.gender);
        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] strings={"男","女"};

                AlertDialog.Builder builder=new AlertDialog.Builder(UserActivity.this);
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
                        upsex.put("sex",gender.getText().toString());
                        data.collection("user").document(ID)
                                .set(upsex, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.show();

            }
        });
        Button bir=findViewById(R.id.birth);

        bir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalenderDialog();
            }
        });




        context=findViewById(R.id.context);

        context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setVisibility(View.GONE);
                introduction.setVisibility(View.GONE);
                introduction.setText(context.getText());
                contextok.setVisibility(View.VISIBLE);
                setcontext.setVisibility(View.VISIBLE);
                contextok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        introduction.setText(setcontext.getText());
                        contextok.setVisibility(View.GONE);
                        introduction.setVisibility(View.VISIBLE);
                        context.setVisibility(View.VISIBLE);
                        setcontext.setVisibility(View.GONE);
                        Map<String, Object> upcontext = new HashMap<>();
                        upcontext.put("context",introduction.getText().toString());
                        data.collection("user").document(ID)
                                .set(upcontext, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                });
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

                Date date = new Date();
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
                upbir.put("bir",date);
                data.collection("user").document(ID)
                        .set(upbir, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
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
            final String newurl="https://firebasestorage.googleapis.com/v0/b/lalala-c7bcf.appspot.com/o/user%2F"+newname+".jpg"+"?alt=media";
            mountainsRef=storageReference.child("user/"+newname+".jpg");
            mountainsRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Map<String, Object> photo = new HashMap<>();
                    photo.put("photo",newurl);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user").document(ID)
                            .set(photo, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                    Glide.with(UserActivity.this).load(newurl)
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(showImage);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showPicture(String img_uri) {
        showImage.setImageBitmap(BitmapFactory.decodeFile(img_uri));
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