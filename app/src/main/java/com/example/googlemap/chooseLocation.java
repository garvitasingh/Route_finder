package com.example.googlemap;

import static com.example.googlemap.initial_location.LOC;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class chooseLocation extends AppCompatActivity implements View.OnClickListener {
    private Button LocationFrom,LocationTo,frmHistory;
    private Button GetDirection;
    private static final String TAG="chooseLocation";
    //to get value returned by initial_location
    ActivityResultLauncher<Intent> take_initial_location=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==78){
                Intent intent =result.getData();
                if(intent!= null){
                    String location_pickUp=intent.getStringExtra("result");
                    LocationFrom.setText(location_pickUp);
                }
            }
        }
    }
        );

    ActivityResultLauncher<Intent> take_final_location=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==68){
                        Intent intent =result.getData();
                        if(intent!= null){
                            String location_destination=intent.getStringExtra("result_loc");
                            LocationTo.setText(location_destination);
                        }
                    }
                }
            }
    );


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_location);

        LocationFrom=(Button) findViewById(R.id.pick_up);
        LocationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(chooseLocation.this,initial_location.class);
                take_initial_location.launch(intent);
            }
        });

        LocationTo=(Button) findViewById(R.id.destination);
        LocationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(chooseLocation.this,final_location.class);
                take_final_location.launch(intent);
            }
        });

        GetDirection=(Button) findViewById(R.id.getdirc);
        GetDirection.setOnClickListener(this);

//        String locationt=getIntent().getStringExtra(initial_location.LOC);
//        LocationFrom.setText(locationt);
//
//        String locationf=getIntent().getStringExtra(final_location.LOCN);
//        LocationTo.setText(locationf);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pick_up:
                startActivity(new Intent(this,initial_location.class));
                break;
            case R.id.destination:
                startActivity(new Intent(this, final_location.class));
                break;
            case R.id.getdirc:
                getdirection();
        }
    }
    public void getdirection(){
        String pickUp_location=LocationFrom.getText().toString().trim();
        String destination_location=LocationTo.getText().toString().trim();
        Bundle bundle=new Bundle();
        bundle.putString("pickUp",pickUp_location);
        bundle.putString("destination",destination_location);
        Intent intent =new Intent(chooseLocation.this,route.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
