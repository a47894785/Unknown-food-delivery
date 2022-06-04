package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fcu.app.unknownfooddelivery.adapter.MenuAdapter;
import fcu.app.unknownfooddelivery.item.HomeShopItem;
import fcu.app.unknownfooddelivery.item.MealItem;

public class ShopInfoFragment extends Fragment {

  private ImageView imShopImage;
  private TextView tvShopName, tvShopPhone;
  private String shopId, shopName, shopPhone, shopImgUrl;
  private ListView lvMenu;
  private FirebaseFirestore db;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_shop_info, container, false);

    if (getArguments() != null) {
      shopId = this.getArguments().getString("shopId");
    }

    db = FirebaseFirestore.getInstance();
    imShopImage = view.findViewById(R.id.im_shop_image);
    tvShopName = view.findViewById(R.id.tv_shop_info_name);
    tvShopPhone = view.findViewById(R.id.tv_shop_info_phone);
    lvMenu = view.findViewById(R.id.lv_show_menu);

    DocumentReference documentReference = db.collection("shops").document(shopId);
    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
       if (documentSnapshot.exists()) {
         shopName = documentSnapshot.getString("shopName");
         shopPhone = documentSnapshot.getString("shopPhone");
         shopImgUrl = documentSnapshot.getString("shopImage");

         Picasso.get().load(shopImgUrl).into(imShopImage);
         tvShopName.setText(shopName);
         tvShopPhone.setText(shopPhone);
       }
      }
    });

    db.collection("shops").document(shopId).collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        ArrayList<MealItem> mealList = new ArrayList<>();
       for (QueryDocumentSnapshot document : task.getResult()) {
         Log.d("menuInfo", document.getId());
         String mealName = String.valueOf(document.getData().get("mealName"));
         String mealPrice = String.valueOf(document.getData().get("mealPrice"));
         String photoName = String.valueOf(document.getData().get("photoName"));
         mealList.add(new MealItem(mealName, mealPrice, photoName));
       }

       MenuAdapter adapter = new MenuAdapter(getContext(), R.layout.listitem_shop_page, mealList);
       lvMenu.setAdapter(adapter);

      }
    });
    return view;
  }
}