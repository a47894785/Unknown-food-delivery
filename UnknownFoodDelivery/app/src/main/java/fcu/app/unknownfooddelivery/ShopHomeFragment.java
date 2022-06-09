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

    tvStatus = view.findViewById(R.id.tv_shop_status);
    spinner = view.findViewById(R.id.sp_status);
    btnChange = view.findViewById(R.id.btn_change_status);
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

//    if (getArguments() != null) {
//      shopStatus = this.getArguments().getString("shopStatus");
//      Log.d("Status", shopStatus);
//    }

    shopStatus = sharedPreferences.getString("shopStatus", "close");

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

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
//            tvStatus.setText("營業中");
            break;
          case 2:
            if (shopStatus.equals("busy")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "busy";
            }
//            tvStatus.setText("忙碌中");
            break;
          case 3:
            if (shopStatus.equals("close")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "close";
            }
//            tvStatus.setText("結束營業");
            break;
          default:
            Toast.makeText(getContext(), "錯誤，請選擇欲更改的狀態", Toast.LENGTH_SHORT).show();
            changeStatus = "error";
//            tvStatus.setText("請設定營業狀態");
            break;
        }
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