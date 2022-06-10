package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import fcu.app.unknownfooddelivery.adapter.UserProfileAdapter;
import fcu.app.unknownfooddelivery.item.UserProfile;

public class EditProfileFragment extends Fragment {

  private String[] userProfileTitle = {"使用者名稱", "電話號碼"};
  String userName, userEmail, userPhone;
  String updateData;
  String profileType = "";
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private SharedPreferences sharedPreferences;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

    // 連接 Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    ListView lv = view.findViewById(R.id.edit_profile_lv);
    ArrayList<UserProfile> userProfileList = new ArrayList<UserProfile>();

    // 讀取 SharePreferences 資料
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    userName = sharedPreferences.getString("username", "");
    userPhone = sharedPreferences.getString("userphone", "");
    userEmail = sharedPreferences.getString("useremail", "");
    Log.d("userEmail", userEmail);

    userProfileList.add(new UserProfile(userProfileTitle[0], userName));
    userProfileList.add(new UserProfile(userProfileTitle[1], userPhone));

    UserProfileAdapter adapter =  new UserProfileAdapter(getContext(), R.layout.layout_edit_profile, userProfileList);
    lv.setAdapter(adapter);

    // ListView 監聽器
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());
        editDialog.setTitle(userProfileTitle[index]);
        EditText updateDataInput = new EditText(getContext());

        if (index == 0) {
          updateDataInput.setText(userName);
        } else if (index ==  1) {
          updateDataInput.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        editDialog.setView(updateDataInput);

        editDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String inputText = updateDataInput.getText().toString();
            if  (index == 1 && inputText.length() != 10) {
              Toast.makeText(getContext(), "輸入錯誤!電話號碼必須為十個數字!", Toast.LENGTH_SHORT).show();
            } else if (inputText.length() <= 0) {
              Toast.makeText(getContext(), "輸入錯誤!輸入不能為空", Toast.LENGTH_SHORT).show();
            } else {
              updateData = updateDataInput.getText().toString();
              switch (index){
                case 0:
                  updateUserData(index, userName, updateData, userEmail);
                  break;
                case 1:
                  updateUserData(index, userPhone, updateData, userEmail);
                  break;
              }
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

    // Inflate the layout for this fragment
    return view;
  }

  // 將使用者資料更新到 Firebase
  private void updateUserData(int index, String currentUserData, String newData, String userEmail) {

    Map<String, Object> userProfile = new HashMap<>();
    switch (index) {
      case 0:
        userProfile.put("username", newData);
        profileType = "username";
        break;
      case 1:
        userProfile.put("phone", newData);
        profileType = "userphone";
        break;
    }


    Log.d("UpdateUserData", newData + ", " + String.valueOf(index) + ", " + currentUserData);
    db.collection("users").whereEqualTo("email", userEmail)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful() && !task.getResult().isEmpty()) {
          db.collection("users")
              .document(userId)
              .update(userProfile)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  getFragmentManager().beginTransaction().detach(EditProfileFragment.this).commit();
                  getFragmentManager().beginTransaction().attach(EditProfileFragment.this).commit();
                  Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                }
              }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(getContext(), "發生錯誤", Toast.LENGTH_SHORT).show();

            }
          });
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString(profileType, newData);
          editor.commit();
          Log.d("updateProfile", "{" + profileType + " / " + newData + "}");
        } else {
          Toast.makeText(getContext(), "更新失敗", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}