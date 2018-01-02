package mil.nga.giat.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.app.R;
import mil.nga.mgrs.gzd.MGRSTileProvider;
import mil.nga.mgrs.wgs84.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap map;
    private TextView mgrsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mgrsLabel = (TextView) findViewById(R.id.mgrs);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        TileOverlay mgrsOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(new MGRSTileProvider(getApplicationContext())));

        map.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {
        com.google.android.gms.maps.model.LatLng center = map.getCameraPosition().target;
        MGRS mgrs = MGRS.from(new LatLng(center.latitude, center.longitude));
        mgrsLabel.setText(mgrs.format(4));
    }
}
