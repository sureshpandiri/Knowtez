package com.example.suresh.knowtek;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {

    EditText email, pass, confpass;

    private FirebaseAuth mAuth;

    private Button signup;

    TextView textView;

    private Toolbar tb;

    ProgressBar pb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        pb = (ProgressBar)findViewById(R.id.processbar);

        email = (EditText) findViewById(R.id.signupemail);
        pass = (EditText) findViewById(R.id.signuppassword);
        confpass = (EditText) findViewById(R.id.confpassword);
        signup = (Button) findViewById(R.id.register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


        textView = findViewById(R.id.newlogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });

    }
    private void register() {
        pb.setVisibility(View.VISIBLE);
        String mail = email.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String confirmpas = confpass.getText().toString().trim();

        if (mail.isEmpty()) {
            email.setError("Please Enter Email Address");
            email.requestFocus();
            return;

        }
        if (password.isEmpty()) {
            pass.setError("Please Enter Password");
            pass.requestFocus();
            return;
        }
        if (confirmpas.isEmpty()) {
            confpass.setError("Please Enter Above Password");
            confpass.requestFocus();
            return;

        }
        if (!confirmpas.trim().equals(password.trim())) {
            confpass.setError("Please Enter Above Password");
            confpass.requestFocus();
            return;

        }
        if (password.length() < 6) {
            pass.setError("Please Enter More Than 6 Letters");
            pass.requestFocus();
            return;

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Please Enter correct Email Address");
            email.requestFocus();
            return;

        }

        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            finish();

                            Toast.makeText(getApplicationContext(), "Successfully registerd", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this,ProfileInfo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(getApplicationContext(), "Email is already Registered", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this,ProfileInfo.class));

        }
    }
}
