/***************************************************************************************************
* TODO List:
*   - criar contentor para os fragmentos
*   - criar o xml com a vista
*   - criar a lista/dicionário para guardar os fragmentos
*   - criar o índice para os títulos dos fragmentos
* *************************************************************************************************/

package mma.example.com.exemplomapa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import timber.log.Timber;

/**
 * Activity contentor para os fragmentos a apresentar
 */

public class MapBoxFragments extends AppCompatActivity{

    private MapView mapView;
    private MapboxMap map;



    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapbox_frag_container);

        Mapbox.getInstance(this, getString(R.string.mapbox_key));

        MapboxMapFragment mapFragment;

        if(savedInstanceState == null){

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            LatLng coimbra = new LatLng (40.192756, -8.4143277);

            MapboxMapOptions options = new MapboxMapOptions();
            options.styleUrl(Style.LIGHT);
            options.camera(new CameraPosition.Builder()
                    .target(coimbra)
                    .zoom(18)
                    .build());

            mapFragment = MapboxMapFragment.newInstance(options);

            transaction.add(R.id.fragment_placeholder, mapFragment, "MapBoxFragments");
            transaction.commit();

        }else{
            mapFragment = (MapboxMapFragment) getSupportFragmentManager().findFragmentByTag("MapBoxFragments");
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                map.addOnMapClickListener(this);

                // Customize map with markers, polylines, etc.

            }
        });

        Log.d("tag", "tagou");

    }

}
