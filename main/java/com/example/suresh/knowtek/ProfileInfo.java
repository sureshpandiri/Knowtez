package com.example.suresh.knowtek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

public class ProfileInfo extends AppCompatActivity {

    ImageView imageView;

    ProgressBar pb;

    private static final int CHOOSE = 101;

    Uri profileImg;

    Button next;

    EditText name;

    String profileData;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.username);
        pb = (ProgressBar)findViewById(R.id.processbar);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        imageView = (ImageView) findViewById(R.id.profileIMg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagechooser();
            }
        });

        loadinfo();

    }

    private void loadinfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getDisplayName()!=null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getPhotoUrl()!=null) {
                name.setText(user.getDisplayName());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Homepage.class));

        }
    }

    private void saveFile() {
        String uname = name.getText().toString();
        if (uname.isEmpty()) {
            name.setError("Please Enter Email Address");
            name.requestFocus();
            return;
        }
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null && profileData != null) {
            pb.setVisibility(View.VISIBLE);
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(uname)
                    .setPhotoUri(Uri.parse(profileData))
                    .build();

            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pb.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        finish();
                        Toast.makeText(ProfileInfo.this, "proflie is updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileInfo.this,Homepage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ProfileInfo.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void imagechooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "select image"), CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImg);
                imageView.setImageBitmap(bitmap);
                uploadImg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImg() {
        final StorageReference reference = FirebaseStorage.getInstance()
                .getReference("profilepic/" + System.currentTimeMillis() + ".jpg");
        if (profileImg != null) {
            reference.putFile(profileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileData = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}
