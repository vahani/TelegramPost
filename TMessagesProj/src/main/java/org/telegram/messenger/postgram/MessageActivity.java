package org.telegram.messenger.postgram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import ir.android.telegram.post.R;
import org.telegram.messenger.postgram.adapters.MessageAdapter;
import org.telegram.messenger.postgram.classes.Comment;
import org.telegram.messenger.postgram.db.AppContentProvider;
import org.telegram.messenger.postgram.db.TableComments;
import org.telegram.messenger.postgram.db.TablePost;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.postgram.tools.Public_Functions;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;


public class MessageActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    RequestQueue mRequestQueue;
    MenuItem refresh;
    ListView message_listview;
    MessageAdapter adapter;
    public long MessageID = 0;
    int mystatus = -1;


    private AlertDialog fragment;
    private ProgressDialog progress;


    // /// Emoji
    private TextView emojiconEditText;
    private ImageView emojiButton;
    private ImageView submitButton;
    String str_comment;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String CID = "0";
    private String UserID = "0";

    // /////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout_message);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.holo_blue_light);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Get_New_Comments();
                    }
                });
        //mSwipeRefreshLayout.setRefreshing(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MessageID = extras.getLong("id");
            UserID = String.valueOf(extras.getLong("MOwnerID"));
            mystatus = extras.getInt("mystatus");
//            Public_Data.thisMessageOwnerID = extras.getLong("MessageOwnerID");
        }


        mRequestQueue = Volley.newRequestQueue(getBaseContext());

        getSupportLoaderManager().initLoader(0, null, this);

        String[] from = new String[]{};
        // Fields on the UI to which we map
        int[] to = new int[]{};
        adapter = new MessageAdapter(this, R.layout.postgram_posts_row, null,
                from, to, 0);

        message_listview = (ListView) findViewById(R.id.listview_post);
        message_listview.setAdapter(adapter);
        message_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MessageID != id) {
                    CID = String.valueOf(id);
                    final CharSequence[] items = {getString(R.string.DeleteComment)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
//                builder.setTitle("Pick a color");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0: // Delete
                                    MainNetwork.DeleteComment(getBaseContext(),
                                            DeleteComment_Listener, DeleteComment_ErrorListener,
                                            Public_Functions.GetMyID(),
                                            String.valueOf(MessageID),
                                            CID);
                                    progress = ProgressDialog.show(
                                            MessageActivity.this,
                                            getResources().getString(
                                                    R.string.please_wait),
                                            getResources().getString(
                                                    R.string.Sending_Message), true);
                                    progress.setCancelable(true);
                                    progress.setCanceledOnTouchOutside(false);

                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Get_New_Comments();

        // ////////////////
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        emojiconEditText = (TextView) findViewById(R.id.emojicon_edit_text);

//        submitButton.setEnabled(false);
        submitButton.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_send));


        // On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String tmpstr = emojiconEditText.getText().toString();
                str_comment = tmpstr;
                if (str_comment.trim().length() > 3) {
                    fragment = new AlertDialog.Builder(MessageActivity.this)
                            .create();
                    fragment.setTitle("Error");

                    progress = ProgressDialog.show(
                            MessageActivity.this,
                            getResources().getString(
                                    R.string.please_wait),
                            getResources().getString(
                                    R.string.Sending_Message), true);
                    progress.setCancelable(true);
                    progress.setCanceledOnTouchOutside(false);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            MainNetwork.SendComment(MessageActivity.this,
                                    SendComment_Listener,
                                    SendComment_ErrorListener,
                                    str_comment,
                                    String.valueOf(MessageID));
                        }
                    }).start();
                }
            }


        });

    }


    private Intent createShareIntent() {
        // Query to get Data !
        String Message = "";
        String[] projection = {TablePost.COLUMN_Text};
        Cursor cursor = getContentResolver().query(
                AppContentProvider.Posts_URI, projection,
                TablePost.COLUMN_ID + "=?",
                new String[]{String.valueOf(MessageID)}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Message = cursor.getString(cursor
                    .getColumnIndex(TablePost.COLUMN_Text));
        }
        cursor.close();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                getResources().getString(R.string.AppName));
        shareIntent.putExtra(Intent.EXTRA_TEXT, Message);
        return shareIntent;
    }

    public void Get_New_Comments() {
        int CommentID = 0;
        long CommentTime = 0;
        // Get Last Comment ID
        String[] projection = {
                "MAX(" + TableComments.COLUMN_ID + ") as MaxID",
                TableComments.COLUMN_Time};
        Cursor cursor = getContentResolver().query(
                AppContentProvider.Comments_Simple_URI, projection,
                TableComments.COLUMN_ID + "=?",
                new String[]{String.valueOf(MessageID)}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (!(cursor.isAfterLast())) {
                try {
                    CommentID = (cursor.getInt(cursor.getColumnIndex("MaxID")));
                    CommentTime = cursor
                            .getLong(cursor
                                    .getColumnIndex(TableComments.COLUMN_Time));
                } catch (Exception e) {
                }
            }
        }
        // always close the cursor
        cursor.close();
        //
        MainNetwork.Get_New_Comments(this, NewComments_Listener,
                NewComments_ErrorListener, String.valueOf(MessageID),
                String.valueOf(CommentID), String.valueOf(CommentTime));
    }

    Listener<String> NewComments_Listener = new Listener<String>() {
        @Override
        public void onResponse(final String response) {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // /
                            JSONObject jObject = new JSONObject(response);
                            JSONArray Comments_element = jObject
                                    .getJSONArray("comments");
                            String LCount = jObject.getString("like_count");
                            String MCount = jObject.getString("comment_count");

                            ContentValues values = new ContentValues();

                            values.put(TablePost.COLUMN_LikeCount,
                                    LCount);
                            values.put(TablePost.COLUMN_CommentCount,
                                    MCount);

                            getContentResolver().update(
                                    AppContentProvider.Posts_URI,
                                    values,
                                    TablePost.COLUMN_ID + "=?",
                                    new String[]{String.valueOf(MessageID)});

                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();

                            // /

                            List<Comment> tmp_New_Comments = new ArrayList<Comment>();

                            tmp_New_Comments = gson.fromJson(
                                    Comments_element.toString(),
                                    new TypeToken<List<Comment>>() {
                                    }.getType());
                            for (int j = 0; j < tmp_New_Comments.size(); j++) {
                                ContentValues cm_values = new ContentValues();

                                cm_values.put(
                                        TableComments.COLUMN_ID,
                                        tmp_New_Comments.get(j).getCommentID());
                                cm_values.put(
                                        TableComments.COLUMN_UserID,
                                        tmp_New_Comments.get(j).getUser_id());
                                cm_values.put(
                                        TableComments.COLUMN_Text,
                                        tmp_New_Comments.get(j).getCommentText());
                                cm_values.put(
                                        TableComments.COLUMN_Time,
                                        tmp_New_Comments.get(j).getRegtime());
                                cm_values.put(
                                        TableComments.COLUMN_PostID,
                                        MessageID);

                                getContentResolver()
                                        .insert(AppContentProvider.Comments_URI,
                                                cm_values);


                            }
                            getContentResolver().notifyChange(
                                    AppContentProvider.Comments_URI,
                                    null);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        message_listview.setSelection(adapter
                                                .getCount() - 1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    ErrorListener NewComments_ErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // mToast.ShowToast(MessageActivity.this,
            // android.R.drawable.ic_dialog_alert,
            // getResources().getString(R.string.http_error));

            try {
                MenuItemCompat.setActionView(refresh, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // /
    Listener<String> SendComment_Listener = new Listener<String>() {
        @Override
        public void onResponse(final String response) {
            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                        if (response.trim().length() > 12) {
                            String ResponseMessage = getResources().getString(
                                    R.string.UnknownError);
                            try {
                                JsonParser parser = new JsonParser();
                                JsonObject element = (JsonObject) parser
                                        .parse(response);
                                // ResCode = element.get("error").getAsInt();
                                ResponseMessage = element.get("str")
                                        .getAsString();
                            } catch (Exception e) {
                            }
                            fragment.setMessage(ResponseMessage);
                            fragment.show();
                        } else {

                            if (!(MessageActivity.this.isFinishing())) {
                                fragment.setTitle(getString(R.string.YourPasswordSuccess));
                                fragment.setMessage(getResources().getString(
                                        R.string.Your_Message_has_been_Send));
                                fragment.setButton(
                                        DialogInterface.BUTTON_POSITIVE, getString(R.string.OK),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface arg0,
                                                    int arg1) {
                                                emojiconEditText.setText("");
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(
                                                        emojiconEditText
                                                                .getWindowToken(),
                                                        0);
                                                Get_New_Comments();
                                            }
                                        });

                                fragment.show();

                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    ErrorListener SendComment_ErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progress.dismiss();
            Toast.makeText(getBaseContext(), getString(R.string.UnknownError), Toast.LENGTH_SHORT).show();
            try {
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    // /

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message, menu);
        String MyID = Public_Functions.GetMyID();
        if (UserID.equals(MyID))
            try {
                menu.findItem(R.id.action_DeletePost).setVisible(true);
            } catch (Exception e) {
            }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                startActivity(createShareIntent());
                break;
            case R.id.action_DeletePost:
                MainNetwork.DeletePost(getBaseContext(), DeletePost_Listener, DeletePost_ErrorListener, Public_Functions.GetMyID(), String.valueOf(MessageID));
                progress = ProgressDialog.show(
                        MessageActivity.this,
                        getResources().getString(
                                R.string.please_wait),
                        getResources().getString(
                                R.string.Sending_Message), true);
                progress.setCancelable(true);
                progress.setCanceledOnTouchOutside(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        String[] projection = {
                TablePost.COLUMN_ID, TablePost.COLUMN_UserID, TablePost.COLUMN_Text
                , TablePost.COLUMN_Time, TablePost.COLUMN_MyLike, TablePost.COLUMN_LikeCount, TablePost.COLUMN_CommentCount, TablePost.COLUMN_MediaList};

        String[] projection2 = {
                TableComments.COLUMN_ID, TableComments.COLUMN_UserID, TableComments.COLUMN_Text
                , TableComments.COLUMN_Time, "0", "0", "-1", "''"};

        CursorLoader cursorLoader = new CursorLoader(getBaseContext(),
                AppContentProvider.Comments_URI, projection,
                String.valueOf(MessageID), projection2,
                TableComments.COLUMN_ID + " ASC ");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onStart() {
//        EasyTracker.getInstance(this).activityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }


    Listener<String> DeletePost_Listener = new Listener<String>() {
        @Override
        public void onResponse(final String response) {
            try {
                if (response.trim().equals("1")) {
                    Toast.makeText(getBaseContext(), getString(R.string.RemovedThePost), Toast.LENGTH_LONG).show();
                    getContentResolver().delete(AppContentProvider.Posts_URI, TablePost.COLUMN_ID + "=?",
                            new String[]{String.valueOf(MessageID)});
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    ErrorListener DeletePost_ErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progress.dismiss();
            Toast.makeText(getBaseContext(), getString(R.string.UnknownError), Toast.LENGTH_SHORT).show();

        }
    };

    Listener<String> DeleteComment_Listener = new Listener<String>() {
        @Override
        public void onResponse(final String response) {
            try {
                if (response.trim().equals("1")) {
                    Toast.makeText(getBaseContext(), getString(R.string.CommentIsRemoved), Toast.LENGTH_LONG).show();
                    getContentResolver().delete(AppContentProvider.Posts_URI, TablePost.COLUMN_ID + "=?",
                            new String[]{CID});
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    ErrorListener DeleteComment_ErrorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progress.dismiss();
            Toast.makeText(getBaseContext(), getString(R.string.UnknownError), Toast.LENGTH_SHORT).show();

        }
    };
}
