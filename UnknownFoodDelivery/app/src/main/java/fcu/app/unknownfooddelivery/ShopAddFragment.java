package fcu.app.unknownfooddelivery;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShopAddFragment extends Fragment {

  Button btnUploadImg, btnSendOut;
  EditText etMealName, etMealPrice, etMealInfo;
  ImageView imUpload;
  Intent intent;

  Uri uri;
  String data_type;
  StorageReference storageReference;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_shop_add, container, false);
//
    btnUploadImg = view.findViewById(R.id.btn_upload_img);
    btnSendOut = view.findViewById(R.id.btn_sendout);
    etMealName = (EditText) view.findViewById(R.id.et_add_meal_name);
    etMealPrice = (EditText) view.findViewById(R.id.et_add_meal_price);
    etMealInfo = (EditText) view.findViewById(R.id.et_add_meal_info);
    imUpload = view.findViewById(R.id.im_upload);

    storageReference = FirebaseStorage.getInstance().getReference();

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