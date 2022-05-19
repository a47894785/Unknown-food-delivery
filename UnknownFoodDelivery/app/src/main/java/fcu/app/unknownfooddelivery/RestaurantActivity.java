package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantActivity extends AppCompatActivity {

  private DrawerLayout drawerLayoutShop;
  private ImageView ivMenuShop;
  private FirebaseAuth fAuth;
  private FirebaseFirestore db;
  int bottomId;
  private NavigationView navigationViewShop;
  private BottomNavigationView bottomNavigationViewShop;
  ShopHomeFragment shopHomeFragment= new ShopHomeFragment();
  ShopAddFragment shopAddFragment = new ShopAddFragment();
  ShopHistoryFragment shopHistoryFragment = new ShopHistoryFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resaurant);


    drawerLayoutShop = findViewById(R.id.drawerLayout_shop);
    ivMenuShop = findViewById(R.id.iv_menu_shop);
    navigationViewShop = findViewById(R.id.navigationView_shop);
    bottomNavigationViewShop = findViewById(R.id.bottom_navigation_shop);
    bottomNavigationViewShop.getMenu().getItem(0).setChecked(false);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
    //跳轉進來就進入到 ShopHomeFragment
    bottomId = R.id.btn_nav_home_shop;
    getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHomeFragment).commit();

    fAuth = FirebaseAuth.getInstance();


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
        if (id == R.id.menu_Logout_shop) {
          fAuth.signOut();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          finish();
        }else if(id == R.id.general_mode_shop){
          startActivity(new Intent(getApplicationContext(), MainActivity_simulate.class));
        }
        return true;
      }
    });

    bottomNavigationViewShop.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.btn_nav_home_shop:
            bottomId = R.id.btn_nav_home_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHomeFragment).commit();
            break;
          case R.id.btn_nav_add_shop:
            bottomId = R.id.btn_nav_add_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopAddFragment).commit();
            break;
          case R.id.btn_nav_history_shop:
            bottomId = R.id.btn_nav_history_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHistoryFragment).commit();
            break;
        }
        return true;
      }
    });
  }
}