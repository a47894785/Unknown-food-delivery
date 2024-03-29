package fcu.app.unknownfooddelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1001;
    private TextView tvLocationAddr;
    private EditText etSearch;
    private DrawerLayout drawerLayout;
    private double latitude;
    private double longitude;
    private ImageView ivMenu;
    private ImageView imBackArrow;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private String userId;
    private String userName, userEmail, userPhone;
    private SharedPreferences sharedPreferences;

    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    int count = 0;
    int bottomId;
    Boolean updateGPS = false;

    static final String DB_NAME = "unknown";
    static final String TB_NAME = "cart";
    SQLiteDatabase sqlDb;
    String createTable;

    private String currentAddress = "當前位置";
    private String lastAddress;
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
        setContentView(R.layout.activity_main);

        // 改變上方通知欄
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Firebase Authentication & Firebase Firestore
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // 連接 Components
        tvLocationAddr = findViewById(R.id.tv_location_addr);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.iv_menu);
        imBackArrow = findViewById(R.id.im_back_arrow);
        etSearch = findViewById(R.id.et_search);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 10);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // SQLite Database
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
            "(userId VARCHAR(32), " +
            "shopName VARCHAR(16), " +
            "mealName VARCHAR(16), " +
            "mealPrice VARCHAR(8), " +
            "mealNum VARCHAR(8), " +
            "mealImg VACHAR(64))";

        sqlDb.execSQL(createTable);
        sqlDb.close();


        // 到 Firebase 拿 一般使用者( users ) 的資料 & 同時將資料寫進 SharedPreferences
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("GetUserInfo", "Document Exists.");
                    userName = documentSnapshot.getString("username");
                    userEmail = documentSnapshot.getString("email");
                    userPhone = documentSnapshot.getString("phone");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", userName);
                    editor.putString("useremail", userEmail);
                    editor.putString("userphone", userPhone);
                    editor.commit();

                    Log.d("GetUserInfo", "UserName: " + userName + ", UserEmail: " + userEmail + ", UserPhone: " + userPhone);
                } else {
                    Log.d("GetUserInfo", "Error, document do not exists.");
                }
            }
        });

        // 左上圖片 點擊可以展開左邊導覽列
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 左邊導覽列的監聽器
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
                    etSearch.setVisibility(View.GONE);
                    Log.d("BottomIndex", String.valueOf(bottomId));
                    getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, editProfileFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.shop_mode){
                    startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
                } else if (id == R.id.delivery_man_mode) {
                    startActivity(new Intent(getApplicationContext(), DeliverActivity.class));
                }
                return true;
            }
        });

        // 拿取 GPS 資訊相關
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        // 更新 GPS
        updateGPS();

        // 下方導覽列監聽器
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DocumentReference documentReference = db.collection("users").document(userId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d("GetUserInfo", "Document Exists.");
                            userName = documentSnapshot.getString("username");
                            userEmail = documentSnapshot.getString("email");
                            userPhone = documentSnapshot.getString("phone");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", userName);
                            editor.putString("useremail", userEmail);
                            editor.putString("userphone", userPhone);
                            editor.commit();

                            Log.d("GetUserInfo", "UserName: " + userName + ", UserEmail: " + userEmail + ", UserPhone: " + userPhone);
                        } else {
                            Log.d("GetUserInfo", "Error, document do not exists.");
                        }
                    }
                });

                switch (item.getItemId()) {
                    case R.id.btn_nav_home:
                        bottomId = R.id.btn_nav_home;
                        etSearch.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, homeFragment).commit();
                        break;
                    case R.id.btn_nav_cart:
                        bottomId = R.id.btn_nav_cart;
                        etSearch.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, cartFragment).commit();
                        break;
                    case R.id.btn_nav_chat:
                        bottomId = R.id.btn_nav_chat;
                        etSearch.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, generalChatFragment).commit();
                        break;
                    case R.id.btn_nav_history:
                        bottomId = R.id.btn_nav_history;
                        etSearch.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, historyFragment).commit();
                        break;
                }
                return true;
            }
        });


    }

    // 更新 UI GPS 資訊
    private void updateUIValues(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            lastAddress = currentAddress;
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            currentAddress = addresses.get(0).getAddressLine(0).substring(3);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Address", currentAddress);
            editor.commit();
            tvLocationAddr.setText(currentAddress);

            Log.d("CurrentAddress", currentAddress + ", No." + ++count);
            // 比對 currentAddress 和 lastAddress ，若不一樣表示定址更動 則更新 SharedPreferences 資料
            if (!currentAddress.equals(lastAddress) && count != 0) {
                editor.putString("Address", currentAddress);
                editor.commit();
                Log.d("CurrentAddress_1", currentAddress + ", No." + count);
                getSupportFragmentManager().beginTransaction().detach(homeFragment).commit();
                getSupportFragmentManager().beginTransaction().attach(homeFragment).commit();
            }

            // 設 flag 控制更新
            if (count == 1) {
                getSupportFragmentManager().beginTransaction().replace(R.id.btn_nav_container, homeFragment).commit();
                count++;

            }
        } catch (Exception e) {
            Toast.makeText(this, "無法取得位置", Toast.LENGTH_SHORT).show();
        }
    }

    // 更新 GPS
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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

    // 那裝置不斷更新 GPS
    private void startLocationUpdate() {
        askLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

    }

    // 停止裝置更新 GPS
    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    // 位置權限確認
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "需要權限", Toast.LENGTH_SHORT).show();
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

    // 更新 SQL 資料庫資訊
    protected void updateSql(String userId, String shopName, String mealName, String mealPrice, String mealNum, String mealImg){
        Log.d("ReturnValues", userId + "/ " + shopName + "/ " + mealName + "/ "  + mealPrice + "/ " + mealNum + "/ " + mealImg);
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sqlDb.execSQL(createTable);
        Cursor cursortemp = sqlDb.rawQuery("SELECT * FROM " + TB_NAME  + " WHERE shopName='" +shopName +"'"+ " AND " + "mealName='" +mealName + "'",null);
        int tempnum = 0;
        if (cursortemp.moveToFirst()){
            do{
                tempnum = Integer.valueOf(cursortemp.getString(4));
            } while(cursortemp.moveToNext());
        } else if (cursortemp.getCount() == 0) {
            tempnum = 0;
        }
        if(tempnum != 0){
            tempnum += Integer.valueOf(mealNum);
            String newnum = Integer.toString(tempnum);
            String updateData = "UPDATE " + TB_NAME + " SET mealNum ='" + newnum + "'"+" WHERE shopName='" +shopName +"'"+ " AND " + "mealName='" +mealName + "'" ;
            sqlDb.execSQL(updateData);
        }else {
            Cursor cursor = sqlDb.rawQuery("SELECT * FROM " + TB_NAME, null);
            ContentValues contentValues = new ContentValues(6);
            contentValues.put("userId", userId);
            contentValues.put("shopName", shopName);
            contentValues.put("mealName", mealName);
            contentValues.put("mealPrice", mealPrice);
            contentValues.put("mealNum", mealNum);
            contentValues.put("mealImg", mealImg);

            sqlDb.insert(TB_NAME, null, contentValues);
        }
        sqlDb.close();
    }

    // 抓取 SQLite 資料並將其顯示在 Logcat
    protected void getSqlData() {
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sqlDb.execSQL(createTable);

        Cursor cursor = sqlDb.rawQuery("SELECT * FROM " + TB_NAME, null);
        String str = "";
        if (cursor.moveToFirst()){
            str = "總共有 " + cursor.getCount() + "筆資料\n";
            str += "-----------------------------\n";

            do{
                str += "userId:" + cursor.getString(0) + "\n";
                str += "shopName:" + cursor.getString(1) + "\n";
                str += "mealName:" + cursor.getString(2) + "\n";
                str += "mealPrice:" + cursor.getString(3) + "\n";
                str += "mealNum:" + cursor.getString(4) + "\n";
                str += "mealImg:" + cursor.getString(5) + "\n";
                str += "-----------------------------\n";
            } while(cursor.moveToNext());
        } else if (cursor.getCount() == 0) {
            str = "總共有 0 筆資料";
        }
        Log.d("CheckSqlData", str);
        sqlDb.close();
    }

    // 回傳 SQLite 購物車的資料
    protected ArrayList<String> getSQLdata_to_cart() {
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sqlDb.execSQL(createTable);
        Cursor cursor = sqlDb.rawQuery("SELECT * FROM " + TB_NAME, null);
        ArrayList<String> arrayList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                arrayList.add(cursor.getString(1));
                arrayList.add(cursor.getString(2));
                arrayList.add(cursor.getString(3));
                arrayList.add(cursor.getString(4));
            } while(cursor.moveToNext());
        } else if (cursor.getCount() == 0) {
        }
        sqlDb.close();
        return arrayList;
    }

    // 用以清除 SQL 的資料 ( 清空購物車 )
    protected void delSqlData() {
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        String deleteData = "DELETE FROM " + TB_NAME;
        sqlDb.execSQL(deleteData);
        sqlDb.close();
    }

    // 刪除購物車單項餐點
    protected void delSqlDate_cart(String shopName,String mealName){
        sqlDb = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        String deleteData = "DELETE FROM " + TB_NAME  + " WHERE " + "shopName" + "='" +shopName +"'" + " AND " + "mealName"+ "='" +mealName +"'";
        sqlDb.execSQL(deleteData);
        sqlDb.close();
    }

    // 進入 MainActivity 時開始更新 GPS
    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdate();
    }

    // 關閉則停止更新 GPS
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }
}