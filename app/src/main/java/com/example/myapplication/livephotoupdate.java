package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class livephotoupdate extends AppCompatActivity {
    String commentid;
    String commentdelete[];
    int csize;
    String photoname,newphotoname;
    private ImageView i1;
    String data_list;
    StorageReference storageReference,pic_storage;
    int PICK_CONTACT_REQUEST=1;
    Uri uri;
    Boolean photouri=false;
    String name,image,uid;
    String nowimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livephotoupdate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storageReference= FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditText title=findViewById(R.id.title);
        final EditText text=findViewById(R.id.text);
        final EditText id=findViewById(R.id.sender40);
        i1=findViewById(R.id.photo);
        final ImageView iv=findViewById(R.id.imag01);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        ImageButton upload =(ImageButton) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
            }
        });
        for(int i=0; i<livephoto.photo.length; i++){
            if(livephoto.photo[i][5]==livephoto.nowid){
                title.setText(livephoto.photo[i][4]);
                text.setText(livephoto.photo[i][3]);
                id.setText(livephoto.photo[i][2]);
                nowimage=livephoto.photo[i][0];
                uid=livephoto.photo[i][2];
                for (int x = 0; x < login.i; x++) {
                    System.out.println("uid="+login.uid[x]);
                    if (uid.equals(login.uid[x].toString())) {
                        name = login.name[x];
                        System.out.println("name="+name);
                        id.setText(name);
                    }
                }

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
                Picasso.with(livephotoupdate.this).load(nowimage).placeholder(R.mipmap.ic_launcher).transform(transformation).into(i1);   //讀取圖片

                String pname=livephoto.photo[i][0];
                pname=pname.substring(79);
                System.out.println("pname="+pname);
                String[] str=pname.split("\\?");
                System.out.println("str="+str[0]);
                photoname=str[0];
                System.out.println("photoname="+photoname);
            }
        }

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
                                    Glide.with(livephotoupdate.this).load(image)//讀取圖片
                                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                            .into(iv);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("photo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("cid=", livephoto.nowid);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("cid2=", document.getId());
                                if(livephoto.nowid.equals(document.getId())) {
                                    commentid=document.getData().get("commentid").toString();
                                    Log.d("cid3=", commentid);
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            csize=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("commentdelete1", document.getId() + " => " + document.getData());
                                if (document.getData().get("commentid").toString() != null) {
                                    if (document.getData().get("commentid").toString().equals(commentid)) {
                                        csize++;
                                    }
                                }
                            }
                            commentdelete=new String[csize];
                            Log.d("commentdelete size", String.valueOf(commentdelete.length));
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        db.collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("commentid").toString() != null) {
                                    if (document.getData().get("commentid").toString().equals(commentid)) {
                                        commentdelete[i] = document.getId();
                                        Log.d("commentdelete2=", String.valueOf(commentdelete[i]));
                                        i++;
                                    }
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        Button edit =(Button) findViewById(R.id.editrelease);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().equals("")) {
                    Toast.makeText(livephotoupdate.this, "標題請勿空白", Toast.LENGTH_SHORT).show();
                }else if(text.getText().toString().equals("")){
                    Toast.makeText(livephotoupdate.this, "內容請勿空白", Toast.LENGTH_SHORT).show();
                }else if(photouri==true){
                    pic_storage=storageReference.child("photo/"+photoname);
                    pic_storage.delete();

                    newphotoname=randomname();
                    pic_storage=storageReference.child("photo/"+newphotoname);
                    pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> p = new HashMap<>();
                            p.put("image","https://firebasestorage.googleapis.com/v0/b/lalala-c7bcf.appspot.com/o/photo%2F"+newphotoname+"?alt=media");
                            p.put("title", title.getText().toString());
                            p.put("text", text.getText().toString());
                            db.collection("photo").document(livephoto.nowid)
                                    .set(p, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(livephotoupdate.this, "更新成功", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(livephotoupdate.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Intent fri = new Intent(livephotoupdate.this, livephoto.class);
                            startActivity(fri);
                        }
                    });
                }else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> p = new HashMap<>();
                    p.put("title", title.getText().toString());
                    p.put("text", text.getText().toString());
                    db.collection("photo").document(livephoto.nowid)
                            .set(p, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(livephotoupdate.this, "更新成功", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(livephotoupdate.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                }
                            });

                    Intent fri = new Intent(livephotoupdate.this, livephoto.class);
                    startActivity(fri);
                }
            }
        });

//        ImageButton delete =(ImageButton) findViewById(R.id.delete);
//        delete.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//                pic_storage=storageReference.child("photo/"+photoname);
//                pic_storage.delete();
//
//                db.collection("photo").document(photo.nowid)
//                        .delete()
//                        .addOnSuccessListener(new OnSuccessListener() {
//                            @Override
//                            public void onSuccess(Object o) {
//                                Toast.makeText(photo_update.this, "刪除成功", Toast.LENGTH_SHORT).show();
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                            }
//                        });
//                if(commentdelete!=null) {
//                    for (int i = 0; i < commentdelete.length; i++) {
//                        db.collection("comment").document(commentdelete[i])
//                                .delete()
//                                .addOnSuccessListener(new OnSuccessListener() {
//                                    @Override
//                                    public void onSuccess(Object o) {
//                                        Toast.makeText(photo_update.this, "commentdelete success", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                    }
//                                });
//                    }
//                }
//
//                Intent fri=new Intent(photo_update.this,photo.class);
//                startActivity(fri);
//            }
//
//        });



//        ImageButton back =(ImageButton) findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent fri=new Intent(photo_update.this,see.class);
//                startActivity(fri);
//            }
//
//        });
//    }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_CONTACT_REQUEST){
            uri=data.getData();
            i1.setImageURI(uri);
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