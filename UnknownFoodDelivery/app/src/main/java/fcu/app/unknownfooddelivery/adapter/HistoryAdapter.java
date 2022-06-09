package fcu.app.unknownfooddelivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fcu.app.unknownfooddelivery.R;
import fcu.app.unknownfooddelivery.item.CartItem;
import fcu.app.unknownfooddelivery.item.HistoryItem;
import fcu.app.unknownfooddelivery.item.HistoryItem;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
  private Context context;
  private List<HistoryItem> historyItems;

  public HistoryAdapter(@NonNull Context context, int resource, @NonNull List<HistoryItem> historyItems) {
    super(context, resource, historyItems);
    this.context = context;
    this.historyItems = historyItems;


  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.history_item, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }
    HistoryItem item = historyItems.get(position);

    TextView tvShop_Meal_Name= itemLayout.findViewById(R.id.history_shop_name);
    tvShop_Meal_Name.setText(item.getTv_shopname());

    TextView tvMeal_name = itemLayout.findViewById(R.id.history_meal_name);
    tvMeal_name.setText(item.getTv_mealname());

    TextView tvMeal_price = itemLayout.findViewById(R.id.history_meal_price);
    tvMeal_price.setText(item.getTv_mealprice());

    TextView tvMeal_num = itemLayout.findViewById(R.id.history_meal_num);
    tvMeal_num.setText(item.getTv_mealnum());
    return  itemLayout;
  }
}
