package fcu.app.unknownfooddelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

  // Components
  TextView tvTitle, tvUsername;
  FirebaseAuth fAuth;
  FirebaseUser user;
  FirebaseFirestore db;
  String userID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 改變上方通知欄
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    // 連接 Components, FirebaseAuth & FirebaseFirestore
    tvTitle = findViewById(R.id.tv_title);
    tvUsername = findViewById(R.id.tv_username);
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    // 當前使用者資訊 ( userID -> database primary key )
    user = fAuth.getCurrentUser();
    userID = user.getUid();


    //                       ┌ document ( userID ) - information
    // collection( users )  -├ document ( userID ) - information
    //                       └ document ( userID ) - information

    // 根據 userID 取得其在 db 中 users collection 的 document info
    DocumentReference documentReference = db.collection("users").document(userID);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          tvUsername.setText(documentSnapshot.getString("username"));
        } else {
          Log.d("TAG", "Error, document do not exists.");
        }
      }
    });

  }

  // Logout & Navigate to LoginActivity
  public void logout(View view) {
   fAuth.signOut();
   startActivity(new Intent(getApplicationContext(), LoginActivity.class));
   finish();
  }
}