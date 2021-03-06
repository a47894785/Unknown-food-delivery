package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fcu.app.unknownfooddelivery.adapter.OrderAdapter;
import fcu.app.unknownfooddelivery.item.OrderItem;

public class DeliveryStatusFragment extends Fragment {

  private String deliverStatus;
  private TextView tvDeliverStatus;
  private ListView lv;
  private ImageView imStatus;
  private ArrayList<OrderItem> orderLists;
  private Spinner spinner;
  private Button btnDeliverChange;
  private int selected;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private String changeStatus;
  private String orderId, shopName, mealName, mealPrice, mealNum, userName, orderStatus;
  private SharedPreferences sharedPreferences;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_deliver_status, container, false);

    // ?????? Components
    tvDeliverStatus = view.findViewById(R.id.tv_deliver_status);
    spinner = view.findViewById(R.id.deliver_status);
    btnDeliverChange = view.findViewById(R.id.btn_change_deliver_status);
    lv = view.findViewById(R.id.lv_show_order);
    imStatus = view.findViewById(R.id.im_status);
    orderLists = new ArrayList<>();

    // ?????? Firebase Authentication & Firebase Firestore
    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    // ??? SharedPreferences ??????
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    deliverStatus = sharedPreferences.getString("deliverstatus", "close");

    // ????????????????????????
    switch (deliverStatus) {
      case "open":
        tvDeliverStatus.setText("?????????...");
        tvDeliverStatus.setTextColor(Color.GREEN);
        imStatus.setImageResource(R.drawable.ic_delivery);
        lv.setVisibility(View.VISIBLE);
        break;
      case "close":
        tvDeliverStatus.setText("????????????");
        tvDeliverStatus.setTextColor(Color.GRAY);
        imStatus.setImageResource(R.drawable.ic_relax);
        lv.setVisibility(View.GONE);
        break;
    }

    // ?????? Firebase ??? orders ????????? (??????)
    db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
          Log.d("ORDER_ID", document.getId());
          orderId = document.getId();
          userName = String.valueOf(document.getData().get("orderUserName"));
          shopName = String.valueOf(document.getData().get("orderShopName"));
          mealName = String.valueOf(document.getData().get("orderMealName"));
          mealPrice = String.valueOf(document.getData().get("orderMealPrice"));
          mealNum = String.valueOf(document.getData().get("orderMealNum"));
          orderStatus = String.valueOf(document.getData().get("orderStatus"));

          if (orderStatus.equals("no")){
            orderLists.add(new OrderItem(orderId, userName, shopName, mealName, mealPrice, mealNum, orderStatus));
          }
        }

        OrderAdapter adapter = new OrderAdapter(getContext(), R.layout.listitem_order_deliver, orderLists);
        lv.setAdapter(adapter);

      }
    });

    // ListView ?????????
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        AlertDialog.Builder shopOrder = new AlertDialog.Builder(getContext());
        shopOrder.setTitle("??????????????????");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(80, 80, 80, 80);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvUserName = new TextView(getContext());
        tvUserName.setText("?????????: " + orderLists.get(index).getUserName());
        tvUserName.setTextSize(16);
        tvUserName.setTextColor(Color.BLACK);
        layout.addView(tvUserName);

        TextView tvShopName = new TextView(getContext());
        tvShopName.setText("??????: " + orderLists.get(index).getShopName());
        tvShopName.setTextSize(16);
        tvShopName.setTextColor(Color.BLACK);
        layout.addView(tvShopName);

        TextView tvMealName = new TextView(getContext());
        tvMealName.setText("??????: " + orderLists.get(index).getMealName());
        tvMealName.setTextSize(16);
        tvMealName.setTextColor(Color.BLACK);
        layout.addView(tvMealName);

        TextView tvMealPrice = new TextView(getContext());
        tvMealPrice.setText("??????: " + orderLists.get(index).getMealPrice());
        tvMealPrice.setTextSize(16);
        tvMealPrice.setTextColor(Color.BLACK);
        layout.addView(tvMealPrice);

        TextView tvMealNum = new TextView(getContext());
        tvMealNum.setText("??????: " + orderLists.get(index).getMealNum());
        tvMealNum.setTextSize(16);
        tvMealNum.setTextColor(Color.BLACK);
        layout.addView(tvMealNum);

        shopOrder.setView(layout);

        shopOrder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            acceptOrder(orderId);
          }
        });

        shopOrder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });

        shopOrder.show();

      }
    });

    // ????????????????????????????????????????????? Firebase
    btnDeliverChange.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selected = spinner.getSelectedItemPosition();
        switch (selected) {
          case 1:
            if (deliverStatus.equals("open")){
              changeStatus = "error";
              Toast.makeText(getContext(), "??????!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "open";
            }
            break;
          case 2:
            if (deliverStatus.equals("close")){
              changeStatus = "error";
              Toast.makeText(getContext(), "??????!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "close";
            }
            break;
          default:
            Toast.makeText(getContext(), "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            changeStatus = "error";
            break;
        }

        if(changeStatus != "error"){
          Log.d("ChangeStatus", changeStatus);
          Map<String, Object> deliverChangeStatus = new HashMap<>();
          deliverChangeStatus.put("deliverStatus", changeStatus);

          db.collection("delivers").document(userId).update(deliverChangeStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              getFragmentManager().beginTransaction().detach(DeliveryStatusFragment.this).commit();
              getFragmentManager().beginTransaction().attach(DeliveryStatusFragment.this).commit();
              Toast.makeText(getContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(getContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            }
          });
          SharedPreferences.Editor deliver_edit = sharedPreferences.edit();
          deliver_edit.putString("deliverstatus", changeStatus);
          deliver_edit.commit();
        }
        else{
          Toast.makeText(getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
      }
    });

    return view;
  }

  // ????????????????????? Firebase ?????????????????????
  private void acceptOrder(String orderId) {
    Map<String, Object> order = new HashMap<>();
    order.put("orderStatus", "yes");

    db.collection("orders").document(orderId).update(order)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void unused) {
            getFragmentManager().beginTransaction().detach(DeliveryStatusFragment.this).commit();
            getFragmentManager().beginTransaction().attach(DeliveryStatusFragment.this).commit();
            Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
      }
    });
  }
}