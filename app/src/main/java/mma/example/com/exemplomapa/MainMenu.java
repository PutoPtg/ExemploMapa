package mma.example.com.exemplomapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Implements a simple menu for switching between maps
 *
 */
public class MainMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);


        final Button mapbutton = findViewById(R.id.map_btn);
        mapbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent mapAct = new Intent(MainMenu.this, MapActivity.class);
                startActivity(mapAct);
            }
        });

        final Button dataButton = findViewById(R.id.values_btn);
        dataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent mapAct2 = new Intent(MainMenu.this, MapBoxActivity.class);
                startActivity(mapAct2);
            }
        });

        final Button fragButton = findViewById(R.id.frag_btn);
        fragButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent mapAct3 = new Intent(MainMenu.this, MapBoxFragments.class);
                startActivity(mapAct3);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
