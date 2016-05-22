package com.borawski.rallylightcontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by harrisborawski on 5/11/16.
 */
public class PresetContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PresetEntry.TABLE_NAME + " (" +
                    PresetEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    PresetEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    PresetEntry.COLUMN_NAME_PITCH + TEXT_TYPE + COMMA_SEP +
                    PresetEntry.COLUMN_NAME_YAW + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PresetEntry.TABLE_NAME;

    public PresetContract() {}

    public static String[] getProjection() {
        String[] proj = {
                PresetEntry._ID,
                PresetEntry.COLUMN_NAME_NAME,
                PresetEntry.COLUMN_NAME_PITCH,
                PresetEntry.COLUMN_NAME_YAW
        };
        return proj;
    }

    public static abstract class PresetEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PITCH = "pitch";
        public static final String COLUMN_NAME_YAW = "yaw";
    }

    public static class PresetDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Preset.db";

        public PresetDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
