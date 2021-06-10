package com.vickysg.myquizapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.vickysg.myquizapp.databinding.FragmentProfileBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private InterstitialAd mInterstitialAd;

String oldName ;
    public ProfileFragment() {
        // Required empty public constructor
    }

Dialog dialog ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FragmentProfileBinding binding ;

    FirebaseFirestore database ;
    FirebaseAuth auth ;
    String uid ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater,container,false);

        //  Ads Start here
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(),"ca-app-pub-3912259549278001/9918008344", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.show(getActivity());
            }
        });

        // Ads End Here

        dialog = new ProgressDialog(getContext());

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

         uid = auth.getUid();

//        DocumentReference documentReference = database.collection("users").document(uid);

        database.collection("users").document(uid).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                User user = new User();

                binding.nameBox.setText(documentSnapshot.getString("name"));
                binding.emailBox.setText(documentSnapshot.getString("email"));
                binding.passBox.setText(documentSnapshot.getString("pass"));

                oldName = binding.nameBox.getText().toString();

                if (documentSnapshot.getString("profile") != null){
                    Glide.with(ProfileFragment.this)
                            .load(documentSnapshot.getString("profile"))
                            .into(binding.profileImage);

                }



            }
        });


       binding.updateBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String name = binding.nameBox.getText().toString();

                if (name.equals(oldName)){
                    Toast.makeText(getContext(), "Your Name is Also same as Old Name", Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, Object> map = new HashMap<>();

                    map.put("name", name);


                    dialog.setTitle("Name is Updating...");

                    dialog.show();
                    database.collection("users")
                            .document(uid).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                       //     FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Your Name is Updated Successfully", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getActivity() , MainActivity.class));
                                getActivity().finish();

                            }else{
                                Toast.makeText(getContext(), "Update Action Failed!! Please Try Again Later!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
           }
       });


       return binding.getRoot();
   }
}