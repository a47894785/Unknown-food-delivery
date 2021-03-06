package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliverActivity extends AppCompatActivity{

  private FirebaseAuth fAuth;
  private FirebaseFirestore db;
  private DocumentReference documentReference;
  private DrawerLayout drawerLayout;

  private SharedPreferences sharedPreferences;
  private String userId;
  private String deliverStatus;

  private DrawerLayout drawerLayoutShop;
  private ImageView ivMenuShop;
  private NavigationView navigationViewShop;
  MapsFragment mapsFragment = new MapsFragment();
  DeliveryStatusFragment deliveryStatusFragment = new DeliveryStatusFragment();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deliver);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    // 連接 Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();
    sharedPreferences = getPreferences(MODE_PRIVATE);

    // 連接 Components
    drawerLayout = findViewById(R.id.drawerLayout_deliver);
    ivMenuShop = findViewById(R.id.iv_menu);
    drawerLayoutShop = findViewById(R.id.drawerLayout_deliver);
    navigationViewShop = findViewById(R.id.navigationView);

    // 拿取 Firebase 上 delivers 的資訊
    Log.d("deliver_try",userId);
    documentReference = db.collection("delivers").document(userId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        Log.d("deliver_try","try");
        if (documentSnapshot.exists()){
          deliverStatus = documentSnapshot.getString("deliverStatus");
          Log.d("deliver_try",deliverStatus);
          SharedPreferences.Editor deliver_edit = sharedPreferences.edit();
          deliver_edit.putString("deliverstatus", deliverStatus);
          deliver_edit.commit();
        }
        else {
          Log.d("deliver_try","not find");
        }
      }
    });

    // 顯示 google map
    getSupportFragmentManager().beginTransaction().replace(R.id.delivery_container,mapsFragment).commit();

    // 左上圖片點擊展開左邊導覽列
    ivMenuShop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawerLayoutShop.openDrawer(GravityCompat.START);
      }
    });
    navigationViewShop.setItemIconTintList(null);
    // 左邊導覽列監聽器
    navigationViewShop.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout_deliver) {
          fAuth.signOut();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          finish();
        }else if (id == R.id.general_mode_deliver){
          startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else if(id == R.id.shop_mode_deliver){
          startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
        }else if(id == R.id.menu_status_deliver){
          getSupportFragmentManager().beginTransaction().replace(R.id.delivery_container, deliveryStatusFragment).commit();
          drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
      }
    });
  }
}