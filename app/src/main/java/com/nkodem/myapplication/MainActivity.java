package com.nkodem.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    Uri image_uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt1 = findViewById(R.id.bt);
        Switch pokaz = findViewById(R.id.switch11);
        Button bt2 = findViewById(R.id.lokacja);

        TextView txt2 = findViewById(R.id.txt1);
        FusedLocationProviderClient fs = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        pokaz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(pokaz.isChecked())
                {
                    txt2.setVisibility(View.VISIBLE);
                } else
                {txt2.setVisibility(View.INVISIBLE);}
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        {
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, PERMISSION_CODE);
                        }
                    
                        else {
                            openCamera();
                        }
                    } else {
                        openCamera();

                    }
            }


        });





        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    getlocation();
                } else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }

            public void getlocation() {



                if (ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
                {
                    fs.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            Location location = task.getResult();

                            if (location != null) {
                                Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    txt2.setText("" + addresses.get(0).getLocality()+ " " + addresses.get(0).getCountryName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } else
                            {
                                txt2.setText("cos poslo nie tak");

                            }
                        }
                    });
                }else{txt2.setText("sss");}

            }


        });


        }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        switch (requestCode)
        {
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openCamera();
                }else
                {
                    Toast.makeText(getApplicationContext(), "Nie przyznano uprawnien", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camera, 1001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            ImageView img = findViewById(R.id.obrazek);
            img.setImageURI(image_uri);
        }
    }
}