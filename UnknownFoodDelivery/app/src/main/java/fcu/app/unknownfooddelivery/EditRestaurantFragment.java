package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditRestaurantFragment extends Fragment {

  private String[] restaurantProfileTitle = {"店家名稱", "店家地址", "電話", "電子郵件"};
  String rName, rEmail, rPhone, rAddress;
  String updateData;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_restaurant, container, false);

    if (getArguments() != null) {
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

    return view;
  }
}