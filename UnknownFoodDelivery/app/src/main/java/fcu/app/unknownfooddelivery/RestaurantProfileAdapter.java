package fcu.app.unknownfooddelivery;

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

public class RestaurantProfileAdapter extends ArrayAdapter<RestaurantProfile> {

  private Context context;
  private List<RestaurantProfile> restaurantProfileItem;

  public RestaurantProfileAdapter(@NonNull Context context, int resource, @NonNull List<RestaurantProfile> restaurantProfileItem) {
    super(context, resource, restaurantProfileItem);
    this.context = context;
    this.restaurantProfileItem = restaurantProfileItem;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    LayoutInflater inflater = LayoutInflater.from(this.context);
    LinearLayout itemLayout = null;

    if (convertView == null) {
      itemLayout = (LinearLayout) inflater.inflate(R.layout.layout_edit_profile, null);
    } else {
      itemLayout = (LinearLayout) convertView;
    }

    RestaurantProfile item = restaurantProfileItem.get(position);

    TextView tvTitle = itemLayout.findViewById(R.id.tv_edit_profile_title);
    tvTitle.setText(item.getTitle());

    TextView tvUpdate = itemLayout.findViewById(R.id.tv_edit_profile_update);
    tvUpdate.setText(item.getProfile());

    return itemLayout;
  }
}
