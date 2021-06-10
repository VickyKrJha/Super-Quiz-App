package com.vickysg.myquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;

    private FirebaseDatabase database ;
    private DatabaseReference myRef ;

    private Dialog loadingDialog ;

    public static List<CategoryModel> list ;

    CategoryAdapter adapter ;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //  Start method for Functionality of ToolBar With Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Ending method Here , for Functionality of ToolBar With Back Button


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        database = FirebaseDatabase.getInstance();

        myRef = database.getReference();




        list = new ArrayList<>();


//        list.add(new CategoryModel("" , "Category1"));
//        list.add(new CategoryModel("" , "Category2"));
//        list.add(new CategoryModel("" , "Category3"));
//        list.add(new CategoryModel("" , "Category4"));

        adapter = new CategoryAdapter(list);

        recyclerView.setAdapter(adapter);

        loadingDialog.show();

        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(CategoriesActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.child("sets").getChildren()){
                        sets.add(snapshot1.getKey());
                    }

                    list.add(0,new CategoryModel(snapshot.child("name").getValue().toString(),
                            sets ,
                            snapshot.child("url").getValue().toString(),
                            snapshot.getKey())
                    );
                }

                adapter.notifyDataSetChanged();

                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoriesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });


    }

    //  Start method for Functionality of ToolBar With Back Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    //  Ending method Here , for Functionality of ToolBar With Back Button


    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
}