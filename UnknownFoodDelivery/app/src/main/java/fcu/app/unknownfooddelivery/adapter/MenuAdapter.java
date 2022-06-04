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

import com.squareup.picasso.Picasso;

import java.util.List;

import fcu.app.unknownfooddelivery.R;
import fcu.app.unknownfooddelivery.item.HomeShopItem;
import fcu.app.unknownfooddelivery.item.MealItem;

public class MenuAdapter extends ArrayAdapter<MealItem> {

  private Context context;
  private List<MealItem> mealItems;

  public MenuAdapter(@NonNull Context context, int resource, @NonNull List<MealItem> mealItems) {
    super(context, resource, mealItems);
    this.context = context;
    this.mealItems = mealItems;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.listitem_shop_page, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    MealItem item = mealItems.get(position);

    TextView tvMealName = itemLayout.findViewById(R.id.tv_menu_meal_name);
    tvMealName.setText(item.getMealName());

    TextView tvMealPrice = itemLayout.findViewById(R.id.tv_menu_meal_price);
    tvMealPrice.setText("$ " + item.getMealPrice());

    ImageView imMealImg =  itemLayout.findViewById(R.id.im_menu_meal_img);
    Picasso.get().load(item.getMealImg()).into(imMealImg);

    return itemLayout;
  }
}
