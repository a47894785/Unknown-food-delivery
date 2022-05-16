package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

public class UserProfileAdapter extends ArrayAdapter<UserProfile> {

  private Context context;
  private List<UserProfile> userProfileItem;

  public UserProfileAdapter(@NonNull Context context, int resource, @NonNull List<UserProfile> userProfileItem) {
    super(context, resource, userProfileItem);
    this.context = context;
    this.userProfileItem = userProfileItem;
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
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

    UserProfile item = userProfileItem.get(position);

    TextView tvTitle = itemLayout.findViewById(R.id.tv_edit_profile_title);
    tvTitle.setText(item.getTitle());

    TextView tvUpdate = itemLayout.findViewById(R.id.tv_edit_profile_update);
    tvUpdate.setText(item.getProfile());

    return itemLayout;
  }
}
