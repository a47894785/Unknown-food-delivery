package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditProfileFragment extends Fragment {

  private String[] userProfileTitle = {"使用者名稱", "電子郵件", "電話號碼"};
  String userName, userEmail, userPhone;
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

//    fAuth = FirebaseAuth.getInstance();
//    db = FirebaseFirestore.getInstance();
//    userId = fAuth.getCurrentUser().getUid();

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
    userProfileList.add(new UserProfile(userProfileTitle[1], userEmail));
    userProfileList.add(new UserProfile(userProfileTitle[2], userPhone));

    UserProfileAdapter adapter =  new UserProfileAdapter(getContext(), R.layout.layout_edit_profile, userProfileList);
    lv.setAdapter(adapter);

    // Inflate the layout for this fragment
    return view;
  }
}