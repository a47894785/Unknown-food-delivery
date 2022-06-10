package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import fcu.app.unknownfooddelivery.adapter.HomShopAdapter;
import fcu.app.unknownfooddelivery.item.HomeShopItem;

public class HomeFragment extends Fragment {

  TextView tvCurrentAddress;
  ListView lvShop;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  public String count;
  private SharedPreferences sharedPreferences;
  private String currentAddress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    // 連接 Components
    tvCurrentAddress = view.findViewById(R.id.home_frag_address);
    lvShop = view.findViewById(R.id.lv_shop);

    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    // 連接 Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    ArrayList<String> shopIdList = new ArrayList<>();

    // 拿取 Firebase 中 shops 中的所有店家資料，並新增到店家的 ArrayLists，設置 Adapter
    db.collection("shops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        count = String.valueOf(task.getResult().size());
        ArrayList<HomeShopItem> homeShopLists = new ArrayList<>();

        for (QueryDocumentSnapshot document : task.getResult()) {
          Log.d("documents", document.getId() + " => " + document.getData());
          if (document.getData().get("shopName") != ""){
            shopIdList.add(document.getId());
            Log.d("documents", String.valueOf(document.getData().get("shopName")));
            String shopName = String.valueOf(document.getData().get("shopName"));
            String shopAddress = String.valueOf(document.getData().get("shopAddress"));
            String shopImgName = String.valueOf(document.getData().get("shopImage"));
            homeShopLists.add(new HomeShopItem(shopName, shopAddress, shopImgName));
          }
        }

        HomShopAdapter adapter = new HomShopAdapter(getContext(), R.layout.layout_home, homeShopLists);
        lvShop.setAdapter(adapter);
        for (int i = 0; i < shopIdList.size(); i++) {
          Log.d("ShopInfo", shopIdList.get(i));
        }
      }

    });

    // ListView 監聽器，點擊可以將切換到該店家的詳細頁面
    lvShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        String selectedId = shopIdList.get(index);
        ShopInfoFragment shopInfoFragment = new ShopInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shopId", selectedId);
        shopInfoFragment.setArguments(bundle);

        EditText etSearch = getActivity().findViewById(R.id.et_search);
        etSearch.setVisibility(View.GONE);

        ImageView imBackArrow = getActivity().findViewById(R.id.im_back_arrow);
        imBackArrow.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.btn_nav_container, shopInfoFragment).commit();
      }
    });

    return view;
  }

  @Override
  public void onResume() {
    currentAddress = sharedPreferences.getString("Address", "無");
//    Log.d("CurrentAddress", currentAddress);
    tvCurrentAddress.setText(currentAddress);
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }
}