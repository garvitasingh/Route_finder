package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register,forgetPass;
    private EditText TextEmail, TextPassword;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mAuth= FirebaseAuth.getInstance();
        register=(TextView) findViewById(R.id.lregister);
        register.setOnClickListener(this);

        forgetPass=(TextView) findViewById(R.id.forget);
        forgetPass.setOnClickListener(this);

        signIn=(Button) findViewById(R.id.login_bt);
        signIn.setOnClickListener(this);

        TextEmail=(EditText) findViewById(R.id.email);
        TextEmail.setOnClickListener(this);

        TextPassword=(EditText) findViewById(R.id.passwordl);
        TextPassword.setOnClickListener(this);

        progressBar=(ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.lregister:
                startActivity(new Intent(this,register_user.class));
                break;
            case R.id.login_bt:
                userLogin();
                break;
            case R.id.forget:
                startActivity(new Intent(this,forget_pass.class));
        }

    }
    private void userLogin() {
        String email=TextEmail.getText().toString().trim();
        String password=TextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            TextEmail.setError("Email is required");
            TextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            TextEmail.setError("Enter valid email");
            TextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            TextPassword.setError("Password is required");
            TextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()){
                                //redirect to user profile
                                Intent intent=new Intent(MainActivity.this,chooseLocation.class);
                                startActivity(intent);
                            }
                            else{
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Check your email to verify your account", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"login failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}