package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class livephotopost extends AppCompatActivity {
    private static int now;
    String nowcid;
    String nowimage;
    String name,image,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livephotopost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        final EditText title=findViewById(R.id.title);
        final EditText text=findViewById(R.id.text);
        final EditText id=findViewById(R.id.id);
        final EditText comment=findViewById(R.id.write);
        final LinearLayout c=findViewById(R.id.clayout);
        final ImageView i1=findViewById(R.id.photo);
        final ImageView iv=findViewById(R.id.ImageVie);
        final TextView date=findViewById(R.id.date1);
        for(int i=1; i<=livephoto.size; i++){
            if(livephoto.photo[i][5]==livephoto.nowid){
                title.setText(livephoto.photo[i][4]);
                text.setText(livephoto.photo[i][3]);
                String pdate=livephoto.photo[i][6];
                String pdate2[]=pdate.split(" ");
                String pdate3[]=pdate2[0].split("-");
                date.setText(pdate3[0]+"/"+pdate3[1]+"/"+pdate3[2]);
                uid=livephoto.photo[i][2];
                for (int x = 0; x < login.i; x++) {
                    System.out.println("uid="+login.uid[x]);
                    if (uid.equals(login.uid[x].toString())) {
                        name = login.name[x];
                        System.out.println("name="+name);
                        id.setText(name);
                    }
                }
                nowcid=livephoto.photo[i][1];
                nowimage=livephoto.photo[i][0];

                Transformation transformation = new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = i1.getWidth();
                        int targetHeight = i1.getHeight();
                        int sourceWidth = source.getWidth();
                        int sourceHeight = source.getHeight();

                        if (source.getWidth() == 0) {
                            return source;
                        }
                        if(sourceWidth<sourceHeight){
                            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                            targetHeight = (int) (targetWidth * aspectRatio);
                        }else if(sourceWidth>sourceHeight){
                            double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                            targetWidth = (int) (targetHeight * aspectRatio);
                        }
                        if (targetHeight != 0 && targetWidth != 0) {
                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                            if (result != source) {
                                source.recycle();
                            }
                            return result;
                        } else {
                            return source;
                        }
                    }
                    @Override
                    public String key() {
                        return "transformation" + " desiredWidth";
                    }
                };
                Picasso.with(livephotopost.this).load(nowimage).placeholder(R.mipmap.ic_launcher).transform(transformation).into(i1);   //讀取圖片

                System.out.println(nowimage);
                now=i;
                break;
            }
        }
        DisplayMetrics m=getResources().getDisplayMetrics();
        final float m2=m.density;
        System.out.println("now"+nowcid);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "t";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("name").toString().equals(name)) {
                                    image=document.getData().get("photo").toString();
                                    Glide.with(livephotopost.this).load(image)//讀取圖片
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(iv);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        db.collection("comment").orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "t";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("commentid").toString());
                                if(document.getData().get("commentid").toString().equals(nowcid)) {
                                    RelativeLayout r1 = new RelativeLayout(livephotopost.this);
                                    TextView t1 = new TextView(livephotopost.this);
                                    for (int x = 0; x < login.i; x++) {
                                        if (document.getData().get("id").toString().equals(login.uid[x].toString())) {
                                            t1.setText(login.name[x] + " : " + document.getData().get("comment").toString());
                                            System.out.println("name="+name);
                                            break;
                                        }
                                    }
                                    RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((int) (420 * m2), (int) (40 * m2));
                                    btParams.leftMargin = 0;
                                    btParams.topMargin = 0;
                                    r1.addView(t1, btParams);
                                    c.addView(r1);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



        ImageButton send =(ImageButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String cc=comment.getText().toString();
                if(!cc.equals("")) {
                    String commentid = livephoto.photo[now][1];
                    RelativeLayout r1 = new RelativeLayout(livephotopost.this);
                    TextView t1 = new TextView(livephotopost.this);
                    t1.setText(login.name + " : " + cc);
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Map<String, Object> ctext = new HashMap<>();
                    ctext.put("comment", cc);
                    ctext.put("time", nowDate);
                    ctext.put("commentid", commentid);
                    ctext.put("id", login.loginid);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("comment")
                            .add(ctext)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                private static final String TAG = "ta";

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error adding document", e);
                                }
                            });
                    RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((int) (420 * m2), (int) (40 * m2));
                    btParams.leftMargin = 0;
                    btParams.topMargin = 0;
                    r1.addView(t1, btParams);
                    c.addView(r1);
                    comment.setText("");
                }
            }

        });



            }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.livephotopost, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.photoedit:
                Intent chang = new Intent(livephotopost.this, livephotoupdate.class);
                startActivity(chang);
                return true;


            case R.id.photodelete:
                //要寫刪除生活照，原本的要刪除還要跳到update頁面才能刪怪怪的
                return true;
            case R.id.photoshare:
                //分享還沒做



            default:
                return super.onOptionsItemSelected(item);
        }
    }



}