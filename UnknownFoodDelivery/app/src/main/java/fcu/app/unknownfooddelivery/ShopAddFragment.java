package fcu.app.unknownfooddelivery;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ShopAddFragment extends Fragment {

  Button btnUploadImg, btnSendOut;
  EditText etMealName, etMealPrice, etMealInfo;
  ImageView imUpload;
  Intent intent;

  Uri uri;
  String data_type, mealName, mealPrice, mealInfo,shopName;
  StorageReference storageReference,pic_storage;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_shop_add, container, false);
//
    if(getArguments() != null){
      shopName = this.getArguments().getString("shopName");
    }
    btnUploadImg = view.findViewById(R.id.btn_upload_img);
    btnSendOut = view.findViewById(R.id.btn_sendout);
    etMealName = (EditText) view.findViewById(R.id.et_add_meal_name);
    etMealPrice = (EditText) view.findViewById(R.id.et_add_meal_price);
    etMealInfo = (EditText) view.findViewById(R.id.et_add_meal_info);
    imUpload = view.findViewById(R.id.im_upload);

    storageReference = FirebaseStorage.getInstance().getReference();

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    btnUploadImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
      }
    });
    Log.d("shopName",shopName);
    btnSendOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        mealInfo = etMealInfo.getText().toString();
//        mealName = etMealName.getText().toString();
//        mealPrice = etMealPrice.getText().toString()
//        Log.d("shopName",shopName);
//        DocumentReference documentReference = db.collection("shop").document(userId).collection("menu").document(shopName);
//        Map<String, Object> menu = new HashMap<String, Object>();
//        menu.put("mealInfo",mealInfo);
//        menu.put("mealName", mealName);
//        menu.put("mealPrice", mealPrice);
//        menu.put("photoName","");

//        documentReference.set(menu).addOnSuccessListener(new OnSuccessListener<Void>() {
//          @Override
//          public void onSuccess(Void unused) {
//            Log.d("NewMael", "OnSuccess: add meal");
//          }
//        }).addOnFailureListener(new OnFailureListener() {
//          @Override
//          public void onFailure(@NonNull Exception e) {
//            Log.w("NewMael", "OnFailure: " + e);
//          }
//        });
//        pic_storage = storageReference.child(mealName + data_type);
//        pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//          @Override
//          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            Log.d("photo", "OnSuccess: upload photo");
//          }
//        });
      }
    });
    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == 1){
      uri = data.getData();
      Log.d("URIINFO", String.valueOf(uri));
      imUpload.setImageURI(uri);
      ContentResolver contentResolver = getContext().getContentResolver();
      MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
      data_type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}