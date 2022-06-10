package fcu.app.unknownfooddelivery;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fcu.app.unknownfooddelivery.adapter.UpdateMenuAdapter;
import fcu.app.unknownfooddelivery.item.UpdateMenuItem;

public class UpdateMenuFragment extends Fragment {

  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private MainActivity mainActivity_simulate;
  private String mealName, mealPrice, mealImg, mealId;
  private String type;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_update_menu, container, false);
    // Inflate the layout for this fragment

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();
    ListView lv = view.findViewById(R.id.lv_update_manu);
    ArrayList<UpdateMenuItem> menuLists = new ArrayList<>();

    db.collection("shops").document(userId).collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {


        for (QueryDocumentSnapshot document : task.getResult()) {
          Log.d("document", document.getId() + " => " + document.getData());
          mealName = String.valueOf(document.getData().get("mealName"));
          mealPrice = String.valueOf(document.getData().get("mealPrice"));
          mealImg = String.valueOf(document.getData().get("photoName"));
          mealId = String.valueOf(document.getId());
          menuLists.add(new UpdateMenuItem(mealName, mealImg, mealPrice, mealId));
        }

        UpdateMenuAdapter adapter = new UpdateMenuAdapter(getContext(), R.layout.listitem_update_menu, menuLists);
        lv.setAdapter(adapter);

      }
    });

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        Log.d("checkMenuItem", String.valueOf(menuLists.get(index).getMealId()));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder updateDialog = new AlertDialog.Builder(getContext());
        String selectMealName = menuLists.get(index).getMealName();
        String selectMealPrice = menuLists.get(index).getMealPrice();
        mealId = menuLists.get(index).getMealId();

        EditText editName = new EditText(getContext());
        editName.setText(selectMealName);
        linearLayout.addView(editName);

        EditText editPrice = new EditText(getContext());
        editPrice.setText(selectMealPrice);
        editPrice.setInputType(InputType.TYPE_CLASS_PHONE);
        linearLayout.addView(editPrice);

        updateDialog.setTitle("更新餐點資訊");
        updateDialog.setView(linearLayout);

        updateDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            updateMenu(mealId, editName.getText().toString(), editPrice.getText().toString());
          }
        });

        updateDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
          }
        });
        updateDialog.show();
      }
    });


//    menuLists.add(new UpdateMenuItem())

    return view;
  }

  private void updateMenu(String mealId, String newName, String newPrice) {
    Map<String, Object> menuInfo = new HashMap<>();
    menuInfo.put("mealName", newName);
    menuInfo.put("mealPrice", newPrice);

    db.collection("shops").document(userId).collection("menu").document(mealId)
        .update(menuInfo)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void unused) {
            getFragmentManager().beginTransaction().detach(UpdateMenuFragment.this).commit();
            getFragmentManager().beginTransaction().attach(UpdateMenuFragment.this).commit();
            Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), "更新失敗", Toast.LENGTH_SHORT).show();
      }
    });

  }
}