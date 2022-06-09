package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fcu.app.unknownfooddelivery.adapter.CartAdapter;
import fcu.app.unknownfooddelivery.adapter.MenuAdapter;
import fcu.app.unknownfooddelivery.item.CartItem;
import fcu.app.unknownfooddelivery.item.MealItem;

public class CartFragment extends Fragment {

  private  String userid,username,key;
  private TextView tv_total;
  private ListView lv_cart;
  private ArrayList temp ;
  private int money = 0;
  private ArrayList<CartItem> cartList;
  private Button btn_Summit,btn_Erase_all;
  private Boolean flag;
  private SharedPreferences sharedPreferences;

  FirebaseAuth fAuth;
  FirebaseFirestore db;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_cart, container, false);

    tv_total = view.findViewById(R.id.tv_money);
    lv_cart = view.findViewById(R.id.lv_cart);
    btn_Summit = view.findViewById(R.id.btn_summit);
    btn_Erase_all = view.findViewById(R.id.btn_erase_all);

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    username = sharedPreferences.getString("username","");

//    if (getArguments() != null) {
//      userid = this.getArguments().getString("userid");
//      username = this.getArguments().getString("username");
//      Log.d("cart_userid", userid +" + " + username);
//    }

    MainActivity_simulate mainActivity = (MainActivity_simulate) getActivity();
    temp = mainActivity.getSQLdata_to_cart();
    cartList = new ArrayList<>();
    money = 0;

    //判斷購物車是否有東西
    if(temp.size() == 0) {
      flag = false;
    }
    else{
      flag = true;
    }

    for(int i = 0; i < temp.size();i+=4){
      cartList.add(new CartItem(temp.get(i).toString()+ " - " + temp.get(i+1).toString(),temp.get(i+2).toString() + " x " + temp.get(i+3).toString()));
      money += Integer.valueOf(temp.get(i+2).toString()) * Integer.valueOf(temp.get(i+3).toString());
    }
    CartAdapter adapter = new CartAdapter(getContext(), R.layout.cart_item, cartList);
    lv_cart.setAdapter(adapter);
    tv_total.setText(Integer.toString(money) + "元");



    btn_Summit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(flag){
          AlertDialog.Builder summitDialog = new AlertDialog.Builder(getContext());
          summitDialog.setTitle("確認餐點並送出");

          summitDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              for(int item = 0 ; item < temp.size();item+= 4){
                String docupath = temp.get(item).toString()+ "-" + temp.get(item+1).toString();
                DocumentReference documentReference = db.collection("orders").document();

                Map<String, Object> order = new HashMap<String, Object>();
                order.put("orderShopName",temp.get(item).toString());
                order.put("orderMealName",temp.get(item+1).toString());
                order.put("orderMealPrice",temp.get(item+2).toString());
                order.put("orderMealNum", temp.get(item+3).toString());
                order.put("orderUserName",username);
                order.put("orderStatus","no");

                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                    Log.d("order","oder summit success");
                    Toast.makeText(getContext(), "訂單送出", Toast.LENGTH_SHORT).show();
                  }
                }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {

                    Log.d("order","oder summit fail");
                  }
                });
              }
              mainActivity.delSqlData();
              mainActivity.getSqlData();
              getFragmentManager().beginTransaction().detach(CartFragment.this).commit();
              getFragmentManager().beginTransaction().attach(CartFragment.this).commit();
            }
          });
          summitDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

              dialogInterface.cancel();
            }
          });
          summitDialog.show();
        }
        else{
          Toast.makeText(getContext(), "購物車尚無東西", Toast.LENGTH_SHORT).show();
        }
      }
    });

    btn_Erase_all.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder eraseDialog = new AlertDialog.Builder(getContext());
        eraseDialog.setTitle("確認清除購物車");

        eraseDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            mainActivity.delSqlData();
            mainActivity.getSqlData();
            getFragmentManager().beginTransaction().detach(CartFragment.this).commit();
            getFragmentManager().beginTransaction().attach(CartFragment.this).commit();
          }
        });
        eraseDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });
        eraseDialog.show();
      }
    });

    lv_cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
        deleteDialog.setTitle("確認刪除此餐點?");
        deleteDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String shopname = temp.get(index*4).toString();
            String mealname = temp.get(index*4+1).toString();
            mainActivity.delSqlDate_cart(shopname,mealname);
            mainActivity.getSqlData();
            getFragmentManager().beginTransaction().detach(CartFragment.this).commit();
            getFragmentManager().beginTransaction().attach(CartFragment.this).commit();
          }
        });
        deleteDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });
        deleteDialog.show();
      }
    });
    return view;
  }
}