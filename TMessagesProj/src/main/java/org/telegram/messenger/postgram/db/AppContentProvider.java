package org.telegram.messenger.postgram.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AppContentProvider extends ContentProvider {

    // database
    private DBHelper database;

    // used for the UriMacher
    private static final int Posts = 10;
    private static final int Comments = 11;
    private static final int Comments_Simple = 12;

    private static final String AUTHORITY = "postgram.AppContentProvider";

    private static final String Posts_PATH = "Posts";
    private static final String Comments_PATH = "Comments";
    private static final String Comments_Simple_PATH = "Comments_Simple";

    public static final Uri Posts_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Posts_PATH);
    public static final Uri Comments_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Comments_PATH);
    public static final Uri Comments_Simple_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Comments_Simple_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, Posts_PATH, Posts);
        sURIMatcher.addURI(AUTHORITY, Comments_PATH, Comments);
        sURIMatcher.addURI(AUTHORITY, Comments_Simple_PATH, Comments_Simple);
    }

    @Override
    public boolean onCreate() {
        database = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case Posts:
                queryBuilder.setTables(TablePost.TABLE_Post);
                break;
            case Comments_Simple:
                queryBuilder.setTables(TableComments.TABLE_Comments);
                break;
            case Comments:
                Cursor cursor = null;
                SQLiteDatabase db = database.getWritableDatabase();
                // /
                String Fprojection = "";
                for (int i = 0; i < projection.length; i++)
                    Fprojection += "," + projection[i];

                Fprojection = Fprojection.substring(1);
                // /
                String FselectionArgs = "";
                for (int i = 0; i < selectionArgs.length; i++)
                    FselectionArgs += "," + selectionArgs[i];

                FselectionArgs = FselectionArgs.substring(1);
                // /
                cursor = db
                        .rawQuery("SELECT " + Fprojection + " FROM "
                                + TablePost.TABLE_Post + " WHERE "
                                + TablePost.COLUMN_ID + "="
                                + selection + " UNION ALL  " + "SELECT "
                                + FselectionArgs + " FROM "
                                + TableComments.TABLE_Comments + " WHERE "
                                + TableComments.COLUMN_PostID + "="
                                + selection, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case Posts:
                try {
                    id = sqlDB.insertOrThrow(TablePost.TABLE_Post, null, values);
                } catch (SQLException e) {
                    id = sqlDB.update(TablePost.TABLE_Post, values, TablePost.COLUMN_ID + "=?",
                            new String[]{values.get(TablePost.COLUMN_ID).toString()});
                }
                break;
            case Comments:
                id = sqlDB.replace(TableComments.TABLE_Comments, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return Uri.parse(uri + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case Posts:
                rowsDeleted = sqlDB.delete(TablePost.TABLE_Post, selection,
                        selectionArgs);
                break;
            case Comments:
                rowsDeleted = sqlDB.delete(TableComments.TABLE_Comments, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case Posts:
                rowsUpdated = sqlDB.update(TablePost.TABLE_Post, values, selection,
                        selectionArgs);
                break;
            case Comments:
                rowsUpdated = sqlDB.update(TableComments.TABLE_Comments, values, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}