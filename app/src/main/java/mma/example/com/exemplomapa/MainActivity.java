package mma.example.com.exemplomapa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private Switch switchSDC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //esconde o título da janela
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //remove barra de notificação
        setContentView(R.layout.activity_main); //Colocar após os dois anteriores para evitar crashes

        //inicializar variáveis
        myVariables = new VariablesClass(0, 1, 2);
        switchGPS = (Switch) findViewById(R.id.switch1);
        switchSDC = (Switch) findViewById(R.id.switch2);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        double Zoom = 12;
        editor.putLong("Zoom", Double.doubleToRawLongBits(Zoom));
        editor.commit();

        //passo 1 - Verificar a versão do SDK
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermitions();
        } else {
            switchGPS.setSaveEnabled(false);
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);
        }
    }


    private void verifyPermitions() {

        //passo 2 - Verificar permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //mensagem de toast que indica ao utilizador que a permissão foi dada

                Context context = getApplicationContext();
                CharSequence text = "Localização e Escrita\nATIVAS";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();


            //se sim, tem permissão e pode arrancar o mapa

            switchGPS.setChecked(true);
            switchSDC.setChecked(true);
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);


        } else {
            //se não, foi negada a ambos?
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)&&
                        !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, R.string.toast_permissions, duration);
                    toast.show();
                }
            }



                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    //não tem permissão, tem de ser pedida
                    switchGPS.setChecked(false);
                    switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                               // The toggle is enabled
                                requestPermission(0);
                            }
                        }
                    });
                }else {
                    switchGPS.setChecked(true);
                    switchGPS.setEnabled(false);
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //não tem permissão, tem de ser pedida
                    switchSDC.setChecked(false);
                    switchSDC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                // The toggle is enabled
                                requestPermission(1);
                            }
                        }
                    });
                }else {
                    switchSDC.setChecked(true);
                    switchSDC.setEnabled(false);
                }
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 5 - Tratar da resposta
        if (requestCode == myVariables.MY_GPS_AUTH) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //utilizador negou permissão
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (!showRationale) {
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

                } else {

                    //permissão negada
                    switchGPS.setChecked(false);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Localização Negada.");
                    alertBuilder.setMessage("A aplicação não funciona sem a localização.\nTem a certeza que deseja continuar?");
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
                    });
                    alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    myVariables.MY_GPS_AUTH);
                        }
                    }).show();


                }

            }

        }else {
            if (requestCode == myVariables.MY_SD_AUTH) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //utilizador negou permissão
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (!showRationale) {
                        //permissão negada e não voltar a perguntar
                        switchSDC.setChecked(false);
                        switchSDC.setEnabled(false);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Escrita Negada.");
                        alertBuilder.setMessage("A aplicação não funciona sem a escrita.\nTerá de fornecer permissões nas opções a partir de agora.");
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

                    } else {

                        //permissão negada
                        switchSDC.setChecked(false);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Escrita Negada.");
                        alertBuilder.setMessage("A aplicação não funciona sem a Escrita.\nTem a certeza que deseja continuar?");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Context context = getApplicationContext();
                                CharSequence text = "Escrita NEGADA";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                v.setTextColor(Color.RED);
                                toast.show();
                            }
                        });
                        alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        myVariables.MY_SD_AUTH);
                            }
                        }).show();
                    }

                }
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);
        }

    }

    private void requestPermission(int i) {
        //4 - Pedir permissão

        if (i == 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myVariables.MY_GPS_AUTH);
        }
        if (i == 1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, myVariables.MY_SD_AUTH);
        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermitions();
        } else {
            switchGPS.setSaveEnabled(false);
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);
        }


    }

    @Override
    protected void onStart() {

        super.onStart();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermitions();
        } else {
            switchGPS.setSaveEnabled(false);
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);
        }


    }

    @Override
    protected void onResume() {

        super.onResume();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermitions();
        } else {
            switchGPS.setSaveEnabled(false);
            Intent menuActivity = new Intent(this, MainMenu.class);
            startActivity(menuActivity);
        }
    }
}
