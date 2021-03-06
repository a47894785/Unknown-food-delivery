package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

  // Components
  EditText etName, etEmail, etPhone, etPassword, etConfirm;
  Button btnRegister;
  TextView tvGoToLogin;
  FirebaseAuth fAuth;
  FirebaseFirestore db;
  String userID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    // 連接 Components
    etName = findViewById(R.id.et_name);
    etEmail = findViewById(R.id.et_mail);
    etPhone = findViewById(R.id.et_phone);
    etPassword = findViewById(R.id.et_password1);
    etConfirm = findViewById(R.id.et_password2);
    btnRegister = findViewById(R.id.btn_register);
    tvGoToLogin = findViewById(R.id.tv_gotologin);

    // 連接 FirebaseAuth & FirebaseFirestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    // 若最近有登入過 則直接 Navigate to MainActivity
    if (fAuth.getCurrentUser() != null) {
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
      finish();
    }

    // 設定　Register Button 的 OnClickListener
    btnRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        // 設定輸入限制

        if (TextUtils.isEmpty(name)) {
          etName.setError("使用者名稱不能為空");
          return;
        }

        if (TextUtils.isEmpty(email)) {
          etEmail.setError("Email不能為空");
          return;
        }

        if (TextUtils.isEmpty(phone)){
          etPhone.setError("電話不能為空");
          return;
        }

        if (phone.length() < 10) {
          etPhone.setError("輸入錯誤!電話號碼必須為十個數字!");
          return;
        }

        if (TextUtils.isEmpty(password)) {
          etPassword.setError("密碼不能為空");
          return;
        }

        if (password.length() < 6) {
          etPassword.setError("密碼必須>=6個字元");
          return;
        }

        if (!password.equals(confirm)) {
          etConfirm.setError("確認密碼不相符!");
          return;
        }

        // Register the user in Firebase
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) { // 註冊成功
              Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();

              // get userID and access db
              userID = fAuth.getCurrentUser().getUid();
              DocumentReference documentReference = db.collection("users").document(userID);

              // 將 username, phone, email 資料根據 userID 存到 db
              Map<String, Object> user = new HashMap<String, Object>();
              user.put("username", name);
              user.put("phone", phone);
              user.put("email", email);
              documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  Log.d("CreateUser", "OnSuccess: user Profile is created for " + userID);
                }
              }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.w("CreateUser", "OnFailure: " + e);
                }
              });

              initShopInfo(userID);
              initDeliverInfo(userID);

              fAuth.signOut(); // 避免一註冊完就跳到 MainActivity
              startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            } else { // 註冊失敗
              Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });

      }
    });

    // 為最下方的 Login TextView 設定 OnClickListener; 按下後 Navigate to LoginActivity
    tvGoToLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      }
    });

  }

  // 初始化 Firebase 上 shops 的資料
  private void initShopInfo(String id){
    DocumentReference documentReference = db.collection("shops").document(id);

    // init db data
    Map<String, Object> shop = new HashMap<>();
    shop.put("shopName", "");
    shop.put("shopAddress", "");
    shop.put("shopPhone", "");
    shop.put("shopEmail", "");
    shop.put("shopStatus", "close");
    shop.put("shopImage", "");

    documentReference.set(shop).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void unused) {
        Log.d("CreateShopInfo", "OnSuccess: Shop information is initialized for " + id);
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d("CreateShopInfo", "OnFailure: " + e);
      }
    });
  }

  // 初始化 Firebase 上 delivers 的資料
  private void initDeliverInfo(String id){
    DocumentReference documentReference = db.collection("delivers").document(id);

    // init db data
    Map<String, Object> deliver = new HashMap<>();
    deliver.put("deliverStatus", "close");


    documentReference.set(deliver).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void unused) {
        Log.d("CreateDeliverInfo", "OnSuccess: Deliver information is initialized for " + id);
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d("CreateDeliverInfo", "OnFailure: " + e);
      }
    });
  }
}