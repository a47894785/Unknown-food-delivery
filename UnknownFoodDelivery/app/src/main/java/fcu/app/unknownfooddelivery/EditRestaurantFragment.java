package fcu.app.unknownfooddelivery;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditRestaurantFragment extends Fragment {

  private String[] restaurantProfileTitle = {"店家名稱", "店家地址", "電話", "電子郵件"};
  String rName, rEmail, rPhone, rAddress, userEmail;
  String updateData;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_restaurant, container, false);

    if (getArguments() != null) {
      userEmail = this.getArguments().getString("userEmail");
      rName = this.getArguments().getString("shopName").equals("") ? "尚未設定店家名稱" : this.getArguments().getString("shopName");
      rEmail = this.getArguments().getString("shopEmail").equals("") ? "尚未設定電子郵件" : this.getArguments().getString("shopEmail");
      rPhone = this.getArguments().getString("shopPhone").equals("") ? "尚未設定店家電話" : this.getArguments().getString("shopPhone");
      rAddress = this.getArguments().getString("shopAddress").equals("") ? "尚未設定店家地址" : this.getArguments().getString("shopAddress");
    }

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    ListView lv = view.findViewById(R.id.lv_edit_shop_prodile);
    ArrayList<RestaurantProfile> restaurantProfileList = new ArrayList<>();

    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[0], rName));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[1], rAddress));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[2], rPhone));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[3], rEmail));

    RestaurantProfileAdapter adapter = new RestaurantProfileAdapter(getContext(), R.layout.layout_edit_profile, restaurantProfileList);
    lv.setAdapter(adapter);

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());
        editDialog.setTitle(restaurantProfileTitle[index]);
        EditText updateDataInput = new EditText(getContext());
        if (index == 0) {
          updateDataInput.setText(rName);
        } else if (index == 1) {
          updateDataInput.setText(rAddress);
        } else if (index == 2) {
//          updateDataInput.setText(rPhone);
          updateDataInput.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (index == 3) {
          updateDataInput.setText(rEmail);
        }

        editDialog.setView(updateDataInput);
        editDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String inputText = updateDataInput.getText().toString();
            if (inputText.length() <= 0) {
              Toast.makeText(getContext(), "輸入錯誤!輸入不能為空", Toast.LENGTH_SHORT).show();
            } else if (index == 2 && inputText.length() != 10) {
              Toast.makeText(getContext(), "輸入錯誤!電話號碼必須為十個數字!", Toast.LENGTH_SHORT).show();
            } else {
              updateRestaurantInfo(index, userEmail, inputText);
            }
          }
        });

        editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });
        editDialog.show();
      }
    });

    return view;
  }

  private void updateRestaurantInfo(int index, String userEmail, String newData) {
    Map<String, Object> rProfile = new HashMap<>();

    switch (index) {
      case 0:
        rProfile.put("shopName", newData);
        break;
      case 1:
        rProfile.put("shopAddress", newData);
        break;
      case 2:
        rProfile.put("shopPhone", newData);
        break;
      case 3:
        rProfile.put("shopEmail", newData);
        break;
    }

    db.collection("users").whereEqualTo("email", userEmail)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful() && !task.getResult().isEmpty()) {
          db.collection("shops").document(userId)
              .update(rProfile)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                }
              }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(getContext(), "發生錯誤", Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          Toast.makeText(getContext(), "更新失敗", Toast.LENGTH_SHORT).show();
        }
      }
    });

  }

}