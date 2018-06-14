package mma.example.com.exemplomapa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


    public class MainMenu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação
        setContentView(R.layout.menu_main); //Colocar após os dois anteriores para evitar crashes
    }
}
