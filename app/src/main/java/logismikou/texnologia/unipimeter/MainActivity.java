package logismikou.texnologia.unipimeter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SharedPreferences preferences;
    DatabaseHelper unipi_meter_db;
    TextView speed_text, pos_text, pois_text;
    ImageView settings, statistics, set_pois;
    LocationManager locationManager;
    Button show_more;

    StringBuffer buffer = new StringBuffer();
    ArrayList<String> pois_in_range;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unipi_meter_db = new DatabaseHelper(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        settings = findViewById(R.id.settings);
        statistics = findViewById(R.id.statistics);
        set_pois = findViewById(R.id.set_pois);

        speed_text = findViewById(R.id.speed_text);
        pos_text = findViewById(R.id.pos_text);
        pois_text = findViewById(R.id.pois_text);

        show_more = findViewById(R.id.show_more);

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

        show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("POIS", buffer.toString());
            }
        });

        gpson();

        pois_in_range = new ArrayList<>(); //list to keep track of pois in range
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude, longitude, current_speed;

        //current position ---------------------------------------------

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        pos_text.setText(latitude+", "+longitude);

        //current speed ------------------------------------------------

        //location.getSpeed(): Get the speed if it is available, returns in meters/second over ground.
        //1 meter / second = 3.6 kilometers per hour
        current_speed = String.valueOf(location.getSpeed()*3.6); //display kh/m

        // parse to float current speed and speed limit(from saver preferences)
        float c_speed = Float.parseFloat(current_speed);
        float speed_limit = Float.parseFloat(preferences.getString("SPEED_LIMITER", "30"));

        float show_speed = Math.round(c_speed);
        speed_text.setText(String.valueOf(show_speed));

        // get current timestamp from system
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();

        if (c_speed > speed_limit)
        {
            boolean isInserted = unipi_meter_db.write_speed_alert(timestamp, Float.toString(c_speed),
                    latitude+", "+longitude);
            if(isInserted = true)
                    Toast.makeText(MainActivity.this, "Speed Alert Added", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }

        //closest POI ---------------------------------------------------

        Cursor res = unipi_meter_db.get_pois_data();
        //if(res.getCount() == 0){
        //    showMessage("Error","Data not found");
        //    return;
        //}

        float closest_poi_dist = 100000000;

        String closest_poi;
        closest_poi = "No POI in range";

        // clear buffer every time location changes
        buffer.setLength(0);

        // loop through saved POIs
        while (res.moveToNext()) {
            String loc = res.getString(3);  // get the location for every POI
            String[] separated = loc.split(",");
            double poi_latit = Double.parseDouble(separated[0]);
            double poi_longit = Double.parseDouble(separated[1].trim());

            // create a location object for the poi to measure distance from current location
            Location poi_loc = new Location("POI");
            poi_loc.setLatitude(poi_latit);
            poi_loc.setLongitude(poi_longit);

            // calculate distance between a POI and current location
            float distance = poi_loc.distanceTo(location);

            float search_rad = Float.parseFloat(preferences.getString("SEARCH_RADIUS", "3000"));

            // check if this poi is in search radius and save it
            if (distance < search_rad) {
                buffer.append("POI title: "+ res.getString(0)+"\n");
                buffer.append("Description: "+ res.getString(1)+"\n");
                buffer.append("Category: "+ res.getString(2)+"\n");
                buffer.append("Location: "+ res.getString(3)+"\n");
                buffer.append("Distance: "+ distance +" meters"+"\n");
                buffer.append("-------------------------------------\n");

                // if this pois wasn't in list before put it and write event in database
                if (!pois_in_range.contains(res.getString(0)))
                {
                    pois_in_range.add(res.getString(0));
                    // write in database that user got into radius of a pois
                    boolean isInserted = unipi_meter_db.write_poi_in_rad(timestamp, res.getString(0),
                            latitude+", "+longitude);
                }

                // check for the closest poi and save it
                if (distance < closest_poi_dist) {
                    closest_poi_dist = distance;
                    closest_poi = res.getString(0);
                }
            }
            // if this pois in out of range but still in list remove it
            else if ((distance > search_rad) && (pois_in_range.contains(res.getString(0))))
            {
                pois_in_range.remove(res.getString(0));
            }
        }
        pois_text.setText(closest_poi);
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

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
