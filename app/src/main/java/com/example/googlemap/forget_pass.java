package com.example.googlemap;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forget_pass extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPassB;
    private ProgressBar progressBar2;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass);

        emailEditText=(EditText) findViewById(R.id.emailpass);
        resetPassB=(Button) findViewById(R.id.resetpass);
        progressBar2=(ProgressBar) findViewById(R.id.progressBar2);

        auth=FirebaseAuth.getInstance();
        resetPassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }
    private void resetPassword(){
        String email=emailEditText.getText().toString().trim();
        if(email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide valid email");
            emailEditText.requestFocus();
            return;
        }

        progressBar2.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forget_pass.this, "Check your email to reset password!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(forget_pass.this, "try again,something wrong happened!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}