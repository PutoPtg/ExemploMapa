package mma.example.com.exemplomapa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends Activity {

    private VariablesClass myVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação
        setContentView(R.layout.activity_main); //Colocar após os dois anteriores para evitar crashes

        //inicializar variáveis
        myVariables = new VariablesClass(0,0);

        // Zona para pedir autorizações aos utilizadores

        verifyPermitions();

    }



    private void verifyPermitions(){


        //GPS e Localização
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //se sim, tem permissão e pode arrancar o mapa
            //TODO criar e chamar a activity do menu

            Context context = getApplicationContext();
            CharSequence text = "Tem Permissões!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();


        }else{
            //não tem permissão, tem de ser pedida
            Context context = getApplicationContext();
            CharSequence text = "Não tem Permissões!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            requestPermission();



        }

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == myVariables.MY_GPS_AUTH) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                //TODO iniciar aqui
            } else {
                //ele tem de terminar mas este finish não pode ficar aqui
                //finish();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

        private void requestPermission(){

            //permissão não foi dada, pedir permissão
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myVariables.MY_GPS_AUTH);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location is necessary.");
                alertBuilder.setMessage("The app will not work without location.");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                myVariables.MY_GPS_AUTH);
                    }
                }).show();
            AlertDialog alert = alertBuilder.create();
            alert.show();


                Context context = getApplicationContext();
                CharSequence text = Integer.toString(myVariables.MY_GPS_AUTH);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myVariables.MY_GPS_AUTH);
            }

        }

}
