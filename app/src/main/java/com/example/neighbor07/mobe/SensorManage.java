package com.example.neighbor07.mobe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorManage extends AppCompatActivity implements SensorEventListener {
    Activity activity;

    TextView title, count;

    private SensorManager sensormanager;
    private Sensor Accelerometer;
    private int ShakeCount;
    private long ShakeTime;
    private static final int SHAKE_THRESHOLD_TIME = 200;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;

    public DatabaseManage databaseManage;
    String dbName ="n_app_exr_info";
    int dbVersion = 1;
    String tag = "SQLITE";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date currentTime = new Date();
    String time=simpleDateFormat.format(currentTime);

    public String deviceUuid;

    public BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);

        title = (TextView)findViewById(R.id.title);
        count = (TextView)findViewById(R.id.count);

        sensormanager=(SensorManager)getSystemService(SENSOR_SERVICE);
        Accelerometer=(sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        databaseManage=new DatabaseManage(this, dbName, null, dbVersion);

        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];

            float gravityX = X / SensorManager.GRAVITY_EARTH;
            float gravityY = Y / SensorManager.GRAVITY_EARTH;
            float gravityZ = Z / SensorManager.GRAVITY_EARTH;

            Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;

            double squaredD = Math.sqrt(f.doubleValue());

            float gravityForce = (float)squaredD;

            if(gravityForce > SHAKE_THRESHOLD_GRAVITY){
                long currentTime = System.currentTimeMillis();

                if(ShakeTime + SHAKE_THRESHOLD_TIME > currentTime){
                    return;
                }
                ShakeTime = currentTime;
                ShakeCount++;

                count.setText(String.valueOf(ShakeCount));
                executeWhenShakeOccur();

                if(ShakeCount == 1){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("운동완료");
                    alertDialogBuilder.setMessage("자리에 앉으세요").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase db;
                            String sql;

                            String device_id=deviceUuid;
                            String exr_dt=time;
                            String exr_count=count.getText().toString();

                            db=databaseManage.getWritableDatabase();
                            sql=String.format("INSERT INTO n_app_exr_info VALUES('%s','%s','%s');",device_id,exr_dt,exr_count);
                            db.execSQL(sql);

                                activity.finishAffinity();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        }
    }

    private void executeWhenShakeOccur() {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensormanager.registerListener(this,Accelerometer,sensormanager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        sensormanager.unregisterListener(this);
    }

    public class DatabaseManage extends SQLiteOpenHelper{

        public DatabaseManage(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE n_app_exr_info(device_id PRIMARY KEY not null, "+" exr_dt not null , exr_count);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS n_app_exr_info");
            onCreate(db);
        }
    }

    public String getUniqueID(Context applicationContext){
        return deviceUuid;
    }

    public class BackPressCloseHandler{
        private long backKeyPressTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context){
            activity=context;
        }

        public void onBackPressed(){
            if(System.currentTimeMillis() > backKeyPressTime + 3000){
                backKeyPressTime = System.currentTimeMillis();
                showGuid();

                return;
            }

            if (System.currentTimeMillis() <= backKeyPressTime + 3000){
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                toast.cancel();
            }
        }

        private void showGuid() {
            toast=Toast.makeText(activity,"\'뒤로\' 버튼을 한번 더 누르시면 완전히 종료됩니다.",Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
