package fcu.app.unknownfooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

  // Components
  EditText etEmail, etPassword;
  Button btn_login;
  TextView tvCreate;
  FirebaseAuth fAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    // 連接 Components
    etEmail = findViewById(R.id.et_login_email);
    etPassword = findViewById(R.id.et_login_password);
    btn_login = findViewById(R.id.btn_login);
    tvCreate = findViewById(R.id.tv_create);

    // 連接 FirebaseAuth
    fAuth = FirebaseAuth.getInstance();

    // 若已經登入，則直接 Navigate to MainActivity
    if (fAuth.getCurrentUser() != null) {
      startActivity(new Intent(getApplicationContext(), MainActivity_simulate.class));
      finish();
    }

    // 設置 Login Button 的 OnClickListener
    btn_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
          etEmail.setError("Email不可為空");
          return;
        }

        if (TextUtils.isEmpty(password)) {
          etPassword.setError("密碼不可為空");
          return;
        }

        if (password.length() < 6) {
          etPassword.setError("密碼必須>=6個字元");
          return;
        }



        // Authenticate the user
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Toast.makeText(LoginActivity.this, "成功登入", android.widget.Toast.LENGTH_SHORT).show();
              startActivity(new Intent(getApplicationContext(), MainActivity_simulate.class));
            } else {
              Toast.makeText(LoginActivity.this, "錯誤", Toast.LENGTH_SHORT).show();
            }
          }
        });

      }
    });

    // 為最下方的 Sign Up TextView 設定 OnClickListener; 按下後 Navigate to RegisterActivity
    tvCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
      }
    });

  }
}