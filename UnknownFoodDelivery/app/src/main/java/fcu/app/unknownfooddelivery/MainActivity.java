package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.List;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

  // Components
  TextView tvTitle, tvUsername;
  FirebaseAuth fAuth;
  FirebaseUser user;
  FirebaseFirestore db;
  String userID;

  public static final int DEFAULT_UPDATE_INTERVAL = 30;
  public static final int FAST_UPDATE_INTERVAL = 5;
  public static final int PERMISSION_FINE_LOCATION = 99;

  TextView tv_lat, tv_lon, tv_sensor, tv_update, tv_address;
  Switch sw_locationupdates, sw_gps;

  boolean updateOn = false;
  LocationRequest locationRequest;
  LocationCallback locationCalBack;
  FusedLocationProviderClient fusedLocationProviderClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    tv_lat = findViewById(R.id.tv_lat);
    tv_lon = findViewById(R.id.tv_lon);
    tv_sensor = findViewById(R.id.tv_sensor);
    tv_update = findViewById(R.id.tv_updates);
    tv_address = findViewById(R.id.tv_address);
    sw_gps = findViewById(R.id.sw_gps);
    sw_locationupdates = findViewById(R.id.sw_locationsupdates);

    locationRequest = new LocationRequest();
    locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
    locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);




    // 連接 Components, FirebaseAuth & FirebaseFirestore
    tvTitle = findViewById(R.id.tv_title);
    tvUsername = findViewById(R.id.tv_username);
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    // 當前使用者資訊 ( userID -> database primary key )
    user = fAuth.getCurrentUser();
    userID = user.getUid();


    //                       ┌ document ( userID ) - information
    // collection( users )  -├ document ( userID ) - information
    //                       └ document ( userID ) - information

    // 根據 userID 取得其在 db 中 users collection 的 document info
    DocumentReference documentReference = db.collection("users").document(userID);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          tvUsername.setText(documentSnapshot.getString("username"));
        } else {
          Log.d("TAG", "Error, document do not exists.");
        }
      }
    });

    locationCalBack = new LocationCallback() {

      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        Location location = locationResult.getLastLocation();
        updateUIValues(location);
      }
    };

    sw_gps.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (sw_gps.isChecked()) {
          locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
          tv_sensor.setText("Using GPS sensor");
        } else {
          locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
          tv_sensor.setText("Using Tower + WIFI");
        }
      }
    });
    sw_locationupdates.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (sw_locationupdates.isChecked()) {
          startLocationUpdates();
        } else {
          stopLocationUpdate();
        }
      }
    });
    updateGPS();

  }

  // Logout & Navigate to LoginActivity
  public void logout(View view) {
   fAuth.signOut();
   startActivity(new Intent(getApplicationContext(), LoginActivity.class));
   finish();
  }

  private void startLocationUpdates() {
    tv_update.setText("Location is being tracked");
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCalBack, null);
    updateGPS();
  }
  private void stopLocationUpdate() {

    tv_update.setText("Location is Not being tracked");
    tv_lat.setText("Not tracking location");
    tv_lon.setText("Not tracking location");
    tv_address.setText("Not tracking location");
    tv_sensor.setText("Not tracking location");
    fusedLocationProviderClient.removeLocationUpdates(locationCalBack);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case PERMISSION_FINE_LOCATION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
          updateGPS();
        }
        else {
          Toast.makeText(this,"this app requires permission to be greated is order to work properly",Toast.LENGTH_LONG);
          finish();
        }
        break;
    }
  }

  private void updateGPS() {
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
      fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
          updateUIValues(location);
        }
      });
    }
    else{
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
      }
    }
  }
  private void updateUIValues(Location location){
    tv_lat.setText(String.valueOf(location.getLatitude()));
    tv_lon.setText(String.valueOf(location.getLongitude()));

    Geocoder geocoder = new Geocoder(MainActivity.this);

    try {
      List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
      tv_address.setText(addresses.get(0).getAddressLine(0));
    }
    catch (Exception e){
      tv_address.setText("Unable to get street address");
    }
  }
}