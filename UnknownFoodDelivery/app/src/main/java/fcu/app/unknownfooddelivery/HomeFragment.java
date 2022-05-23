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
  /*public static final String ALBUM_NO = "album_no";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    tvCurrentAddress = view.findViewById(R.id.home_frag_address);


    ListView lvAlbum = getActivity().findViewById(R.id.imageView17);

    ArrayList<String> albumList = new ArrayList<String>();

    /*albumList.add("台北之旅");
    albumList.add("台中之旅");
    albumList.add("高雄之旅");*/

    /*ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, albumList);

    lvAlbum.setAdapter(adapter);


    if (getArguments() != null) {
      String currentAddress = this.getArguments().getString("address");
      tvCurrentAddress.setText(currentAddress);
    }

    return view;
  }*/
}