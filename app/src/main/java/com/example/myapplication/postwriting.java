package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.myapplication.ui.home.HomeFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class postwriting extends AppCompatActivity {

    Intent intent;
    int PICK_CONTACT_REQUEST = 1;
    Uri uri;
    ImageButton add,start,finish;
    String data_list;
    static int act_id;
    TextView starttime,finishtime;
    EditText title,lastlocation,context,max;
    TextView notnull;
    String uuid,longaddress;
    StorageReference storageRef,mountainsRef;
    Button sdate,fdate;
    private Date startTime = new Date();
    private Date endTime = new Date();
    private DatePickerDialog datePickerDialog;
    TimePickerDialog picker;
    private NumberPicker mNumberPicker ;
    private Calendar calendar;
    private TimePickerDialog timePickerDialog;
    private TimePickerView pvTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postwriting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        calendar = Calendar.getInstance();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        reload();
        title=findViewById(R.id.title);
        start=findViewById(R.id.start);
        finish=findViewById(R.id.finish);
        lastlocation=findViewById(R.id.lastlocation);
        context=findViewById(R.id.context);
        max=findViewById(R.id.max);

        notnull.setVisibility(View.GONE);
        start.setOnClickListener(textViewClickListener);
        finish.setOnClickListener(textViewClickListener);

        initTimePicker();


        Places.initialize(getApplicationContext(),"AIzaSyCwE6QqfBf8u9Br-J-QAEJENrdD5B4BVu4");
        lastlocation.setFocusable(false);
        lastlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(postwriting.this);
                startActivityForResult(intent,100);
            }
        });
        uuid = UUID.randomUUID().toString();
        final FirebaseStorage storage = FirebaseStorage.getInstance("gs://lalala-c7bcf.appspot.com");//最外面的網址
        storageRef  = storage.getReference("active");//資料夾!
        StorageReference spaceRef = storageRef.child("images/"+uuid);
        String path = spaceRef.getPath();
        mountainsRef = storageRef.child(uuid+".jpg");

//        Button per = findViewById(R.id.back);
//        per.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent change = new Intent(postwriting.this, fragment.class);
//                startActivity(change);
//            }
//
//        });
     /*   Button reles = findViewById(R.id.button2);
        reles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent change = new Intent(release.this, fragment.class);
                startActivity(change);
            }

        });*/

        add = findViewById(R.id.photo);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }

        });
        Button submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().equals("")
                        ||starttime.getText().toString().equals("")
                        ||finishtime.getText().toString().equals("")
                        ||lastlocation.getText().toString().equals("")
                        ||context.getText().toString().equals("")
                        ||max.getText().toString().equals("")){
                    Toast.makeText(postwriting.this,"請確實填寫",Toast.LENGTH_LONG).show();

                }else {
                    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date sdate = new Date();
                    Date fdate = new Date();

                    try {
                        sdate = sdf.parse(starttime.getText().toString());

                        fdate = sdf.parse(finishtime.getText().toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = ((BitmapDrawable) add.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);

                    Uri file = Uri.fromFile(new File("path/to/" + uuid + ".jpg"));

                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpg")
                            .build();
                    uploadTask = storageRef.child("images/" + file.getLastPathSegment()).putFile(file, metadata);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            // ...
                        }
                    });
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> active = new HashMap<>();
                    Log.d("2202", "count" + ":" + act_id + 1);
                    active.put("actid",act_id+1);
                    active.put("address",lastlocation.getText().toString());
                    active.put("detail",context.getText().toString());
                    active.put("fdate",fdate);
                    active.put("maxmember",Integer.parseInt(max.getText().toString()));
                    active.put("photo","https://firebasestorage.googleapis.com/v0/b/lalala-c7bcf.appspot.com/o/active%2F"+uuid+".jpg"+"?alt=media");
                    active.put("postid", login.loginid);
                    active.put("posttime", new Date());
                    active.put("sdate", sdate);
                    active.put("title", title.getText().toString());
                    db.collection("active")
                            .document()//document可填可不填，更新或新增集合!
                            .set(active)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error writing document", e);
                                }
                            });
                    Intent pick=new Intent(postwriting.this, active.class);
                    startActivity(pick);
                }

            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            uri = data.getData();
            Log.d("22", "uri:" + uri);
            add.setImageURI(uri);
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            data_list = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }  else if(requestCode==100){
            Place place= Autocomplete.getPlaceFromIntent(data);
            lastlocation.setText(place.getName());
            longaddress=place.getAddress();
            // textView1.setText(String.format("Locality Name :%s",place.getName()));
            Log.d("name", String.valueOf(requestCode));
            // textView2.setText(String.valueOf(place.getLatLng()));
        }else if(resultCode== AutocompleteActivity.RESULT_ERROR){
            Status status=Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void reload() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            act_id=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // act_id=document.getLong("actid");
                                act_id++;
                            }

                        } else {
                            Log.w("000", "Error getting documents.", task.getException());
                        }

                    }
                });
    }
    View.OnClickListener textViewClickListener = new View.OnClickListener() {//Textview監聽要加這行
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start:
                    if (pvTime != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startTime);
                        pvTime.setDate(calendar);
                        pvTime.show(view);
                    }
                    break;
                case R.id.finish:
                    if (pvTime != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(endTime);
                        pvTime.setDate(calendar);
                        pvTime.show(view);
                    }
                    break;
            }
        }
    };
    private void initTimePicker() {

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //如果是開始時間的EditText
                if(v.getId() == R.id.start){
                    startTime = date;
                }else {
                    endTime = date;
                }
                EditText editText = (EditText)v;
                editText.setText(getTime(date));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {

                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true)
                .build();


        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改動畫樣式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部顯示
            }
        }
    }

    private String getTime(Date date) {//可根據需要自行擷取資料顯示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
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
                final String calender = year + "/" + (month + 1) + "/" + dayOfMonth ;
                //   birthday.setText(calender);
                Log.e("TAG", "calender : " + calender);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog( postwriting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                // time.setText(sHour + ":" + sMinute);
                                //  timer=time.getText().toString();
                                String time;
                                if(sHour<10){
                                    if(sMinute<10){
                                        time="0"+sHour + ":0" + sMinute;
                                    }else{
                                        time="0"+sHour + ":" + sMinute;
                                    }
                                }else{
                                    if(sMinute<10){
                                        time=sHour + ":0" + sMinute;
                                    }else{
                                        time=sHour + ":" + sMinute;
                                    }
                                }

                                starttime.setText(calender+"  "+time);
                            }
                        }, hour, minutes, true);
                picker.show();

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }



    private void showCalenderDialog2(){
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String calender = year + "/" + (month + 1) + "/" + dayOfMonth ;
                //   birthday.setText(calender);
                Log.e("TAG", "calender : " + calender);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog( postwriting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                // time.setText(sHour + ":" + sMinute);
                                //  timer=time.getText().toString();
                                String time;
                                if(sHour<10){
                                    if(sMinute<10){
                                        time="0"+sHour + ":0" + sMinute;
                                    }else{
                                        time="0"+sHour + ":" + sMinute;
                                    }
                                }else{
                                    if(sMinute<10){
                                        time=sHour + ":0" + sMinute;
                                    }else{
                                        time=sHour + ":" + sMinute;
                                    }
                                }
                                finishtime.setText(calender+"  "+time);
                                System.out.println(finishtime.getText());

                            }
                        }, hour, minutes, true);
                picker.show();

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }



}