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

import fcu.app.unknownfooddelivery.adapter.UserProfileAdapter;
import fcu.app.unknownfooddelivery.item.UserProfile;

public class EditProfileFragment extends Fragment {

  private String[] userProfileTitle = {"使用者名稱", "電話號碼"};
  String userName, userEmail, userPhone;
  String updateData;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

    if (getArguments() != null) {
      userEmail = this.getArguments().getString("email");
      userName = this.getArguments().getString("username");
      userPhone = this.getArguments().getString("phone");
    }

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

//    DocumentReference documentReference = db.collection("users").document(userId);
//    Log.d("UserId", userId);
//    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//      @Override
//      public void onSuccess(DocumentSnapshot documentSnapshot) {
//        if (documentSnapshot.exists()) {
//          Log.d("GetUserInfo", "Document Exists.");
//          userName = documentSnapshot.getString("username");
//          userEmail = documentSnapshot.getString("email");
//          userPhone = documentSnapshot.getString("phone");
//          Log.d("GetUserInfo", "UserName: " + userName + ", UserEmail: " + userEmail + ", UserPhone: " + userPhone);
//        } else {
//          Log.d("GetUserInfo", "Error, document do not exists.");
//        }
//      }
//    });

    ListView lv = view.findViewById(R.id.edit_profile_lv);
    ArrayList<UserProfile> userProfileList = new ArrayList<UserProfile>();

    userProfileList.add(new UserProfile(userProfileTitle[0], userName));
    userProfileList.add(new UserProfile(userProfileTitle[1], userPhone));
//    userProfileList.add(new UserProfile(userProfileTitle[1], userEmail));

    UserProfileAdapter adapter =  new UserProfileAdapter(getContext(), R.layout.layout_edit_profile, userProfileList);
    lv.setAdapter(adapter);

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
//           Toast.makeText(getContext(), "User Profile is update to " + updateData, Toast.LENGTH_SHORT).show();
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

  private void updateUserData(int index, String currentUserData, String newData, String userEmail) {
    String profileType = "";
    Map<String, Object> userProfile = new HashMap<>();
    switch (index) {
      case 0:
        userProfile.put("username", newData);
        profileType = "username";
        break;
      case 1:
        userProfile.put("phone", newData);
        profileType = "phone";
        break;
    }


    Log.d("UpdateUserData", newData + ", " + String.valueOf(index) + ", " + currentUserData);
    db.collection("users").whereEqualTo("email", userEmail)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful() && !task.getResult().isEmpty()) {
//          DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//          String documentId = documentSnapshot.getId();
          db.collection("users")
              .document(userId)
              .update(userProfile)
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