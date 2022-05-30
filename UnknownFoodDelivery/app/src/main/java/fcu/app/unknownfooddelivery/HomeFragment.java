package fcu.app.unknownfooddelivery;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

  TextView tvCurrentAddress;
  ListView lvShop;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  public String count;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    tvCurrentAddress = view.findViewById(R.id.home_frag_address);
    lvShop = view.findViewById(R.id.lv_shop);

    if (getArguments() != null) {
      String currentAddress = this.getArguments().getString("address");
      tvCurrentAddress.setText(currentAddress);
    }

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();
//
    db.collection("shops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        count = String.valueOf(task.getResult().size());
        ArrayList<HomeShopItem> homeShopLists = new ArrayList<>();

        for (QueryDocumentSnapshot document : task.getResult()) {
          Log.d("documents", document.getId() + " => " + document.getData());
          if (document.getData().get("shopName") != ""){
            Log.d("documents", String.valueOf(document.getData().get("shopName")));
            String shopName = String.valueOf(document.getData().get("shopName"));
            String shopAddress = String.valueOf(document.getData().get("shopAddress"));
            String shopImgName = "salad.jpg";
            homeShopLists.add(new HomeShopItem(shopName, shopAddress, shopImgName));
          }
        }

        HomShopAdapter adapter = new HomShopAdapter(getContext(), R.layout.layout_home, homeShopLists);
        lvShop.setAdapter(adapter);

      }
    });


    return view;
  }

}