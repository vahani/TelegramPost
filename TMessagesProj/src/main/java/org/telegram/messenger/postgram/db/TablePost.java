package org.telegram.messenger.postgram.db;

import android.database.sqlite.SQLiteDatabase;

public class TablePost {

    // Database table
    public static final String TABLE_Post = "Posts";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UserID = "UserID";
    public static final String COLUMN_Text = "PText";
    public static final String COLUMN_Time = "PTime";
    public static final String COLUMN_MyLike = "mylike";
    public static final String COLUMN_LikeCount = "LikeCount";
    public static final String COLUMN_CommentCount = "CommentCount";
    public static final String COLUMN_MediaList = "MediaList";

    private static final String Table_CREATE = "create table " + TABLE_Post
            + "(" + COLUMN_ID + " integer PRIMARY KEY ," + COLUMN_Text + " text ," +
            COLUMN_UserID + " integer ," + COLUMN_Time + " integer ," + COLUMN_LikeCount + " integer ," + COLUMN_CommentCount + " integer ," + COLUMN_MediaList + " text, " + COLUMN_MyLike + " integer);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(Table_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Table_CREATE);
        onCreate(database);
    }
}