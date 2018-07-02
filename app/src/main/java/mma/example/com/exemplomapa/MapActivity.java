package mma.example.com.exemplomapa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapActivity extends AppCompatActivity {


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
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

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


        @SuppressWarnings("deprecation")
        RotationGestureOverlay myRotations = new RotationGestureOverlay(ctx, map);
        myRotations.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(myRotations);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        Log.i("Methods", "onCreate");



    }

    @Override
    public void onStart(){
        super.onStart();
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        usrZoom = (double) Double.longBitsToDouble(pref.getLong("Zoom", 0));

        double temp = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(40.192756)));
        double temp2 = (double) Double.longBitsToDouble(pref.getLong("UserLon", Double.doubleToLongBits(-8.4143277)));

        GeoPoint temp3 = new GeoPoint(temp, temp2);


        mapController.setCenter(temp3);

        mapController = map.getController();
        mapController.setZoom(usrZoom);

        Log.i("Methods", "On Start");

        Log.i("OnStart Lat", Double.toString(temp));
        Log.i("OnStart",Double.toString(temp2));



    }
    @Override
    public void onResume(){
        super.onResume();
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        usrZoom = (double) Double.longBitsToDouble(pref.getLong("Zoom", 0));

        double temp = (double) Double.longBitsToDouble(pref.getLong("UserLat", Double.doubleToLongBits(40.192756)));
        double temp2 = (double) Double.longBitsToDouble(pref.getLong("UserLon", Double.doubleToLongBits(-8.4143277)));

        GeoPoint temp3 = new GeoPoint(temp, temp2);

        mapController.setCenter(temp3);

        mapController = map.getController();
        mapController.setZoom(usrZoom);
        Log.i("Methods", "on Resume");
        mapController.setZoom(usrZoom);



    }
    @Override
    public void onPause(){
        super.onPause();
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //double usrZoom;

        //userLocation = new GeoPoint(mLocationOverlay.getMyLocation().getLatitude(),mLocationOverlay.getMyLocation().getLongitude());


        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up

        double tEmp0 = map.getMapCenter().getLatitude();
        double tEmp1 = map.getMapCenter().getLongitude();

        usrZoom = map.getZoomLevelDouble();

        Log.i("Methods", "onPause");
        Log.i("Do MAPA Lat:", Double.toString(tEmp0));
        Log.i("Do MAPA Lon:", Double.toString(tEmp1));
        Log.i("Zoom:", Double.toString(usrZoom));



        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("Zoom", Double.doubleToRawLongBits(usrZoom));
        editor.putLong("UserLat",Double.doubleToRawLongBits(tEmp0));
        editor.putLong("UserLon",Double.doubleToRawLongBits(tEmp1));
        editor.commit();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Configuration.getInstance().save(ctx, prefs);

    }
}
