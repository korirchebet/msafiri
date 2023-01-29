package com.elitechinc.my;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.elitechinc.my.Classes.Hotspots;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

public class postScene extends AppCompatActivity {
    Button finish;
    TextView txtlocation, txtextra, txtlocation2, start, close;
    int ADDRESS_PICKER_REQUEST = 101;
    TimePicker timepicker;
    FirebaseAuth mAuth;
    EditText namebro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scene);
        finish = findViewById(R.id.finish);
        txtlocation = findViewById(R.id.txtlocation);
        txtextra = findViewById(R.id.txtextra);
        txtlocation2 = findViewById(R.id.txtlocation2);
        namebro = findViewById(R.id.nameBro);
        MapUtility.apiKey = getResources().getString(R.string.google_maps_api);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitudeme = Double.parseDouble(txtlocation.getText().toString());
                double longitudeme = Double.parseDouble(txtlocation2.getText().toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotspots");
                Hotspots hotspots = new Hotspots(latitudeme, longitudeme, namebro.getText().toString());
                reference.push().setValue(hotspots);
                finish();

            }
        });
    }

    public void locationpick(View view) {
        Intent i = new Intent(postScene.this, LocationPickerActivity.class);
        startActivityForResult(i, ADDRESS_PICKER_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    // String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Bundle completeAddress = data.getBundleExtra("fullAddress");
                    txtextra.setText(new StringBuilder().append("addressline2: ").append
                            (completeAddress.getString("addressline2")).append("\ncity: ").append
                            (completeAddress.getString("city")).append("\npostalcode: ").append
                            (completeAddress.getString("postalcode")).append("\nstate: ").append
                            (completeAddress.getString("state")).toString());
                    txtlocation.setText(new StringBuilder().append(currentLatitude).toString());
                    txtlocation2.setText(new StringBuilder().append(currentLongitude).toString());

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}