package org.telegram.messenger.postgram.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Postgram.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        TablePost.onCreate(database);
        TableComments.onCreate(database);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        TablePost.onUpgrade(database, oldVersion, newVersion);
        TableComments.onUpgrade(database, oldVersion, newVersion);
    }
}