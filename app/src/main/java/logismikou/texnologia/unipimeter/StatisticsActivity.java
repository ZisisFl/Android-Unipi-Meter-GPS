package logismikou.texnologia.unipimeter;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends AppCompatActivity {

    DatabaseHelper unipi_meter_db;

    TextView n_pois, n_categories, max_speed, count_speeding, most_visited, category_most;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        unipi_meter_db = new DatabaseHelper(this);

        n_pois = findViewById(R.id.n_pois);
        n_categories = findViewById(R.id.n_categories);
        max_speed = findViewById(R.id.max_speed);
        count_speeding = findViewById(R.id.count_speeding);
        most_visited = findViewById(R.id.most_visited);
        category_most = findViewById(R.id.category_most);

        //1
        Cursor q1 = unipi_meter_db.n_pois();
        if (q1.getCount() != 0){
            q1.moveToFirst();
            n_pois.setText(q1.getString(0));
        }
        else
            n_pois.setText("No data yet");

        //2
        Cursor q2 = unipi_meter_db.n_categories();
        if (q2.getCount() != 0) {
            q2.moveToFirst();
            n_categories.setText(q2.getString(0));
        }
        else
            n_categories.setText("No data yet");

        //3
        Cursor q3 = unipi_meter_db.get_highest_speed();
        if (q3.getCount() != 0) {
            q3.moveToFirst();
            max_speed.setText(q3.getString(0));
        }
        else
            max_speed.setText("No data yet");

        //4
        String speeding_counter = String.valueOf(unipi_meter_db.count_speeding());
        count_speeding.setText(speeding_counter);

        //5
        Cursor q5 = unipi_meter_db.most_visited();
        if (q5.getCount() != 0) {
            q5.moveToFirst();
            most_visited.setText(q5.getString(0));
        }
        else
            most_visited.setText("No data yet");


        //6
        Cursor q6 = unipi_meter_db.category_most_pois();
        if (q6.getCount() != 0) {
            q6.moveToFirst();
            category_most.setText(q6.getString(0));
        }
        else
            category_most.setText("No data yet");
    }
}
