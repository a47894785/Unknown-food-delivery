/*package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.core.View;

import java.util.List;

public class AlbumArrayAdapter extends ArrayAdapter<HomeFragment.AlbumItem> {

    private Context context;
    private List<HomeFragment.AlbumItem> albumItems;

    public AlbumArrayAdapter(@NonNull Context context, int resource, List<HomeFragment.AlbumItem> albumItems) {
        super(context, resource, albumItems);
        this.context = context;
        this.albumItems = albumItems;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(this.context);
        LinearLayout itemLayout = null;

        if (convertView == null) {
            itemLayout = (LinearLayout) inflater.inflate(R.layout.listitem, null);
        } else {
            itemLayout = (LinearLayout) convertView;
        }

        HomeFragment.AlbumItem item = albumItems.get(position);

        ImageView iv = itemLayout.findViewById(R.id.lock);
        iv.setImageResource(item.getImgResId());

        TextView tvPlace = itemLayout.findViewById(R.id.Rname);
        tvPlace.setText(item.getPlace());

        TextView tvDate = itemLayout.findViewById(R.id.Rnumber);
        tvDate.setText(item.getDate());

        return itemLayout;
    }
}
*/