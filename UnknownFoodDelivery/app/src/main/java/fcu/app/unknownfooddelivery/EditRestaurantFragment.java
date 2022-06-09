package fcu.app.unknownfooddelivery;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fcu.app.unknownfooddelivery.adapter.RestaurantProfileAdapter;
import fcu.app.unknownfooddelivery.item.RestaurantProfile;

public class EditRestaurantFragment extends Fragment {

  private String[] restaurantProfileTitle = {"店家名稱", "店家地址", "電話", "電子郵件"};
  String rName, rEmail, rPhone, rAddress, userEmail, rImgUrl;
  String updateData, data_type;
  String profileType = "";
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private SharedPreferences sharedPreferences;

  private Button btnUpload;
  private ImageView imShopImg;
  private Intent intent;
  private Uri uri;
  private StorageReference storageReference,pic_storage;
  Boolean imgFlag = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_restaurant, container, false);

//    if (getArguments() != null) {
//      userEmail = this.getArguments().getString("userEmail");
//      rName = this.getArguments().getString("shopName").equals("") ? "尚未設定店家名稱" : this.getArguments().getString("shopName");
//      rEmail = this.getArguments().getString("shopEmail").equals("") ? "尚未設定電子郵件" : this.getArguments().getString("shopEmail");
//      rPhone = this.getArguments().getString("shopPhone").equals("") ? "尚未設定店家電話" : this.getArguments().getString("shopPhone");
//      rAddress = this.getArguments().getString("shopAddress").equals("") ? "尚未設定店家地址" : this.getArguments().getString("shopAddress");
//      rImgUrl = this.getArguments().getString("shopImage");
//    }

    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    imShopImg = view.findViewById(R.id.im_upload_shop_img);
    btnUpload = view.findViewById(R.id.btn_upload_shop_img);
    storageReference = FirebaseStorage.getInstance().getReference();
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    rName = sharedPreferences.getString("shopName", "").equals("") ? "尚未設定店家名稱" : sharedPreferences.getString("shopName", "");
    rEmail = sharedPreferences.getString("shopEmail", "").equals("") ? "尚未設定電子郵件" : sharedPreferences.getString("shopEmail", "");
    rPhone = sharedPreferences.getString("shopPhone", "").equals("") ? "尚未設定店家電話": sharedPreferences.getString("shopPhone", "");
    rAddress = sharedPreferences.getString("shopAddress", "").equals("") ? "尚未設定店家地址": sharedPreferences.getString("shopAddress", "");
    rImgUrl = sharedPreferences.getString("shopImage", "");
    userEmail = sharedPreferences.getString("useremail", "");
    Log.d("getShopInfor", rName + "/" + rEmail + "/" + rPhone + "/" + rAddress + "/" + userEmail);

    ListView lv = view.findViewById(R.id.lv_edit_shop_prodile);
    ArrayList<RestaurantProfile> restaurantProfileList = new ArrayList<>();

    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[0], rName));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[1], rAddress));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[2], rPhone));
    restaurantProfileList.add(new RestaurantProfile(restaurantProfileTitle[3], rEmail));

    RestaurantProfileAdapter adapter = new RestaurantProfileAdapter(getContext(), R.layout.layout_edit_profile, restaurantProfileList);
    lv.setAdapter(adapter);

    if (rImgUrl != "") {
      Picasso.get().load(rImgUrl).into(imShopImg);
    }

    imShopImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
      }
    });

    btnUpload.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (imgFlag) {
          Log.d("rName", rName);
          pic_storage = storageReference.child(rName + "." + data_type);
          pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              Log.d("photo", "OnSuccess: upload photo");
              pic_storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                  String imgUrl = task.getResult().toString();
                  DocumentReference documentReference = db.collection("shops").document(userId);
                  Map<String, Object> shopImgUrl = new HashMap<>();
                  shopImgUrl.put("shopImage", imgUrl);

                  documentReference.update(shopImgUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      Toast.makeText(getContext(), "上傳成功!", Toast.LENGTH_SHORT).show();
                      Log.d("UploadImage", "Successfully");
                      SharedPreferences.Editor editor = sharedPreferences.edit();
                    }
                  }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.d("UploadImage", "Failed");
                    }
                  });
                }
              });
            }
          });
        } else {
          Toast.makeText(getContext(), "錯誤", Toast.LENGTH_SHORT).show();
        }

      }
    });

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());
        editDialog.setTitle(restaurantProfileTitle[index]);
        EditText updateDataInput = new EditText(getContext());
        if (index == 0) {
          if (rName.equals("尚未設定店家名稱")) {
            updateDataInput.setHint(rName);
          } else {
            updateDataInput.setText(rName);
          }
        } else if (index == 1) {
          if (rAddress.equals("尚未設定店家地址")) {
            updateDataInput.setHint(rAddress);
          } else {
            updateDataInput.setText(rAddress);
          }
        } else if (index == 2) {
          if (rPhone.equals("尚未設定店家電話")) {
            updateDataInput.setHint(rPhone);
          }
//          updateDataInput.setText(rPhone);
          updateDataInput.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (index == 3) {
          if (rEmail.equals("尚未設定電子郵件")) {
            updateDataInput.setHint(rEmail);
          } else {
            updateDataInput.setText(rEmail);
          }
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == 1){
      uri = data.getData();
      Log.d("URIINFO", String.valueOf(uri));
      imShopImg.setImageURI(uri);
      ContentResolver contentResolver = getContext().getContentResolver();
      MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
      data_type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
      imgFlag = true;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void updateRestaurantInfo(int index, String userEmail, String newData) {
    Map<String, Object> rProfile = new HashMap<>();

    switch (index) {
      case 0:
        rProfile.put("shopName", newData);
        profileType = "shopName";
        break;
      case 1:
        rProfile.put("shopAddress", newData);
        profileType = "shopAddress";
        break;
      case 2:
        rProfile.put("shopPhone", newData);
        profileType = "shopPhone";
        break;
      case 3:
        rProfile.put("shopEmail", newData);
        profileType = "shopEmail";
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

                  getFragmentManager().beginTransaction().detach(EditRestaurantFragment.this).commit();
                  getFragmentManager().beginTransaction().attach(EditRestaurantFragment.this).commit();
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