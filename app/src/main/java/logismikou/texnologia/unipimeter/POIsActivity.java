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
    Button save_button, view_button, delete_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pois);

        unipi_meter_db = new DatabaseHelper(this);

        save_button = findViewById(R.id.save_button);
        view_button = findViewById(R.id.view_button);
        delete_button = findViewById(R.id.delete_button);

        pois_title = findViewById(R.id.pois_title);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        coord_x = findViewById(R.id.coord_x);
        coord_y = findViewById(R.id.coord_y);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if input is double number
                int status = 1;
                try {
                    Double.parseDouble(coord_x.getText().toString());
                    Double.parseDouble(coord_y.getText().toString());
                } catch (NumberFormatException e) {
                    status = 0;
                }
                if (status == 1){
                    // check if there are black fields
                    if (pois_title.getText().toString().equals("") || description.getText().toString().equals("") ||
                            category.getText().toString().equals("") || coord_x.getText().toString().equals("") ||
                            coord_y.getText().toString().equals("")){
                        Toast.makeText(POIsActivity.this, "Blank field", Toast.LENGTH_SHORT).show();
                    }
                    // if conditions are met save in db
                    else{
                        String coordinates = coord_x.getText().toString() +", "+ coord_y.getText().toString();
                        boolean isInserted = unipi_meter_db.write_pois_data(pois_title.getText().toString(),
                                description.getText().toString(),
                                category.getText().toString(), coordinates);
                        if(isInserted = true)
                            Toast.makeText(POIsActivity.this, "POI Added", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(POIsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(POIsActivity.this, "Coordinates must be a double", Toast.LENGTH_SHORT).show();
            }
        });

        // view all pois save in db
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
                    buffer.append("POI title: "+ res.getString(0)+"\n");
                    buffer.append("Description: "+ res.getString(1)+"\n");
                    buffer.append("Category: "+ res.getString(2)+"\n");
                    buffer.append("Location: "+ res.getString(3)+"\n");
                    buffer.append("-------------------------------------\n");
                }
                showMessage("POIs", buffer.toString());

            }
        });

        // delete a poi based on poi_title
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deletedRows = unipi_meter_db.deleteData(pois_title.getText().toString());
                if(deletedRows == 0)
                    Toast.makeText(POIsActivity.this, "There isn't such a POI", Toast.LENGTH_SHORT).show();
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
