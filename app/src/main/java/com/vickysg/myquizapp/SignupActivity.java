package com.vickysg.myquizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vickysg.myquizapp.databinding.ActivitySignupBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding ;

    FirebaseAuth auth;
    FirebaseUser userf ;
    FirebaseFirestore database ;

    ProgressDialog dialog ;

    private final int REQ = 1 ;

    // this variable is store the image download url
    String downloadUrl = "";

    // Variable for bitmap
    private Bitmap bitmap;

    private DatabaseReference reference;
    private StorageReference storageReference;

    String email, pass, name, refercode;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //   For Account Build up
        auth = FirebaseAuth.getInstance();
        userf = auth.getCurrentUser();

        database = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        dialog = new ProgressDialog(this);
        dialog.setMessage("We are Creating New Account...");

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling Function for Open Gallery
                openGallery();
            }
        });

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = binding.emailBox.getText().toString();
                pass = binding.passwordBox.getText().toString();
                name = binding.nameBox.getText().toString();
                refercode = binding.referBox.getText().toString();

                if (name.isEmpty()) {
                    binding.nameBox.setError("Name is Required*");
                    binding.nameBox.requestFocus();
                }  else if (email.isEmpty()) {
                    binding.emailBox.setError("Email is Required*");
                    binding.emailBox.requestFocus();
                } else if (pass.isEmpty()) {
                    binding.passwordBox.setError("Password is Required*");
                    binding.passwordBox.requestFocus();
                } else if (refercode.isEmpty()) {
                    binding.referBox.setError("Refer Code is Required*");
                    binding.referBox.requestFocus();
                } else if (bitmap == null) {   // Image is Stored in bitmap
                    Toast.makeText(SignupActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                } else {
                    // Calling Function or method for Uploading Image
                    uploadImage();
                }


            }
        });



        binding.gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }


    // Calling Function or method for Uploading Image
    private void uploadImage() {

        dialog.setMessage("Uploading...");
        dialog.show();

        // first Compressing the image then upload image

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImage = baos.toByteArray();

        final StorageReference filePath = storageReference.child("ProfileImages").child(finalImage + "jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImage);

        uploadTask.addOnCompleteListener(SignupActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    // For getting the Path of Image
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    // Calling uploadData()  function or method
                                    uploadData();
                                }
                            });
                        }
                    });
                }else {
                    dialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
// Ending here , Calling Function or method for Uploading Image
// Calling Function or method for Uploading Data in FireBase
    private void uploadData() {

            User user = new User(name, email, pass, downloadUrl, refercode, 25);

            dialog.show();
            //  dialog.setMessage("We are Creating New Account...");

            if (auth.getCurrentUser().isAnonymous()) {

                userf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignupActivity.this, "Temp User Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String uid = task.getResult().getUser().getUid();

                        database
                                .collection("users")
                                .document(uid)
                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();

                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }

    // Making or Creating Function for Open Gallery
    private void openGallery() {

        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();

            // We Store Image in Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            binding.profileImage.setImageBitmap(bitmap);
        }
    }
    // End here , Making or Creating Function for Open Gallery
}