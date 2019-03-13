package com.example.suresh.knowtek;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    Button login;

    TextView textView, validate;

    EditText Lemail, Lpass;

    ProgressBar pb;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        Lemail = (EditText) findViewById(R.id.loginEmail);
        Lpass = (EditText) findViewById(R.id.Loginpassword);

        mAuth = FirebaseAuth.getInstance();

        pb = (ProgressBar)findViewById(R.id.processbar);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        textView = (TextView) findViewById(R.id.signuptext);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));

            }
        });

        validate = (TextView) findViewById(R.id.Confirmlogin);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verify();

            }
        });

        login = (Button) findViewById(R.id.LoginBut);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


    }

    private void verify() {
        String mail = Lemail.getText().toString().trim();
        if (mail.isEmpty()) {
            Lemail.setError("Please Enter Email Address");
            Lemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Lemail.setError("Please Enter correct Email Address");
            Lemail.requestFocus();
            return;

        }


        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Reset Email Has Sent ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (!(task.getException() instanceof FirebaseAuthUserCollisionException))
                            Toast.makeText(getApplicationContext(), "Email is Not Registered", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    private void loginUser() {
        pb.setVisibility(View.VISIBLE);
       String mail = Lemail.getText().toString().trim();
       String  password = Lpass.getText().toString().trim();
        if (mail.isEmpty()) {
            Lemail.setError("Please Enter Email Address");
            Lemail.requestFocus();
            return;

        }
        if (password.isEmpty()) {
            Lpass.setError("Please Enter Password");
            Lpass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(Login.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, ProfileInfo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
