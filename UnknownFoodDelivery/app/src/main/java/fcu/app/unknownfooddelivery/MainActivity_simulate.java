package fcu.app.unknownfooddelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity_simulate extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1001;
    private TextView tvLocationAddr;
    private DrawerLayout drawerLayout;
    private double latitude;
    private double longitude;
    private ImageView ivMenu;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private String userId;
    private String userName, userEmail, userPhone;

    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    int count = 0;
    int bottomId;

    private String currentAddress = "當前位置";
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;



    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    GeneralChatFragment generalChatFragment = new GeneralChatFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    EditProfileFragment editProfileFragment = new EditProfileFragment();

    public static final int PERMISSION_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_simulate);

        // 改變上方通知欄
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("GetUserInfo", "Document Exists.");
                    userName = documentSnapshot.getString("username");
                    userEmail = documentSnapshot.getString("email");
                    userPhone = documentSnapshot.getString("phone");
                    Log.d("GetUserInfo", "UserName: " + userName + ", UserEmail: " + userEmail + ", UserPhone: " + userPhone);
                } else {
                    Log.d("GetUserInfo", "Error, document do not exists.");
                }
            }
        });

        tvLocationAddr = findViewById(R.id.tv_location_addr);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.iv_menu);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 10);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuLogout) {
                    fAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else if (id == R.id.menuProfile) {
                    Log.d("BottomIndex", String.valueOf(bottomId));

                    Bundle bundle = new Bundle();
                    bundle.putString("username", userName);
                    bundle.putString("email", userEmail);
                    bundle.putString("phone", userPhone);
                    editProfileFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, editProfileFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        updateGPS();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btn_nav_home:
                        bottomId = R.id.btn_nav_home;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", currentAddress);
                        homeFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, homeFragment).commit();
                        break;
                    case R.id.btn_nav_cart:
                        bottomId = R.id.btn_nav_cart;
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, cartFragment).commit();
                        break;
                    case R.id.btn_nav_chat:
                        bottomId = R.id.btn_nav_chat;
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, generalChatFragment).commit();
                        break;
                    case R.id.btn_nav_history:
                        bottomId = R.id.btn_nav_history;
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, historyFragment).commit();
                        break;
                }
                return true;
            }
        });


    }

    private void updateUIValues(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(MainActivity_simulate.this);
        try {
            currentAddress = "";
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            currentAddress = addresses.get(0).getAddressLine(0);
            tvLocationAddr.setText(currentAddress);

            Log.d("CurrentAddress", currentAddress + ", No." + ++count);
            // TODO 設 flag 控制更新
            if (count == 1) {
                Bundle bundle = new Bundle();
                bundle.putString("address", currentAddress);
                homeFragment.setArguments(bundle);
                bottomId = R.id.btn_nav_home;
                getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, homeFragment).commit();
                Log.d("CurrentAddress", "Update");
            }


        } catch (Exception e) {
            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity_simulate.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void startLocationUpdate() {
        askLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate();
            } else {
                //
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }
}