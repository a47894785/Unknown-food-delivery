package fcu.app.unknownfooddelivery;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

  TextView tvCurrentAddress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    tvCurrentAddress = view.findViewById(R.id.home_frag_address);
    ListView lvAlbum = getActivity().findViewById(R.id.lvAlbum);
    ArrayList<AlbumItem> albumList = new ArrayList<AlbumItem>();
    ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, albumList);

    albumList.add(new AlbumItem(R.drawable.logo,"台北之旅", "2019-01-01"));
    lvAlbum.setAdapter(adapter);
    if (getArguments() != null) {
      String currentAddress = this.getArguments().getString("address");
      tvCurrentAddress.setText(currentAddress);
    }

    return view;
  }
  public class AlbumItem {

    private int imgResId;
    private String place;
    private String date;

    public AlbumItem(int imgResId, String place, String date) {
      this.imgResId = imgResId;
      this.place = place;
      this.date = date;
    }

    public int getImgResId() {
      return imgResId;
    }

    public String getPlace() {
      return place;
    }

    public String getDate() {
      return date;
    }
  }

}