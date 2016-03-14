package org.telegram.messenger.postgram;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.postgram.adapters.ProfileCursorAdapter;
import org.telegram.messenger.postgram.classes.Post;
import org.telegram.messenger.postgram.db.AppContentProvider;
import org.telegram.messenger.postgram.db.TablePost;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ProfileCursorAdapter adapter;
    RecyclerView my_recycler_view;
    //    SwipeRefreshLayout mSwipeRefreshLayout;
    String userid;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userid = String.valueOf(getIntent().getExtras().getInt("userid"));
        TLRPC.User muser = MessagesController.getInstance().getUser(Integer.valueOf(userid));
        TextView txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtPhone.setText("Mobile : " + muser.phone);
        TextView tmpTxtUserName = new TextView(getBaseContext());
        BackupImageView tmpImage = (BackupImageView) findViewById(R.id.img_user);
        tmpImage.setRoundRadius(AndroidUtilities.dp(22));

        update(Integer.valueOf(userid), tmpTxtUserName, tmpImage);

        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(tmpTxtUserName.getText().toString());
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.White));

        my_recycler_view.setLayoutManager(new
                LinearLayoutManager(this));

        adapter = new ProfileCursorAdapter(ProfileActivity.this, null);
        my_recycler_view.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, this);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
//                R.color.holo_blue_light);
//        mSwipeRefreshLayout
//                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        MainNetwork.GetPosts(getBaseContext(), Post_Response, Post_Error, userid);
//                    }
//                });
//        mSwipeRefreshLayout.setRefreshing(true);

        MainNetwork.GetPosts(getBaseContext(), Post_Response, Post_Error, userid);

//        my_recycler_view.addOnItemTouchListener(
//                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Cursor c = adapter.getCursor();
//                        Intent i = new Intent(getBaseContext(), MessageActivity.class);
//                        i.putExtra("id", c.getLong(c.getColumnIndex(TablePost.COLUMN_ID))); // msg id
//                        i.putExtra("MOwnerID", c.getLong(c.getColumnIndex(TablePost.COLUMN_UserID)));
////                        i.putExtra("mystatus", c.getInt(c.getColumnIndex(TablePost.)));
//
//                        startActivity(i);
//                    }
//                })
//        );

        findViewById(R.id.btn_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TLRPC.Chat currentChat;
//                int chat_id = Integer.valueOf(userid);
//                currentChat = MessagesController.getInstance().getChat(chat_id);
//
//                NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
//                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
//                Bundle args = new Bundle();
//                args.putInt("chat_id", currentChat.id);
//                presentFragment(new ChatActivity(args), true);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TablePost.COLUMN_ID, TablePost.COLUMN_UserID, TablePost.COLUMN_Text
                , TablePost.COLUMN_Time, TablePost.COLUMN_MyLike, TablePost.COLUMN_LikeCount,
                TablePost.COLUMN_CommentCount, TablePost.COLUMN_MediaList};
        CursorLoader cursorLoader = new CursorLoader(getBaseContext(),
                AppContentProvider.Posts_URI, projection,
                TablePost.COLUMN_UserID + "=?", new String[]{userid}, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    Response.Listener<String> Post_Response = new Response.Listener<String>() {
        @Override
        public void onResponse(final String response) {
            if (!isFinishing()) {
                try {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                List<Post> Post_List = new ArrayList<Post>();

                                Post_List = gson.fromJson(response,
                                        new TypeToken<List<Post>>() {
                                        }.getType());
                                for (Post mPost : Post_List) {
                                    ContentValues CV = new ContentValues();
                                    CV.put(TablePost.COLUMN_ID, mPost.getPost_id());
                                    CV.put(TablePost.COLUMN_Text, mPost.getPost_text());
                                    CV.put(TablePost.COLUMN_Time, mPost.getPost_time());
                                    CV.put(TablePost.COLUMN_UserID, mPost.getUser_id());
                                    CV.put(TablePost.COLUMN_LikeCount, mPost.getLike_count());
                                    CV.put(TablePost.COLUMN_CommentCount, mPost.getComment_count());
                                    CV.put(TablePost.COLUMN_MediaList, mPost.getMedia_list());
                                    getContentResolver().insert(AppContentProvider.Posts_URI, CV);
                                }
                                getContentResolver().notifyChange(AppContentProvider.Posts_URI, null);

                            } catch (Exception e) {
                            }
                        }
                    }).start();
//                    try {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                } catch (Exception e) {
                }
            }
        }
    };

    // /
    Response.ErrorListener Post_Error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.print("");
//            try {
//                mSwipeRefreshLayout.setRefreshing(false);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    };

    int localLimit = 20;

    public void update(int UserID, TextView nameTextView, BackupImageView avatarImageView) {
        TLRPC.User muser = MessagesController.getInstance().getUser(UserID);
        TLObject currentObject = muser;
        AvatarDrawable avatarDrawable = new AvatarDrawable(muser, true);
        String lastName;
        if (currentObject == null) {
            return;
        }
        TLRPC.FileLocation photo = null;
        String newName = null;
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        if (currentObject instanceof TLRPC.User) {
            currentUser = (TLRPC.User) currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
        } else {
            currentChat = (TLRPC.Chat) currentObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
        }


        avatarDrawable.setInfo(currentChat);

        if (currentUser != null) {
            lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
        } else {
            lastName = newName == null ? currentChat.title : newName;
        }
        nameTextView.setText(lastName);


        avatarImageView.setImage(photo, "50_50", avatarDrawable);
    }
}
