package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class ShopHomeFragment extends Fragment {

  private String shopStatus;
  private TextView tvStatus;
  private Spinner spinner;
  private Button btnChange;
  private int selected;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_shop_home, container, false);

    tvStatus = view.findViewById(R.id.tv_shop_status);
    spinner = view.findViewById(R.id.sp_status);
    btnChange = view.findViewById(R.id.btn_change_status);

    if (getArguments() != null) {
      shopStatus = this.getArguments().getString("shopStatus");
      Log.d("Status", shopStatus);
    }

//    switch (shopStatus) {
//      case "open":
//        tvStatus.setText("營業中");
//        break;
//      case "busy":
//        tvStatus.setText("忙碌中");
//        break;
//      case "close":
//        tvStatus.setText("結束營業");
//        break;
//    }



    btnChange.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selected = spinner.getSelectedItemPosition();
        switch (selected) {
          case 1:

            tvStatus.setText("營業中");
            break;
          case 2:
            tvStatus.setText("忙碌中");
            break;
          case 3:
            tvStatus.setText("結束營業");
            break;
          default:
            tvStatus.setText("請設定營業狀態");
            break;
        }
      }
    });

    return view;
  }
}