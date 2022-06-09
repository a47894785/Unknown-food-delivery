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
import fcu.app.unknownfooddelivery.item.MealItem;
import fcu.app.unknownfooddelivery.item.UpdateMenuItem;

public class UpdateMenuAdapter extends ArrayAdapter<UpdateMenuItem> {

  private Context context;
  private List<UpdateMenuItem> updateMenuItems;

  public UpdateMenuAdapter(@NonNull Context context, int resource, @NonNull List<UpdateMenuItem> updateMenuItems) {
    super(context, resource, updateMenuItems);
    this.context = context;
    this.updateMenuItems = updateMenuItems;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.listitem_update_menu, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    UpdateMenuItem item = updateMenuItems.get(position);

    TextView tvMealName = itemLayout.findViewById(R.id.tv_update_menu_meal_name);
    tvMealName.setText(item.getMealName());

    ImageView imMealImg = itemLayout.findViewById(R.id.im_update_meal_img);
    Picasso.get().load(item.getMealImg()).into(imMealImg);

    return itemLayout;
  }
}
