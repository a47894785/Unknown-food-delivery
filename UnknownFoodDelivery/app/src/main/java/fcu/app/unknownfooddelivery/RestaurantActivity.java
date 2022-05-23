package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
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
  private String userId;
  private String rName, rEmail, rPhone, rAddress, userEmail;

  int bottomId;
  private NavigationView navigationViewShop;
  private BottomNavigationView bottomNavigationViewShop;

  ShopHomeFragment shopHomeFragment= new ShopHomeFragment();
  ShopAddFragment shopAddFragment = new ShopAddFragment();
  ShopHistoryFragment shopHistoryFragment = new ShopHistoryFragment();
  EditRestaurantFragment editRestaurantFragment = new EditRestaurantFragment();

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
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    DocumentReference userDocumentReference = db.collection("users").document(userId);
    userDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          userEmail = documentSnapshot.getString("email");
        }
      }
    });

    DocumentReference documentReference = db.collection("shops").document(userId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()){
          rName = documentSnapshot.getString("shopName");
          rEmail = documentSnapshot.getString("shopEmail");
          rPhone = documentSnapshot.getString("shopPhone");
          rAddress = documentSnapshot.getString("shopAddress");
          Log.d("GetRestaurantInfo", "Shop Name: " + rName + ", Shop Email: " + rEmail + ", Shop Phone: " + rPhone + ", Shop Address: " + rAddress);
        } else {
          Log.d("GetRestaurantInfo", "Error, document not found.");
        }
      }
    });

    checkRestaurantInfo(editRestaurantFragment);

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
        if (id == R.id.menu_logout_shop) {
          fAuth.signOut();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          finish();
        } else if (id == R.id.menu_profile_shop) {
          dataBundle(editRestaurantFragment);
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, editRestaurantFragment).commit();
          drawerLayoutShop.closeDrawer(GravityCompat.START);
        } else if (id == R.id.general_mode_shop){
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
            checkRestaurantInfo(editRestaurantFragment);
            break;
          case R.id.btn_nav_add_shop:
            bottomId = R.id.btn_nav_add_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopAddFragment).commit();
            checkRestaurantInfo(editRestaurantFragment);
            break;
          case R.id.btn_nav_history_shop:
            bottomId = R.id.btn_nav_history_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHistoryFragment).commit();
            checkRestaurantInfo(editRestaurantFragment);
            break;
        }
        return true;
      }
    });
  }

  public void checkRestaurantInfo(EditRestaurantFragment editRestaurantFragment) {
    dataBundle(editRestaurantFragment);
    Log.d("GetRestaurantInfo", "Shop Name: " + rName + ", Shop Email: " + rEmail + ", Shop Phone: " + rPhone + ", Shop Address: " + rAddress);

    if (rName == null || rEmail == null || rPhone == null || rAddress == null
        || rName == "" || rEmail == "" || rPhone == "" || rAddress == ""){
      AlertDialog.Builder checkDialog = new AlertDialog.Builder(this);
      checkDialog.setMessage("尚有店家資訊未設定，請至店家資訊設定。");
      checkDialog.setTitle("提醒");
      checkDialog.setPositiveButton("前往", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          dataBundle(editRestaurantFragment);
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, editRestaurantFragment).commit();
        }
      });
      checkDialog.setCancelable(false);
      checkDialog.show();
    }
  }

  private void dataBundle(EditRestaurantFragment editRestaurantFragment) {
    DocumentReference documentReference = db.collection("shops").document(userId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          rName = documentSnapshot.getString("shopName");
          rEmail = documentSnapshot.getString("shopEmail");
          rPhone = documentSnapshot.getString("shopPhone");
          rAddress = documentSnapshot.getString("shopAddress");
        }
      }
    });
    Bundle bundle = new Bundle();
    bundle.putString("shopName", rName);
    bundle.putString("shopEmail", rEmail);
    bundle.putString("shopPhone", rPhone);
    bundle.putString("shopAddress", rAddress);
    bundle.putString("userEmail", userEmail);
    editRestaurantFragment.setArguments(bundle);
  }
}