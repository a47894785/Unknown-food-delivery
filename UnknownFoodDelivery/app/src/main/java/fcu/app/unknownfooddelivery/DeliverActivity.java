package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliverActivity extends AppCompatActivity{

  private FirebaseAuth fAuth;
  private FirebaseFirestore db;
  private DocumentReference documentReference;

  private DrawerLayout drawerLayoutShop;
  private ImageView ivMenuShop;
  private NavigationView navigationViewShop;
  MapsFragment mapsFragment = new MapsFragment();
  DeliveryStatusFragment deliveryStatusFragment = new DeliveryStatusFragment();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deliver);
    /*SupportMapFragment
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);*/


    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();


    ivMenuShop = findViewById(R.id.iv_menu);
    drawerLayoutShop = findViewById(R.id.drawerLayout_deliver);
    navigationViewShop = findViewById(R.id.navigationView);
    getSupportFragmentManager().beginTransaction().replace(R.id.delivery_container,mapsFragment).commit();
    ivMenuShop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawerLayoutShop.openDrawer(GravityCompat.START);
      }
    });
    navigationViewShop.setItemIconTintList(null);
    navigationViewShop.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout_deliver) {
          fAuth.signOut();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          finish();
        } else if (id == R.id.general_mode_deliver){
          startActivity(new Intent(getApplicationContext(), MainActivity_simulate.class));
        }else if(id == R.id.shop_mode_deliver){
          startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
        }else if(id == R.id.menu_status_deliver){
          getSupportFragmentManager().beginTransaction().replace(R.id.delivery_container, deliveryStatusFragment).commit();
        }
        return true;
      }
    });
  }
  /*@Override
  public void onMapReady(GoogleMap googleMap) {
    googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(24.1798, 120.6467))
            .title("Marker"));
  }*/
}