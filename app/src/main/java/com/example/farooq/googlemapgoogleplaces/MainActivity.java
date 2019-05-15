package com.example.farooq.googlemapgoogleplaces;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServiceOK()){
            init();
        }
    }
    private void init(){
        Intent intent = new Intent(MainActivity.this,MapActivity.class);
        startActivity(intent);
        finish();
    }

    // checking for Google services availability.
    public boolean isServiceOK(){
        Log.d(TAG,"isServicesOk : checking google services version");
        int avilable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (avilable == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map request
            Log.d(TAG,"isServiceOK : google play service is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(avilable)) {
            //an error is ocurred but we can fix it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,avilable,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "we can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
