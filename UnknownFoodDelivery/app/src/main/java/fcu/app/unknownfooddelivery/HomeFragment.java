package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

public class HomeFragment extends Fragment {

  TextView tvCurrentAddress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    tvCurrentAddress = view.findViewById(R.id.home_frag_address);

    if (getArguments() != null) {
      String currentAddress = this.getArguments().getString("address");
      tvCurrentAddress.setText(currentAddress);
    }

    return view;
  }
}