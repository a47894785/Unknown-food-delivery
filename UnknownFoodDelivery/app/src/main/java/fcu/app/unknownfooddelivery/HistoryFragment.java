package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import fcu.app.unknownfooddelivery.adapter.HistoryAdapter;
import fcu.app.unknownfooddelivery.adapter.HomShopAdapter;
import fcu.app.unknownfooddelivery.item.HistoryItem;
import fcu.app.unknownfooddelivery.item.HomeShopItem;

public class HistoryFragment extends Fragment {

  ListView lvHistory;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId,userName;
  private String orderShopName,orderMealName,orderMealPrice,orderMealNum,orderUserName,orderStatus;
  private SharedPreferences sharedPreferences;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_history, container, false);
    lvHistory = view.findViewById(R.id.lv_history);

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();
    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    userName = sharedPreferences.getString("username", "無");
    //Log.d("history",userName);

    db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {

        ArrayList<HistoryItem> historyList = new ArrayList<>();

        for (QueryDocumentSnapshot document : task.getResult()) {
          orderStatus = String.valueOf(document.getData().get("orderStatus"));
          orderUserName = String.valueOf(document.getData().get("orderUserName"));
          Log.d("history",orderStatus);
          if (orderUserName.equals(userName) && orderStatus.equals("yes") ){
            Log.d("history",userName);
            orderShopName= String.valueOf(document.getData().get("orderShopName"));
            orderMealName = String.valueOf(document.getData().get("orderMealName"));
            orderMealPrice = String.valueOf(document.getData().get("orderMealPrice"));
            orderMealNum = String.valueOf(document.getData().get("orderMealNum"));
            Log.d("history",orderShopName + " + " + orderMealName + " + " + orderMealPrice + " + " + orderMealNum);
            historyList.add(new HistoryItem(orderShopName,orderMealName,orderMealPrice,orderMealNum));
          }
        }
        HistoryAdapter adapter = new HistoryAdapter(getContext(), R.layout.history_item, historyList);
        lvHistory.setAdapter(adapter);
      }

    });

    return view;
  }
}