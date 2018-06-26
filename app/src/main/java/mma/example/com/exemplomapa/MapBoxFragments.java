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
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

public class MapBoxFragments extends AppCompatActivity{

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.mapbox_frag_container);

        Timber.wtf("Fragment Bucket ON");
    }
}
