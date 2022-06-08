package fcu.app.unknownfooddelivery;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeliveryStatusFragment extends Fragment {

  private String deliverStatus;
  private TextView tvDeliverStatus;
  private Spinner spinner;
  private Button btnDeliverChange;
  private int selected;
  private FirebaseFirestore db;
  private FirebaseAuth fAuth;
  private String userId;
  private String changeStatus;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_deliver_status, container, false);

    tvDeliverStatus = view.findViewById(R.id.tv_deliver_status);
    spinner = view.findViewById(R.id.deliver_status);
    btnDeliverChange = view.findViewById(R.id.btn_change_deliver_status);

    if (getArguments() != null) {
      deliverStatus = this.getArguments().getString("shopStatus");
      Log.d("Status", deliverStatus);
    }

    fAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();

    /*switch (deliverStatus) {
      case "open":
        tvDeliverStatus.setText("接單中");
        tvDeliverStatus.setTextColor(Color.GREEN);
        break;
      case "busy":
        tvDeliverStatus.setText("忙碌中");
        tvDeliverStatus.setTextColor(Color.RED);
        break;
      case "close":
        tvDeliverStatus.setText("停止接單");
        tvDeliverStatus.setTextColor(Color.GRAY);
        break;
    }*/



   /* btnDeliverChange.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selected = spinner.getSelectedItemPosition();
        switch (selected) {
          case 1:
            if (deliverStatus.equals("open")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "open";
            }
//            tvStatus.setText("營業中");
            break;
          case 2:
            if (deliverStatus.equals("busy")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "busy";
            }
//            tvStatus.setText("忙碌中");
            break;
          case 3:
            if (deliverStatus.equals("close")){
              changeStatus = "error";
              Toast.makeText(getContext(), "錯誤!", Toast.LENGTH_SHORT).show();
            } else {
              changeStatus = "close";
            }
//            tvStatus.setText("結束營業");
            break;
          default:
            Toast.makeText(getContext(), "錯誤，請選擇欲更改的狀態", Toast.LENGTH_SHORT).show();
            changeStatus = "error";
//            tvStatus.setText("請設定營業狀態");
            break;
        }
        Log.d("ChangeStatus", changeStatus);
        Map<String, Object> shopChangeStatus = new HashMap<>();
        shopChangeStatus.put("shopStatus", changeStatus);

        db.collection("shops").document(userId).update(shopChangeStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void unused) {
            Toast.makeText(getContext(), "更改接單狀態成功", Toast.LENGTH_SHORT).show();
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "更改接單狀態失敗", Toast.LENGTH_SHORT).show();

          }
        });

      }
    });*/

    return view;
  }
}