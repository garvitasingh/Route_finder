package com.example.googlemap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_page extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_activity);

        logOut= (Button) findViewById(R.id.logout);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(user_page.this,MainActivity.class));
            }
        });
        user=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();

        final TextView greetingTextView= (TextView) findViewById(R.id.greeting);
        final TextView fullNameTextView= (TextView) findViewById(R.id.fullName);
        final TextView emailTextView= (TextView) findViewById(R.id.emailp);
        final TextView ageTextView= (TextView) findViewById(R.id.agep);
        final TextView professionTextView=(TextView) findViewById(R.id.prof);
        final TextView genderTextView = (TextView) findViewById(R.id.genderp) ;

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue((User.class));

                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;
                    String prof=userProfile.prof;
                    String gender=userProfile.gender;

                    greetingTextView.setText("Welcome," + fullName + "!");
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    ageTextView.setText(age);
                    professionTextView.setText(prof);
                    genderTextView.setText(gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(user_page.this, "Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }
}
