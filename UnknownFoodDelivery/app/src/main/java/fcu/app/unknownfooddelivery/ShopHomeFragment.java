package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShopHomeFragment extends Fragment {

  private String shopStatus;
  private TextView tvStatus;
  private Spinner spinner;
  private Button btnChange;
  private int selected;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private String changeStatus;
  private SharedPreferences sharedPreferences;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_shop_home, container, false);

    // 連接 Components
    tvStatus = view.findViewById(R.id.tv_shop_status);
    spinner = view.findViewById(R.id.sp_status);
    btnChange = view.findViewById(R.id.btn_change_status);

    // 讀 SharedPreferences 並拿資料
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    shopStatus = sharedPreferences.getString("shopStatus", "close");

    // 連接 Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    // 依據店家狀態資料顯示
    switch (shopStatus) {
      case "open":
        tvStatus.setText("營業中");
        tvStatus.setTextColor(Color.GREEN);
        break;
      case "busy":
        tvStatus.setText("忙碌中");
        tvStatus.setTextColor(Color.RED);
        break;
      case "close":
        tvStatus.setText("結束營業");
        tvStatus.setTextColor(Color.GRAY);
        break;
    }

    // 更該狀態
    btnChange.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selected = spinner.getSelectedItemPosition();
        switch (selected) {
          case 1:
            if (shopStatus.equals("open")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "open";
            }
            break;
          case 2:
            if (shopStatus.equals("busy")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "busy";
            }
            break;
          case 3:
            if (shopStatus.equals("close")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "close";
            }
            break;
          default:
            Toast.makeText(getContext(), "錯誤，請選擇欲更改的狀態", Toast.LENGTH_SHORT).show();
            changeStatus = "error";
            break;
        }

        // 更改 Firebase 上的資料並存入 SharedPreferences
        if(changeStatus != "error") {
          Log.d("ChangeStatus", changeStatus);
          Map<String, Object> shopChangeStatus = new HashMap<>();
          shopChangeStatus.put("shopStatus", changeStatus);

          db.collection("shops").document(userId).update(shopChangeStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              getFragmentManager().beginTransaction().detach(ShopHomeFragment.this).commit();
              getFragmentManager().beginTransaction().attach(ShopHomeFragment.this).commit();
              Toast.makeText(getContext(), "更改營業狀態成功", Toast.LENGTH_SHORT).show();
            }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(getContext(), "更改營業狀態失敗", Toast.LENGTH_SHORT).show();

            }
          });
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString("shopStatus", changeStatus);
          editor.commit();
        }
        else{
          Toast.makeText(getContext(), "所選模式與當前相同", Toast.LENGTH_SHORT).show();
        }
      }
    });

    return view;
  }
}