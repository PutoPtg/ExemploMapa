package mma.example.com.exemplomapa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;



public class MainMenu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação
        setContentView(R.layout.menu_main); //Colocar após os dois anteriores para evitar crashes


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
}
