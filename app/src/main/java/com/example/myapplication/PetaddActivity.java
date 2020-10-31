package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PetaddActivity extends AppCompatActivity {
    EditText nameID,gender,birthday,introduction;
    ImageButton image,birth;
    private ImageView imageview;
    String data_list;
    int PICK_CONTACT_REQUEST=1;
    Uri uri;
    StorageReference storageReference,pic_storage;
    Boolean photouri=false;
    String petsex="";
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    TextView petbirth;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petadd);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendar = Calendar.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        petbirth=findViewById(R.id.textView);
        image=findViewById(R.id.imageview);
        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
            }
        });
        //imageview=(ImageView)findViewById(R.id.imageview);
        nameID=findViewById(R.id.nameID);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        final String[] whatsex = {"男","女"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(PetaddActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                whatsex);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(PetaddActivity.this, "您選擇了:" + whatsex[position], Toast.LENGTH_SHORT).show();
                petsex=whatsex[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //birthday=findViewById(R.id.birthday);
        introduction=findViewById(R.id.introduction);
        birth=findViewById(R.id.birth);
        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalenderDialog();
                date = new Date();
//注意format的格式要與日期String的格式相匹配
                DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    date = sdf.parse(petbirth.getText().toString());
                    System.out.println(date.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("name="+nameID.getText());
                if (nameID.getText().toString().equals("")) {
                    Toast.makeText(PetaddActivity.this, "愛稱請勿空白", Toast.LENGTH_SHORT).show();
                } else if (petsex.equals("")) {
                    Toast.makeText(PetaddActivity.this, "性別請勿空白", Toast.LENGTH_SHORT).show();
                } else if (date.toString().equals("")) {
                    Toast.makeText(PetaddActivity.this, "生日請勿空白", Toast.LENGTH_SHORT).show();
                } else if (introduction.getText().toString().equals("")) {
                    Toast.makeText(PetaddActivity.this, "品種請勿空白", Toast.LENGTH_SHORT).show();
                }else if(photouri==false){
                    Toast.makeText(PetaddActivity.this, "請上傳寵物頭貼", Toast.LENGTH_SHORT).show();
                } else {
                    String rn=randomname();
                    final String photoname=rn+".jpg";
                    pic_storage=storageReference.child("pet/"+photoname);
                    pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //圖片上傳需要一點時間 直接跳轉會來不及顯示讀片
                            Map<String, Object> p = new HashMap<>();
                            p.put("nameID", nameID.getText().toString());
                            p.put("gender", petsex);
                            p.put("birthday", date.toString());
                            p.put("introduction", introduction.getText().toString());
                            p.put("image", "https://firebasestorage.googleapis.com/v0/b/lalala-c7bcf.appspot.com/o/pet%2F"+photoname+"?alt=media");
                            p.put("user", "ck_is_so_cute");


                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("pet")
                                    .add(p)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("add", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        private static final String TAG = "ta";

                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("add", "Error adding document", e);
                                        }
                                    });
                            Map<String, Object> p2 = new HashMap<>();
                            ArrayList arrayList = new ArrayList();
                            for (int i = 0; i < PetchooseActivity.petdata.length; i++) {
                                if (PetchooseActivity.petdata[i][0] == null) {
                                    break;
                                }
                                arrayList.add(PetchooseActivity.petdata[i][0]);
                            }
                            arrayList.add(nameID.getText().toString());
                            p2.put("petname", arrayList);
                            db.collection("user").document("NuqOAo8zk0k2A6cIER2K")
                                    .set(p2, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(PetaddActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            System.out.println("更新成功");

                                            Intent intent = new Intent(PetaddActivity.this, PetchooseActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PetaddActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                            System.out.println("更新失敗");
                                        }
                                    });
                        }
                    });

                }
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_CONTACT_REQUEST){
            uri=data.getData();
            image.setImageURI(uri);
            ContentResolver contentResolver=getContentResolver();
            MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
            data_list=mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
            photouri=true;
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
    private void showCalenderDialog(){
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String calender = year + "/" + (month + 1) + "/" + dayOfMonth ;
                petbirth.setText(calender);
                Log.e("TAG", "calender : " + calender);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

}