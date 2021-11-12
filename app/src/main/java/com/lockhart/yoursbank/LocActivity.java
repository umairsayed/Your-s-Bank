package com.lockhart.yoursbank;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.FragmentActivity;

        import com.example.yoursbank.R;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

public class LocActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng loc = new LatLng(18.9682, 72.8311);
        map.addMarker(new MarkerOptions().position(loc).title("Your's Bank"));
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));


    }
}