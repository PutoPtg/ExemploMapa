package mma.example.com.exemplomapa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import android.util.Log;
import timber.log.Timber;

import static timber.log.Timber.DebugTree;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapBoxActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener,
        PermissionsListener, MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private NavigationMapRoute navigationMapRoute;
    private DirectionsRoute currentRoute;

    private int runFlag;  //nota de recuperação para correr apenas uma vez
    private Button navButton; //botão de Navegação

    private boolean DEBUG;
    /*************************************
     *
     *      On Create
     *
     * ************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
        * DEBUG Variable
        */

        DEBUG = true;
            Timber.plant(new DebugTree());
            

        /*
         * DEBUG Variable
         */

        super.onCreate(savedInstanceState);

        runFlag = 0;
        MapBoxActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        MapBoxActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação

        //chave num ficheiro não colocado no git
        Mapbox.getInstance(MapBoxActivity.this, getString(R.string.mapbox_key));
        setContentView(R.layout.map_box_view);


        //janela do mapa - Portrait ou Landscape
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            mapView = findViewById(R.id.mapViewBox);

            if(DEBUG) {
                Context context = getApplicationContext();
                CharSequence text = "Retrato";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                TextView vt = toast.getView().findViewById(android.R.id.message);
                vt.setTextColor(Color.WHITE);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }else{

            mapView = findViewById(R.id.mapViewBoxLand);

            if(DEBUG) {
                Context context = getApplicationContext();
                CharSequence text = "Paisagem";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                TextView vt = toast.getView().findViewById(android.R.id.message);
                vt.setTextColor(Color.WHITE);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(MapBoxActivity.this);//->aqui chama o onMapReady

        //Recuperação de dados Guardados
        if((savedInstanceState != null) && (savedInstanceState.getSerializable("currentRoute") != null)
                && (savedInstanceState.getSerializable("origin") != null)
                && (savedInstanceState.getSerializable("destination") != null)
        ){
            currentRoute = (DirectionsRoute) savedInstanceState.getSerializable("currentRoute");
            originPosition = (Point) savedInstanceState.getSerializable("origin");
            destinationPosition = (Point) savedInstanceState.getSerializable("destination");
            runFlag = 1;
        }

        //botão para ir para o local presente do utilizdor
        Button mapButton;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapButton = findViewById(R.id.OE);
        }else{
            mapButton = findViewById(R.id.OEL);
        }

        //listener do botão de utilizador
        mapButton.setOnClickListener(v -> {
            if(originLocation!=null){
                setCameraPosition(originLocation);
            }else{
                Context context = getApplicationContext();
                CharSequence text = "À espera de GPS";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                TextView vt = toast.getView().findViewById(android.R.id.message);
                vt.setTextColor(Color.WHITE);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        });

        //Botão para activar a vavegação
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            navButton = findViewById(R.id.navBtn);
        }else{
            navButton = findViewById(R.id.navBtnL);
        }

        navButton.setOnClickListener(view -> {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder().directionsRoute(currentRoute).shouldSimulateRoute(true).build();
            NavigationLauncher.startNavigation(MapBoxActivity.this, options);
        });

        Timber.wtf("ONCREATE");
    }

    /*************************************
     *
     *      MAP SECTION
     *
     * ************************************/
    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        map = mapboxMap;

        enableLocation();

        if(runFlag == 1) {

            runFlag = 0;
            LatLng point = new LatLng(destinationPosition.latitude(), destinationPosition.longitude());
            destinationMarker = map.addMarker(new MarkerOptions().position(point));
            try {
                navigationMapRoute = new NavigationMapRoute( mapView, map);
                navigationMapRoute.addRoute(currentRoute);
            }catch(Exception e){
                Timber.e( "Falha ao redesenhar rota: "+ e.getMessage());
            }
        }
        map.addOnMapClickListener(MapBoxActivity.this);

        //secção do botão switch
        /*switchDark = (Switch) findViewById(R.id.DeN);

        switchDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked != false) {
                    // The toggle is enabled
                    map.setStyleUrl(Style.DARK);
                }
                else{
                    map.setStyleUrl(Style.LIGHT);
                }
            }
        });*/
    }

    @Override
    public void onMapClick(@NonNull LatLng point){

        //if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (destinationMarker != null) {
                map.removeMarker(destinationMarker);
            }
            destinationMarker = map.addMarker(new MarkerOptions().position(point));
            destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());

            try {
                originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
                getRoute(originPosition, destinationPosition);
                navButton.setEnabled(true);
            } catch (Exception sem_posIni) {
                Timber.e("Sem Posição inicial");
            }
        /*}else{
            Context context = getApplicationContext();
            CharSequence text = "Toque Desabilitado/nVire para retrato para activar.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            TextView vt = toast.getView().findViewById(android.R.id.message);
            vt.setTextColor(Color.WHITE);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }*/
    }

    private  void getRoute(Point origin,  Point destination){

        //segurança para o cálculo não ser interrompido a meio
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        NavigationRoute.builder(MapBoxActivity.this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Timber.e("FAIL! No Routes Found");
                            return;
                        }else{
                            if(response.body().routes().size() == 0){
                                Timber.e("No Routes Found");
                                return;
                            }
                            try {
                                currentRoute = response.body().routes().get(0);
                            }catch(NullPointerException e){
                                Timber.e("Bad Route, Null Pointer");
                            }
                            if(navigationMapRoute != null){
                                navigationMapRoute.removeRoute();
                            }else {
                                    navigationMapRoute = new NavigationMapRoute( mapView, map);
                            }
                            try {
                                navigationMapRoute.addRoute(currentRoute);
                            }catch(Exception e){
                                Timber.e ("AQUI : " + e.getMessage());

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                        Timber.e( "Error: %s", t.getMessage());
                        android.util.Log.e("UIUIUIUIUIUIIIII", "Sem Comunicação" + t.getMessage());
                    }
                });
        //segurança para o cálculo não ser interrompido a meio
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }



    /*************************************
     *
     * LOCATION
     *
     * ************************************/
    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(MapBoxActivity.this)){
            initializeLocationEngine();
            initializeLocationLayer();
        }
        else{
            permissionsManager = new PermissionsManager(MapBoxActivity.this);
            permissionsManager.requestLocationPermissions(MapBoxActivity.this);
        }
    }

    /*************************************
     *
     * Location Engine
     *
     * ************************************/
    private void  initializeLocationEngine(){
        try {
            locationEngine = new LocationEngineProvider(MapBoxActivity.this).obtainBestLocationEngineAvailable();
            locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            locationEngine.activate();
        }
        catch(Exception e){
            Timber.e (e);
        }
        if(locationEngine == null)
        {
            Timber.wtf ("LOCAL ENGINE NULL");
        }


        @SuppressLint("MissingPermission")
        Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation != null){
            originLocation = lastLocation;
            setCameraPosition(lastLocation);

        }
        else{
            locationEngine.addLocationEngineListener(MapBoxActivity.this);
        }
        if(locationEngine == null) {
            Timber.wtf("ONSTOP");
        }
    }

    /*************************************
     *
     * Location Layer
     *
     * ************************************/
    private void initializeLocationLayer(){
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        //recoverMarkerPoint(); //salvar os pontos anteriores
    }

    @Override
    public void onConnected() {
        locationEngine.removeLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            originLocation = location;
            setCameraPosition(location);
            Timber.wtf("Local change");
        }
    }

    /*************************************
     *
     * Camera Position
     *
     * ************************************/
    private void setCameraPosition (Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 13.0
        ));
    }

    /*************************************
     *
     * Permissions handling
     *
     * ************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }
    }

    /*++++++++++++ Activity Lifecycle Methods Section+++++++++++++++++++*/

    /**************************************
     *          START
     * ************************************/
    @Override
    public void onStart() {
        super.onStart();
        if(locationEngine != null){
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin != null){
            locationLayerPlugin.onStart();
        }

        mapView.onStart();
        Timber.wtf("ONSTART");
    }

    /**************************************
     *          RESTART
     * ************************************/
    @Override
    public void  onRestart(){
        super.onRestart();
        Timber.wtf("ONRESTART");
    }

    /**************************************
     *          RESUME
     * ************************************/
    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
    }

    /**************************************
     *          PAUSE
     * ************************************/
    @Override
    public void onPause() {
        super.onPause();
        Timber.wtf("OnPause");
        mapView.onPause();
    }

    /**************************************
     *          STOP
     * ************************************/
    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null){
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin != null){
            locationLayerPlugin.onStop();
        }
        Timber.wtf("ONSTOP");
        mapView.onStop();
    }

    /**************************************
     *          DESTROY
     * ************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine != null){
            locationEngine.deactivate();
        }
        Timber.wtf("On Destroy");
        if(destinationMarker!=null){
        map.removeMarker(destinationMarker);
        }
        if(map != null){

            map.removeOnMapClickListener(MapBoxActivity.this);
            map.clear();
        }
        if(mapView != null){
            mapView.onDestroy();
        }
        if(navigationMapRoute != null){
            navigationMapRoute.removeRoute();
        }


    }

    /**************************************
     *          LOW MEMORY
     * ************************************/
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**************************************
     *          SAVED INSTANCES
     * ************************************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("currentRoute", currentRoute);
        outState.putSerializable("origin", originPosition);
        outState.putSerializable("destination", destinationPosition);

        mapView.onSaveInstanceState(outState);
        Timber.wtf("Saved Instances");
}
}









