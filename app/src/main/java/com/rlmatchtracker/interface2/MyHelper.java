package com.rlmatchtracker.interface2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyHelper extends SQLiteOpenHelper { //Helperclass
    private Context context;
    private static final String CREATE_TABLE =
            "CREATE TABLE "+
                    Constants.TABLE_NAME + " (" +
                    Constants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.IMAGE + " TEXT, " +
                    Constants.WL + " TEXT, " +
                    Constants.MODE + " TEXT, " +
                    Constants.TEAM + " TEXT, " +
                    Constants.OPP + " TEXT, " +
                    Constants.POINTS + " TEXT, " +
                    Constants.GOALS + " TEXT, " +
                    Constants.ASSISTS + " TEXT, " +
                    Constants.SHOTS + " TEXT, " +
                    Constants.SAVES + " TEXT, " +
                    Constants.TIME + " TEXT);" ;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME;

    public MyHelper(Context context){
        super (context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "exception onCreate() db", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // CAN MODIFY, DELETE THE TABLE, ADD/DELETE COLUMNS AND DROP TABLES
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
    public void resetTable(SQLiteDatabase db){
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
    public void compressTable(SQLiteDatabase db){
        db.execSQL("VACUUM");
    }
}
