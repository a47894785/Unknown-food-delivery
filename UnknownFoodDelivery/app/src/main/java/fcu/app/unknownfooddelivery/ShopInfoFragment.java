package fcu.app.unknownfooddelivery;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import fcu.app.unknownfooddelivery.item.MealItem;

public class ShopInfoFragment extends Fragment {

  private ImageView imShopImage, imBackArrow;
  private TextView tvShopName, tvShopPhone;
  private String shopId, shopName, shopPhone, shopImgUrl;
  private ListView lvMenu;
  private FirebaseFirestore db;
  private String mealName;
  private String mealPrice;
  private String photoName;
  private String mealInfo;
  private ArrayList<String> mealNameList, mealPriceList;
  private ArrayList<MealItem> mealList;

  static final String DB_NAME = "unknown";
  static final String TB_NAME = "cart";
  SQLiteDatabase sqLiteDatabase;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_shop_info, container, false);


    if (getArguments() != null) {
      shopId = this.getArguments().getString("shopId");
    }

    // 連接 Firebase Firestore
    db = FirebaseFirestore.getInstance();

    // 連接 Components
    imShopImage = view.findViewById(R.id.im_shop_image);
    imBackArrow = getActivity().findViewById(R.id.im_back_arrow);
    tvShopName = view.findViewById(R.id.tv_shop_info_name);
    tvShopPhone = view.findViewById(R.id.tv_shop_info_phone);
    lvMenu = view.findViewById(R.id.lv_show_menu);
    mealList = new ArrayList<>();

    // Firebase 拿取該店家的資料
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


    // Firebase 拿該店家的菜單資料
    db.collection("shops").document(shopId).collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {

        for (QueryDocumentSnapshot document : task.getResult()) {
          Log.d("menuInfo", document.getId());
          mealName = String.valueOf(document.getData().get("mealName"));
          mealPrice = String.valueOf(document.getData().get("mealPrice"));
          photoName = String.valueOf(document.getData().get("photoName"));
          mealInfo = String.valueOf(document.getData().get("mealInfo"));
          mealList.add(new MealItem(mealName, mealPrice, photoName, shopName));
        }

        MenuAdapter adapter = new MenuAdapter(getContext(), R.layout.listitem_shop_page, mealList);
        lvMenu.setAdapter(adapter);

      }
    });

    // ListView 監聽器，點擊可以選擇數量並加入購物車
    lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder mealDialog = new AlertDialog.Builder(getContext());
        String selectMealName = mealList.get(index).getMealName();
        String selectMaelPrice = mealList.get(index).getMealPrice();
        Log.d("SelectMeal", mealList.get(index).getShopName() + " - " + mealList.get(index).getMealName());
        mealDialog.setTitle("選擇欲訂購的數量");
        Spinner mealNum = new Spinner(getContext());
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.mealNum, android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealNum.setAdapter(spinnerAdapter);
        mealNum.setPadding(20, 0 ,0, 0);
        mealDialog.setView(mealNum);
        mealDialog.setPositiveButton("加入購物車", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String num = String.valueOf(mealNum.getSelectedItemPosition() + 1);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateSql(shopId, shopName, selectMealName, selectMaelPrice, num, photoName);
            Toast.makeText(getContext(), "加入成功", Toast.LENGTH_SHORT).show();
            mainActivity.getSqlData();
          }
        });
        mealDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });
        mealDialog.show();
      }
    });

    // 返回
    imBackArrow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        HomeFragment homeFragment = new HomeFragment();
        EditText etSearch = getActivity().findViewById(R.id.et_search);
        imBackArrow.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.btn_nav_container, homeFragment).commit();
      }
    });

    return view;
  }
}