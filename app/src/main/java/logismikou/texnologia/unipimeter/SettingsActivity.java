package logismikou.texnologia.unipimeter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    Button save_button;
    EditText search_rad, speed_lim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        save_button = findViewById(R.id.save_button);

        search_rad = findViewById(R.id.search_rad);
        speed_lim = findViewById(R.id.speed_lim);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // check for blank fields
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed_lim.getText().toString().equals("") && search_rad.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Blank speed limiter and search radius field",Toast.LENGTH_SHORT).show();
                }
                else if (speed_lim.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Blank speed limiter field",Toast.LENGTH_SHORT).show();
                }
                else if (search_rad.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Blank search radius field",Toast.LENGTH_SHORT).show();
                }
                // save new settings if conditions are met
                else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SPEED_LIMITER", speed_lim.getText().toString());
                    editor.putString("SEARCH_RADIUS", search_rad.getText().toString());
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Settings Saved",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // setText of textfield with the latest settings
        SharedPreferences.Editor editor = preferences.edit();
        speed_lim.setText(preferences.getString("SPEED_LIMITER", "30"));
        search_rad.setText(preferences.getString("SEARCH_RADIUS", "3000"));
        editor.commit();
    }
}
