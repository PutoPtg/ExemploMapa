package mma.example.com.exemplomapa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private VariablesClass myVariables;
    private Switch switchGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação
        setContentView(R.layout.activity_main); //Colocar após os dois anteriores para evitar crashes

        switchGPS = (Switch)findViewById(R.id.switch1);
        //inicializar variáveis
        myVariables = new VariablesClass(0,0);



        // Zona para pedir autorizações aos utilizadores

        //passo 1 - Verificar a versão do SDK
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermitions();
        }else{
            //TODO LAUNCH MENU
        }
    }



    private void verifyPermitions(){


        //passo 2 - Verificar permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //mensagem de toast que indica ao utilizador que a permissão foi dada
            Context context = getApplicationContext();
            CharSequence text = "Localização ATIVA";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            //se sim, tem permissão e pode arrancar o mapa

            switchGPS.setChecked(true);
            //TODO criar e chamar a activity do menu


        }else{
            //não tem permissão, tem de ser pedida
            switchGPS.setChecked(false);
            Context context = getApplicationContext();
            CharSequence text = "A aplicação não tem acesso à localização.\n Por favor ligue a localização para a aplicação funcionar.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        requestPermission();
                    } else {
                        //requestPermission();
                    }
                }
            });




        }

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == myVariables.MY_GPS_AUTH) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //utilizador negou permissão
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if(!showRationale){
                    //permissão negada e não voltar a perguntar
                    switchGPS.setChecked(false);
                    switchGPS.setEnabled(false);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Localização Negada.");
                    alertBuilder.setMessage("A aplicação não funciona sem a localização.\nTerá de fornecer permissões nas opções a partir de agora.");
                    alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).show();
                AlertDialog alert = alertBuilder.create();
                alert.show();

                }else{
                        //permissão negada
                        switchGPS.setChecked(false);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Localização Negada.");
                        alertBuilder.setMessage("A aplicação não funciona sem a localização.\nTem a certeza que deseja continuar?.");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Context context = getApplicationContext();
                                CharSequence text = "Localização NEGADA";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                v.setTextColor(Color.RED);
                                toast.show();
                            }
                        }).show();
                        alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        myVariables.MY_GPS_AUTH);
                            }
                        }).show();

                }

            } else {
                //TODO launch menu
            }
        }
    }

        private void requestPermission(){

            //permissão não foi dada, pedir permissão
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myVariables.MY_GPS_AUTH);

                /*AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
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
            alert.show();*/


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
