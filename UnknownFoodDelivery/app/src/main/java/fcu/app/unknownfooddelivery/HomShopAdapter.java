package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HomShopAdapter extends ArrayAdapter<HomeShopItem> {

  private Context context;
  private List<HomeShopItem> homeShopItem;
  private Uri uri;
  private StorageReference  storageRef, pic_storage;

  public HomShopAdapter(@NonNull Context context, int resource, @NonNull List<HomeShopItem> homeShopItem) {
    super(context, resource, homeShopItem);
    this.context = context;
    this.homeShopItem = homeShopItem;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.layout_home, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    HomeShopItem item = homeShopItem.get(position);

    TextView tvShopName = itemLayout.findViewById(R.id.tv_shop_name);
    tvShopName.setText(item.getShopName());

    TextView tvShopAddress = itemLayout.findViewById(R.id.tv_shop_address);
    tvShopAddress.setText(item.getShopAddress());

    ImageView imShopImg = itemLayout.findViewById(R.id.im_shopImg);
    storageRef = FirebaseStorage.getInstance().getReference();
    pic_storage = storageRef.child(item.getImgName());
    try {
      File localFile = File.createTempFile("images", "jpg");
      pic_storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
          imShopImg.setImageURI(Uri.fromFile(localFile));
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

   return  itemLayout;
  }
}
