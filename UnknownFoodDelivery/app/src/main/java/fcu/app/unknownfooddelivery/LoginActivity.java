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

public class LoginActivity extends AppCompatActivity {

  // Components
  EditText etEmail, etPassword;
  Button btn_login;
  TextView tvCreate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    // 連接 Components
    etEmail = findViewById(R.id.et_login_email);
    etPassword = findViewById(R.id.et_login_password);
    btn_login = findViewById(R.id.btn_login);
    tvCreate = findViewById(R.id.tv_create);

    // 為最下方的 Sign Up TextView 設定 OnClickListener; 按下後 Navigate to RegisterActivity
    tvCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
      }
    });

  }
}