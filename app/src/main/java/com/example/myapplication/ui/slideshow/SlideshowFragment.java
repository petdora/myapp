package com.example.myapplication.ui.slideshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.livephotopost;
import com.example.myapplication.myattendact;
import com.example.myapplication.mypostact;
import com.example.myapplication.postwriting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SlideshowFragment extends Fragment {
    private static int count=0;
    public static int size=0;
    public static String photo[][];
    public static String nowid;
    LinearLayout l1;
    float m2;
    int w,h;
    boolean menuiscreate=true;
    private SlideshowViewModel slideshowViewModel;
    public SlideshowFragment(){
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menuiscreate=true) {
            inflater.inflate(R.menu.active, menu);
            menuiscreate=false;
            super.onCreateOptionsMenu(menu, inflater);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.photorelease:
                Intent chang = new Intent(getActivity(), postwriting.class);
                startActivity(chang);
                return true;
            case R.id.myphoto:
                Intent change = new Intent(getActivity(), myattendact.class);
                startActivity(change);
                return true;
            case R.id.mypreasure:
                Intent change1 = new Intent(getActivity(), mypostact.class);
                startActivity(change1);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        setHasOptionsMenu(true);
        //System.out.println(login.loginname);
        DisplayMetrics metrics = new DisplayMetrics();
        DisplayMetrics m=getResources().getDisplayMetrics();
        m2=m.density;
        l1 =(LinearLayout) root.findViewById(R.id.l1);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w=(int)(135*m2);
        h=(int)(120*m2);

        //ImageButton write =(ImageButton) root.findViewById(R.id.write);
//        write.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                System.out.println("test");
//                Intent fri=new Intent(getActivity(), com.example.myapplication.write.class);
//                startActivity(fri);
//            }
//
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("photo").orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            size=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                size++;
                            }
                            photo = new String[size+1][7];
                            Log.d("TAG", String.valueOf(size));
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("photo").orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "main";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("123");
                                count++;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, "size => " + count);
                                String image= document.getData().get("image").toString();
                                String commentid=document.getData().get("commentid").toString();
                                String id=document.getData().get("id").toString();
                                String text=document.getData().get("text").toString();
                                String title=document.getData().get("title").toString();
                                String photoid=document.getId();
                                String nowdate=document.getData().get("time").toString();
                                photo[count][0]=image;
                                photo[count][1]=commentid;
                                photo[count][2]=id;
                                photo[count][3]=text;
                                photo[count][4]=title;
                                photo[count][5]=photoid;
                                photo[count][6]=nowdate;
                            }
                            re();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("comment").orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        //Button act= root.findViewById(R.id.active);
//        act.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent fri=new Intent(photo.this, fragment.class);
//                startActivity(fri);
//            }
//
//        });
//        Button fri= root.findViewById(R.id.best);
//        fri.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent fri=new Intent(photo.this, MainActivity.class);
//                startActivity(fri);
//            }
//
//        });
//        Button per= root.findViewById(R.id.personal);
//        per.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent fri=new Intent(photo.this, Main5Activity.class);
//                startActivity(fri);
//            }
//
//        });
//
//        Button tra= root.findViewById(R.id.translate);
//        tra.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent change = new Intent(photo.this, translate.class);
//                startActivity(change);
//            }
//
//        });

        return root;
    }
    protected void re(){
        for (int i=1;i<=count;i++){
            final int nowcount=i;
            RelativeLayout r1=new RelativeLayout(getContext());
            final ImageButton ib=new ImageButton(getContext());
            ib.setBackgroundColor(Color.TRANSPARENT);
            System.out.println(photo[nowcount][0]);
            Transformation transformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = ib.getWidth();
                    int targetHeight = ib.getHeight();
                    int sourceWidth = source.getWidth();
                    int sourceHeight = source.getHeight();

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
            Picasso.with(getActivity()).load(photo[nowcount][0]).placeholder(R.mipmap.ic_launcher).transform(transformation).into(ib);   //讀取圖片  fit()
            ib.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    System.out.println("now="+nowcount);
                    nowid=photo[nowcount][5];
                    System.out.println(nowid);
                    Intent fri=new Intent(getActivity(), livephotopost.class);
                    startActivity(fri);
                }

            });
            RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams (w,h);
            btParams.leftMargin =(int)(60*m2);
            btParams.topMargin =(int)(15*m2);
            r1.addView(ib,btParams);
            i++;
            System.out.println(i);
            if(i<=count){
                final int nowcount2=i;
                final ImageButton ib2=new ImageButton(getActivity());
                ib2.setBackgroundColor(Color.TRANSPARENT);
                Transformation transformation2 = new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = ib2.getWidth();
                        int targetHeight = ib2.getHeight();
                        int sourceWidth = source.getWidth();
                        int sourceHeight = source.getHeight();

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
                Picasso.with(getActivity()).load(photo[nowcount2][0]).placeholder(R.mipmap.ic_launcher).transform(transformation2).into(ib2);
                ib2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        System.out.println("now="+nowcount2);
                        nowid=photo[nowcount2][5];
                        System.out.println(nowid);

                        Intent fri=new Intent(getActivity(),livephotopost.class);
                        startActivity(fri);
                    }

                });
                RelativeLayout.LayoutParams btParams2 = new RelativeLayout.LayoutParams (w,h);
                btParams2.leftMargin =(int)(236*m2);
                btParams2.topMargin =(int)(15*m2);
                r1.addView(ib2,btParams2);
            }
            l1.addView(r1);
        }
    }
}
