package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
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
  private SharedPreferences sharedPreferences;

  private FirebaseAuth fAuth;
  private FirebaseFirestore db;
  private DocumentReference documentReference;
  private String userId;
  private String rName, rEmail, rPhone, rAddress, userEmail, rStatus, rImgUrl;
  boolean flag = false;

  int bottomId;
  private NavigationView navigationViewShop;
  private BottomNavigationView bottomNavigationViewShop;

  ShopHomeFragment shopHomeFragment= new ShopHomeFragment();
  ShopAddFragment shopAddFragment = new ShopAddFragment();
  ShopHistoryFragment shopHistoryFragment = new ShopHistoryFragment();
  EditRestaurantFragment editRestaurantFragment = new EditRestaurantFragment();
  UpdateMenuFragment updateMenuFragment = new UpdateMenuFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resaurant);

    // 連接 Components
    drawerLayoutShop = findViewById(R.id.drawerLayout_shop);
    ivMenuShop = findViewById(R.id.iv_menu_shop);
    navigationViewShop = findViewById(R.id.navigationView_shop);
    bottomNavigationViewShop = findViewById(R.id.bottom_navigation_shop);
    bottomNavigationViewShop.getMenu().getItem(0).setChecked(false);
    sharedPreferences = getPreferences(MODE_PRIVATE);


    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    // 連接 Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    // 抓取使用者 email
    DocumentReference userDocumentReference = db.collection("users").document(userId);
    userDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          userEmail = documentSnapshot.getString("email");
        }
      }
    });

    // 以 userId 抓取店家詳細資訊並存入 SharedPreferences
    documentReference = db.collection("shops").document(userId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()){
          rName = documentSnapshot.getString("shopName");
          rEmail = documentSnapshot.getString("shopEmail");
          rPhone = documentSnapshot.getString("shopPhone");
          rAddress = documentSnapshot.getString("shopAddress");
          rStatus = documentSnapshot.getString("shopStatus");
          rImgUrl = documentSnapshot.getString("shopImage");
          flag = true;


          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString("shopName", rName);
          editor.putString("shopEmail", rEmail);
          editor.putString("shopPhone", rPhone);
          editor.putString("shopAddress", rAddress);
          editor.putString("shopStatus", rStatus);
          editor.putString("shopImage", rImgUrl);
          editor.putString("useremail", userEmail);
          editor.commit();

          checkRestaurantInfo(editRestaurantFragment, flag, rName, rEmail, rPhone, rAddress, rImgUrl);
          //跳轉進來就進入到 ShopHomeFragment
          bottomId = R.id.btn_nav_home_shop;
          Log.d("GetRestaurantInfo", "NO.1 Shop Name: " + rName + ", Shop Email: " + rEmail + ", Shop Phone: " + rPhone + ", Shop Address: " + rAddress + ", ShopStatus: " + rStatus);
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHomeFragment).commit();
        } else {
          Log.d("GetRestaurantInfo", "Error, document not found.");
        }
      }
    });

    // 左上圖片點擊展開左邊導覽列
    ivMenuShop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawerLayoutShop.openDrawer(GravityCompat.START);
      }
    });

    // 左方導覽列監聽器
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
//          dataBundle(editRestaurantFragment, flag);
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, editRestaurantFragment).commit();
          drawerLayoutShop.closeDrawer(GravityCompat.START);
        } else if (id == R.id.general_mode_shop){
          startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else if(id == R.id.delivery_man_mode_shop){
          startActivity(new Intent(getApplicationContext(), DeliverActivity.class));
        } else if (id == R.id.menu_change_shop) {
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, updateMenuFragment).commit();
          drawerLayoutShop.closeDrawer(GravityCompat.START);
        }
        return true;
      }
    });

    // 下方導覽列監聽器
    bottomNavigationViewShop.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
          case R.id.btn_nav_home_shop:
            bottomId = R.id.btn_nav_home_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHomeFragment).commit();
            updateInfo(userId);
            break;
          case R.id.btn_nav_add_shop:
            bottomId = R.id.btn_nav_add_shop;
            updateInfo(userId);
            bundle.putString("shopName", rName);
            shopAddFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopAddFragment).commit();
            break;
          case R.id.btn_nav_history_shop:
            bottomId = R.id.btn_nav_history_shop;
            getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, shopHistoryFragment).commit();
            updateInfo(userId);
            break;
        }
        return true;
      }
    });



  }

  // 檢查店家資訊有無空白 有則強制跳轉設定資訊頁面
  public void checkRestaurantInfo(EditRestaurantFragment editRestaurantFragment, boolean flag, String rName, String rEmail, String rPhone, String rAddress, String rImgUrl) {

    if (rName == null || rEmail == null || rPhone == null || rAddress == null || rImgUrl == null
        || rName == "" || rEmail == "" || rPhone == "" || rAddress == "" || rImgUrl == ""){
      AlertDialog.Builder checkDialog = new AlertDialog.Builder(this);
      checkDialog.setMessage("尚有店家資訊未設定，請至店家資訊設定。");
      checkDialog.setTitle("提醒");
      checkDialog.setPositiveButton("前往", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container_shop, editRestaurantFragment).commit();
        }
      });
      checkDialog.setCancelable(false);
      checkDialog.show();
    }
  }

  // 更新店家資訊並存入 SharedPreferences
  private void updateInfo(String userId) {
    DocumentReference documentReference = db.collection("shops").document(userId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          rName = documentSnapshot.getString("shopName");
          rEmail = documentSnapshot.getString("shopEmail");
          rPhone = documentSnapshot.getString("shopPhone");
          rAddress = documentSnapshot.getString("shopAddress");
          rStatus = documentSnapshot.getString("shopStatus");
          rImgUrl = documentSnapshot.getString("shopImage");

          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString("shopName", rName);
          editor.putString("shopEmail", rEmail);
          editor.putString("shopPhone", rPhone);
          editor.putString("shopAddress", rAddress);
          editor.putString("shopStatus", rStatus);
          editor.putString("shopImage", rImgUrl);
          editor.commit();

          checkRestaurantInfo(editRestaurantFragment, flag, rName, rEmail, rPhone, rAddress, rImgUrl);
          Log.d("GetRestaurantInfo", "NO.3 Shop Name: " + rName + ", Shop Email: " + rEmail + ", Shop Phone: " + rPhone + ", Shop Address: " + rAddress);
        }
      }
    });
  }
}