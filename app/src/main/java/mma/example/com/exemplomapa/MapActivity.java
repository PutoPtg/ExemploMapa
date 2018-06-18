package mma.example.com.exemplomapa;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapActivity extends Activity {


    private IMapController mapController;
    private MapView map = null;
    private double usrZoom;
    private long usrZoomL;
    private GeoPoint startPoint;
    private GeoPoint userLocation;
    private MyLocationNewOverlay mLocationOverlay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        usrZoom = Double.longBitsToDouble(prefs.getLong("Zoom", 0));

        //inflate and create the map
        setContentView(R.layout.map_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(usrZoom);
        startPoint = new GeoPoint(40.192756, -8.4143277);
        mapController.setCenter(startPoint);

        RotationGestureOverlay myRotations = new RotationGestureOverlay(this, map);
        myRotations.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(myRotations);


        usrZoom = (double)map.getZoomLevelDouble();

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        Log.i("Methods", "onCreate");



    }

    @Override
    public void onStart(){
        super.onStart();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        usrZoom = (double) Double.longBitsToDouble(pref.getLong("Zoom", 0));

       // double temp = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(0)));
        //double temp2 = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(0)));

        //userLocation.setCoords(temp, temp2);
        mapController = map.getController();
        mapController.setZoom(usrZoom);

        Log.i("Methods", "On Start");

    }
    @Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        usrZoom = (double) Double.longBitsToDouble(pref.getLong("Zoom", 0));

        //double temp = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(0)));
        //double temp2 = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(0)));

        //userLocation.setCoords(temp, temp2);
        mapController = map.getController();
        mapController.setZoom(usrZoom);
        Log.i("Methods", "on Resume");
        mapController.setZoom(usrZoom);

    }
    @Override
    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        double usrZoom;

        //userLocation = new GeoPoint(mLocationOverlay.getMyLocation().getLatitude(),mLocationOverlay.getMyLocation().getLongitude());


        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        usrZoom = map.getZoomLevelDouble();
        Log.i("Methods", "onPause");



        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("Zoom", Double.doubleToRawLongBits(usrZoom));
        //editor.putLong("UserLat",Double.doubleToRawLongBits(userLocation.getLatitude()));
        //editor.putLong("UserLon",Double.doubleToRawLongBits(userLocation.getLongitude()));
        editor.commit();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().save(this, prefs);

    }
}
