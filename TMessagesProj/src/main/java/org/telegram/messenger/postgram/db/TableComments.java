package org.telegram.messenger.postgram.db;

import android.database.sqlite.SQLiteDatabase;

public class TableComments {

    // Database table
    public static final String TABLE_Comments = "Comments";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UserID = "UserID";
    public static final String COLUMN_PostID = "PostID";
    public static final String COLUMN_Text = "PText";
    public static final String COLUMN_Time = "PTime";


    private static final String Table_CREATE_Comment = "create table " + TABLE_Comments
            + "(" + COLUMN_ID + " integer PRIMARY KEY ," + COLUMN_Text + " text ," +
            COLUMN_UserID + " integer ," + COLUMN_Time + " integer ," + COLUMN_PostID + " integer);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(Table_CREATE_Comment);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Table_CREATE_Comment);
        onCreate(database);
    }
}