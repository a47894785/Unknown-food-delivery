package fcu.app.unknownfooddelivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fcu.app.unknownfooddelivery.R;
import fcu.app.unknownfooddelivery.item.CartItem;
import fcu.app.unknownfooddelivery.item.HomeShopItem;

public class CartAdapter extends ArrayAdapter<CartItem> {

  private Context context;
  private List<CartItem> cartItem;

  public CartAdapter(@NonNull Context context, int resource, @NonNull List<CartItem> cartItem) {
    super(context, resource, cartItem);
    this.context = context;
    this.cartItem = cartItem;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.cart_item, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    CartItem item = cartItem.get(position);

    TextView tvShop_Meal_Name= itemLayout.findViewById(R.id.tv_shopname_mealname);
    tvShop_Meal_Name.setText(item.getTv_shopname_mealname());

    TextView tv_money = itemLayout.findViewById(R.id.tv_money_num);
    tv_money.setText(item.getTv_money_num());

    return  itemLayout;
  }
}
