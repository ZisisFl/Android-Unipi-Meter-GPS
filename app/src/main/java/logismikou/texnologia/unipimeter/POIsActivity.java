package logismikou.texnologia.unipimeter;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class POIsActivity extends AppCompatActivity {
    DatabaseHelper unipi_meter_db;
    EditText pois_title, description, category, coord_x, coord_y;
    Button save_button, view_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pois);

        unipi_meter_db = new DatabaseHelper(this);

        save_button = findViewById(R.id.save_button);
        view_button = findViewById(R.id.view_button);

        pois_title = findViewById(R.id.pois_title);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        coord_x = findViewById(R.id.coord_x);
        coord_y = findViewById(R.id.coord_y);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = coord_x.getText().toString() +", "+ coord_y.getText().toString();
                boolean isInserted = unipi_meter_db.write_pois_data(pois_title.getText().toString(),
                        description.getText().toString(),
                        category.getText().toString(), coordinates);
                if(isInserted = true)
                    Toast.makeText(POIsActivity.this, "POIs Added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(POIsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = unipi_meter_db.get_pois_data();
                if(res.getCount() == 0){
                    showMessage("Error","Data not found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("POIs title: "+ res.getString(0)+"\n");
                    buffer.append("Description: "+ res.getString(1)+"\n");
                    buffer.append("Category: "+ res.getString(2)+"\n");
                    buffer.append("Location: "+ res.getString(3)+"\n");
                    buffer.append("-------------------------------------\n");
                }
                showMessage("POIS", buffer.toString());

            }
        });
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
