package fcu.app.unknownfooddelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

  // Components
  EditText etName, etEmail, etPhone, etPassword, etConfirm;
  Button btnRegister;
  TextView tvGoToLogin;
  String userID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    // 連接 Components
    etName = findViewById(R.id.et_name);
    etEmail = findViewById(R.id.et_mail);
    etPhone = findViewById(R.id.et_phone);
    etPassword = findViewById(R.id.et_password1);
    etConfirm = findViewById(R.id.et_password2);
    btnRegister = findViewById(R.id.btn_register);
    tvGoToLogin = findViewById(R.id.tv_gotologin);

    // 為最下方的 Login TextView 設定 OnClickListener; 按下後 Navigate to LoginActivity
    tvGoToLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      }
    });

  }
}