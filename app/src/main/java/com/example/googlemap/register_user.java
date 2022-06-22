package com.example.googlemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class register_user extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    String prof,gender;
    private TextView banner,registerUser;
    private EditText FullName,TextAge,TextMail,TextPassword,TextConfirmPassword,TextAadharNo,
            TextCarPlateNo, TextDLNo;
    private ProgressBar progressBar;
//    private RadioGroup Radio_group_prof, Radio_group_gender;
    private RadioButton Radio_passenger,Radio_driver,Radio_female,Radio_male,Radio_otherGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        banner=(TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=(Button) findViewById(R.id.registerBt);
        registerUser.setOnClickListener(this);

        FullName=(EditText) findViewById(R.id.Fullname);
        TextAge=(EditText) findViewById(R.id.age);
        TextMail=(EditText) findViewById(R.id.Email);
        TextPassword=(EditText) findViewById(R.id.password);
        TextConfirmPassword=(EditText) findViewById(R.id.confirmPassword);
//        TextAadharNo=(EditText)findViewById(R.id.aadharNo);
//        TextCarPlateNo=(EditText)findViewById(R.id.carplateNo);
//        TextDLNo=(EditText)findViewById(R.id.dlNo);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
//    //    Radio_group_prof  = (RadioGroup) findViewById(R.id.radioGroupProf);
//     //   Radio_group_gender  = (RadioGroup) findViewById(R.id.radioGroupGender);
//        Radio_driver=(RadioButton) findViewById(R.id.radioDriver);
//        Radio_passenger=(RadioButton) findViewById(R.id.radioPassenger);
        Radio_female=(RadioButton) findViewById(R.id.femaleButton);
        Radio_male=(RadioButton) findViewById(R.id.maleButton);
        Radio_otherGender=(RadioButton) findViewById(R.id.otherGenderButton);
    }
//    public void onProfRadioBtClick(View view){
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radioDriver:
//                if (checked)
//                    prof=Radio_driver.getText().toString().trim();
//                    break;
//            case R.id.radioPassenger:
//                if (checked)
//                    prof=Radio_passenger.getText().toString().trim();
//                    break;
//        }
//    }

    public void onGenderRadioBtClick(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.femaleButton:
                if (checked)
                    gender=Radio_female.getText().toString().trim();
                break;
            case R.id.maleButton:
                if (checked)
                    gender=Radio_male.getText().toString().trim();
                break;
            case R.id.otherGenderButton:
                if(checked)
                    gender=Radio_otherGender.getText().toString().trim();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        case R.id.banner:
            startActivity(new Intent(this,MainActivity.class));
            break;
        case R.id.registerBt:
            Toast.makeText(register_user.this,"clicked",Toast.LENGTH_LONG).show();
            registerUser();
            break;
        }
}

    private void registerUser() {
        String fullName = FullName.getText().toString().trim();
        String email = TextMail.getText().toString().trim();
        String gendr=gender;
        String age = TextAge.getText().toString().trim();
//        String profs=prof;
        String password = TextPassword.getText().toString().trim();
        String confirmPassword=TextConfirmPassword.getText().toString();
//        String aadharno=TextAadharNo.getText().toString().trim();
//        String carplateno=TextCarPlateNo.getText().toString().trim();
//        String dlno = TextDLNo.getText().toString().trim();

        if (fullName.isEmpty()) {
            FullName.setError("Full name is required");
            FullName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            TextMail.setError("Email is required");
            TextMail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            TextMail.setError("Please provide valid email");
            TextMail.requestFocus();
            return;
        }
        if(!Radio_female.isChecked() && !Radio_male.isChecked() && !Radio_otherGender.isChecked()){
            Radio_otherGender.setError("Select Gender");
            Radio_otherGender.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            TextAge.setError("Age is required");
            TextAge.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            TextPassword.setError("Password is required");
            TextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            TextPassword.setError("Password is short, minimum 6 characters are required");
            TextPassword.requestFocus();
            return;
        }
        if (!confirmPassword.contentEquals(password)) {
            TextConfirmPassword.setError("Confirm Password does not match");
            TextConfirmPassword.requestFocus();
            return;
        }
//        if (!Radio_driver.isChecked() && !Radio_passenger.isChecked()) {
//            Radio_driver.setError("Select any one");//Set error to last Radio button
//            Radio_driver.requestFocus();
//            return;
//        }
////        if(Radio_passenger.isChecked()){
////            TextCarPlateNo=null;
////            TextDLNo=null;
////        }
//        if(aadharno.isEmpty() && Radio_passenger.isChecked()){
//            TextAadharNo.setError("Enter your aadhar number");
//            TextAadharNo.requestFocus();
//            return;
//        }
//        if(carplateno.isEmpty() && Radio_driver.isChecked()){
//            TextCarPlateNo.setError("Enter Vehicle Number");
//            TextCarPlateNo.requestFocus();
//            return;
//        }
//        if(dlno.isEmpty() && Radio_driver.isChecked()){
//            TextDLNo.setError("Enter driving license number");
//            TextDLNo.requestFocus();
//            return;
//        }
        progressBar.setVisibility(View.VISIBLE);
        registerUser.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, age, email,gendr);
//                            User user = new User(fullName, age, email,profs,gendr,aadharno,carplateno,dlno);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(register_user.this, "User has been registered successfully", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(register_user.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(register_user.this,MainActivity.class));
                                }
                            });
                        } else {
                            Toast.makeText(register_user.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
