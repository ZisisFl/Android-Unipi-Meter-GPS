package logismikou.texnologia.unipimeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "unipi_meter.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS POIs(pois_title VARCHAR,description VARCHAR, category VARCHAR, coordinates VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS SpeedAlerts(timestamp VARCHAR,speed VARCHAR, location VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS POI_in_rad(timestamp VARCHAR,poi_title VARCHAR, location VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS POIs");
        db.execSQL("DROP TABLE IF EXISTS SpeedAlerts");
        db.execSQL("DROP TABLE IF EXISTS POI_in_rad ");
        onCreate(db);
    }

    //used to save a new POI
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

    // delete a saved POI
    public Integer deleteData(String poi_title){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("POIs", "pois_title = ?",new String[] {poi_title});
    }

    // view all saved POIs
    public Cursor get_pois_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM POIs",null);
        return res;
    }

    // save in db a speeding alert
    public boolean write_speed_alert(String timestamp, String speed, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", timestamp);
        contentValues.put("speed", speed);
        contentValues.put("location", location);
        long result = db.insert("SpeedAlerts", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // save in db the event of getting within a POI's range
    public boolean write_poi_in_rad(String timestamp, String poi_title, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", timestamp);
        contentValues.put("poi_title", poi_title);
        contentValues.put("location", location);
        long result = db.insert("POI_in_rad", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // functions for the statistics ----------------------------

    public Cursor get_highest_speed(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT MAX(speed) FROM SpeedAlerts",null);
        return res;
    }

    public long count_speeding()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, "SpeedAlerts");
    }

    public Cursor most_visited(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT poi_title, COUNT(poi_title) AS value_occurrence FROM POI_in_rad GROUP BY poi_title ORDER BY `value_occurrence` DESC\n" +
                " LIMIT 1",null);
        return res;
    }

    public Cursor category_most_pois(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT category, COUNT(category) AS value_occurrence FROM POIs GROUP BY category ORDER BY `value_occurrence` DESC\n" +
                "    LIMIT 1 ",null);
        return res;
    }

    public Cursor n_pois(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(DISTINCT pois_title) FROM POIs",null);
        return res;
    }

    public Cursor n_categories(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(DISTINCT category) FROM POIs",null);
        return res;
    }
}
