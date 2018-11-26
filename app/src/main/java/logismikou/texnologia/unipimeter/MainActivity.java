package logismikou.texnologia.unipimeter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView speed_text, pos_text, pois_text;
    ImageView settings, statistics, set_pois;
    LocationManager locationManager;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        db = openOrCreateDatabase("UnipiMeter", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS USER(user_id VARCHAR,user_name VARCHAR, user_tel VARCHAR);");

        settings = findViewById(R.id.settings);
        statistics = findViewById(R.id.statistics);
        set_pois = findViewById(R.id.set_pois);

        speed_text = findViewById(R.id.speed_text);
        pos_text = findViewById(R.id.pos_text);
        pois_text = findViewById(R.id.pois_text);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });

        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,StatisticsActivity.class));
            }
        });
        set_pois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,POIsActivity.class));
            }
        });

        gpson();
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude, longitude, current_speed;

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        pos_text.setText(latitude+", "+longitude);

        //location.getSpeed(): Get the speed if it is available, returns in meters/second over ground.
        //1 meter / second = 3.6 kilometers per hour
        current_speed = String.valueOf(location.getSpeed()*3.6); //display kh/m
        speed_text.setText(current_speed);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void gpson(){
        if(ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,
                    0,this);
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},6);
        }
    }
}
