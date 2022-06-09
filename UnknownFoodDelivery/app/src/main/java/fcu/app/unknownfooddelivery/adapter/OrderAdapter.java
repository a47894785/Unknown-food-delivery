package fcu.app.unknownfooddelivery.adapter;

import android.content.Context;
import android.util.Log;
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
import fcu.app.unknownfooddelivery.item.MealItem;
import fcu.app.unknownfooddelivery.item.OrderItem;

public class OrderAdapter extends ArrayAdapter<OrderItem> {

  private Context context;
  private List<OrderItem> orderItems;

  public OrderAdapter(@NonNull Context context, int resource, @NonNull List<OrderItem> orderItems) {
    super(context, resource, orderItems);
    this.context = context;
    this.orderItems = orderItems;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.listitem_order_deliver, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    OrderItem item = orderItems.get(position);

    TextView tvShowOrder = itemLayout.findViewById(R.id.tv_order_info);
    Log.d("TEMP", item.getOrderId());
    tvShowOrder.setText("# " + item.getShopName() + " - " + item.getMealName());

    return itemLayout;
  }
}
