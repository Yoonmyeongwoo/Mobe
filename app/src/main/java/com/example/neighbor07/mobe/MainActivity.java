package com.example.neighbor07.mobe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText name, rank;

    Button Login;

    TextView Title;

    public String deviceUuid;

    public DatabaseManage databaseManage;
    String dbName="n_app_user_info";
    int dbVersion=1;
    String tag="SQLITE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Title = (TextView)findViewById(R.id.Title);

        name=(EditText)findViewById(R.id.name);
        rank=(EditText)findViewById(R.id.rank);

        databaseManage=new DatabaseManage(this, dbName, null, dbVersion);

        Login=(Button)findViewById(R.id.Login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name == null){
                    Toast.makeText(MainActivity.this,"이름을 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                if(rank == null){
                    Toast.makeText(MainActivity.this,"직급을 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                if(name != null && rank != null){
                    SharedPreferences sharedPreferences = getSharedPreferences("autologin",MainActivity.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.commit();

                    SQLiteDatabase db;
                    String sql;

                    switch (v.getId()){
                        case R.id.Login:
                            String user_name=name.getText().toString();
                            String user_rank=rank.getText().toString();
                            String device_id=deviceUuid;

                            Log.d("name","name ="+user_name);
                            Log.d("rank","rank ="+user_rank);
                            Log.d("uuid","uuid ="+device_id);

                            db=databaseManage.getReadableDatabase(); // 읽기 전용 DB
                            sql=String.format("INSERT INTO n_app_user_info VALUES('%s','%s','%s');",user_name,user_rank,device_id);
                            db.execSQL(sql);

                            Log.d("database","db="+db);

                            break;
                    }
                    Intent intent = new Intent(MainActivity.this, SensorManage.class);
                    startActivity(intent);
                }
            }
        });
    }

    public class DatabaseManage extends SQLiteOpenHelper {

        public DatabaseManage(MainActivity mainActivity, String dbName, Object o, int dbVersion) {
            super(mainActivity, dbName, (SQLiteDatabase.CursorFactory) o, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE n_app_user_info(user_name TEXT not null, user_rank TEXT not null, device_id TEXT PRIMARY KEY not null);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS n_app_user_info");
            onCreate(db);
        }
    }
    public String getUniqueID(Context applicationContext){
        return deviceUuid;
    }
}
