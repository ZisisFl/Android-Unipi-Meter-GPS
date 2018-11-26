package logismikou.texnologia.unipimeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "unipi_meter.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS POIs(pois_title VARCHAR,description VARCHAR, category VARCHAR, coordinates VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS POIs");
        onCreate(db);
    }

    public boolean write_pois_data(String pois_title, String description, String category, String coordinates){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pois_title", pois_title);
        contentValues.put("description", description);
        contentValues.put("category", category);
        contentValues.put("coordinates", coordinates);
        long result = db.insert("POIs", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor get_pois_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM POIs",null);
        return res;
    }
}
