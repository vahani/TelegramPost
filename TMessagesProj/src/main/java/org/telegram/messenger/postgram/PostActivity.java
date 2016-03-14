package org.telegram.messenger.postgram;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationCompat.ObjectAnimatorProxy;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.postgram.adapters.posts_Adapter;
import org.telegram.messenger.postgram.classes.Post;
import org.telegram.messenger.postgram.db.AppContentProvider;
import org.telegram.messenger.postgram.db.TablePost;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    String MYID;
    ListView listview_post;
    posts_Adapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    int mLastFirstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TLRPC.User ME = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        MYID = String.valueOf(ME.id);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.holo_blue_light);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        MainNetwork.GetPosts(getBaseContext(), Post_Response, Post_Error, MYID);
                    }
                });
        mSwipeRefreshLayout.setRefreshing(true);

        listview_post = (ListView) findViewById(R.id.listview_post);


        AddFloatIcon();

        MainNetwork.GetPosts(getBaseContext(), Post_Response, Post_Error, MYID);

        adapter = new posts_Adapter(this,
                R.layout.postgram_posts_row, null, new String[]{}, new int[]{}, 0);

        listview_post.setAdapter(adapter);

        listview_post.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (absListView.getId() == listview_post.getId()) {
                    final int currentFirstVisibleItem = listview_post.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        hideFloatingButton(true);
                        Log.i("a", "scrolling down...");
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        hideFloatingButton(false);
                        Log.i("a", "scrolling up...");
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);

        listview_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long MessageOwnerId = 0, Userid = 0;
                try {
                    Cursor Myc = adapter.getCursor();
                    Myc.moveToPosition(position);
                    MessageOwnerId = Myc.getLong(Myc
                            .getColumnIndex(TablePost.COLUMN_ID));
                    Userid = Myc.getInt(Myc
                            .getColumnIndex(TablePost.COLUMN_UserID));
                    // Myc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
                Intent MessageActivity_Intent = new Intent(getBaseContext(),
                        MessageActivity.class);
                MessageActivity_Intent.putExtra("id", id);
                MessageActivity_Intent.putExtra("MOwnerID", Userid);
                MessageActivity_Intent.putExtra("MessageOwnerID",
                        MessageOwnerId);
                startActivity(MessageActivity_Intent);
            }
        });

    }

    ImageView floatingButton;

    private void AddFloatIcon() {
        floatingButton = new ImageView(getBaseContext());
        floatingButton.setVisibility(View.VISIBLE);
        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        floatingButton.setBackgroundResource(R.drawable.floating_states);
        floatingButton.setImageResource(R.drawable.floating_pencil);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            floatingButton.setStateListAnimator(animator);
            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint("NewApi")
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                }
            });
        }
        ((LinearLayout) findViewById(R.id.linear_floating)).addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 16 : 0, 0, LocaleController.isRTL ? 0 : 16, 16));
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), WriteActivity.class));
            }
        });
    }

    private boolean floatingHidden;

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimatorProxy animator = ObjectAnimatorProxy.ofFloatProxy(floatingButton, "translationY", floatingHidden ? AndroidUtilities.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                    try {
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            try {
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    int localLimit = 20;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TablePost.COLUMN_ID, TablePost.COLUMN_UserID, TablePost.COLUMN_Text
                , TablePost.COLUMN_Time, TablePost.COLUMN_MyLike, TablePost.COLUMN_LikeCount, TablePost.COLUMN_CommentCount, TablePost.COLUMN_MediaList};
        CursorLoader cursorLoader = new CursorLoader(getBaseContext(),
                AppContentProvider.Posts_URI, projection,
                null, null, TablePost.COLUMN_ID
                + " DESC Limit 0," + String.valueOf(localLimit));

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
}