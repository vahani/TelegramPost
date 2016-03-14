package org.telegram.messenger.postgram.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.postgram.classes.Picture;
import org.telegram.messenger.postgram.db.AppContentProvider;
import org.telegram.messenger.postgram.db.TablePost;
import org.telegram.messenger.postgram.dialogs.LikerListActivity;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.postgram.tools.Public_Data;
import org.telegram.messenger.postgram.tools.Public_Functions;
import org.telegram.messenger.postgram.tools.ViewPagerZoomPictures;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

import java.util.ArrayList;


public class posts_Adapter extends SimpleCursorAdapter {

    private LayoutInflater mLayoutInflater;
    private int layout;

    private class ViewHolder {
        TextView txt_MOwnerName;
        TextView txt_MText;
        org.telegram.ui.Components.BackupImageView img_UserImage;
        TextView txt_Date;
        ImageView Img_JokePic, img_play, img_loading, img_like;
        TextView txt_CommentCount;
        TextView txt_LikeCount;
        RelativeLayout LikeArea, relative_parent;
        TextView txtTag, txtTime, txtVideoTitle;
        LinearLayout Lin_ScreenShots;

        ViewHolder(View v) {

            txt_MOwnerName = (TextView) v.findViewById(R.id.txtSender);
            txt_MText = (TextView) v.findViewById(R.id.txtMessage);
            img_UserImage = (org.telegram.ui.Components.BackupImageView) v
                    .findViewById(R.id.UserImage);
            img_UserImage.setRoundRadius(AndroidUtilities.dp(26));

            txt_Date = (TextView) v.findViewById(R.id.textDate);
            Img_JokePic = (ImageView) v.findViewById(R.id.Img_JokePic);
            img_loading = (ImageView) v.findViewById(R.id.img_loading);
            txt_CommentCount = (TextView) v.findViewById(R.id.CommentCount);
            txt_LikeCount = (TextView) v.findViewById(R.id.txtLike);
            LikeArea = (RelativeLayout) v.findViewById(R.id.LikeArea);

            txtTag = (TextView) v.findViewById(R.id.txtTag);

            txtTime = (TextView) v.findViewById(R.id.txtTime);
            txtVideoTitle = (TextView) v.findViewById(R.id.txtVideoTitle);
            img_play = (ImageView) v.findViewById(R.id.img_play);
            img_like = (ImageView) v.findViewById(R.id.img_like);
            relative_parent = (RelativeLayout) v.findViewById(R.id.relative_parent);
            Lin_ScreenShots = (LinearLayout) v
                    .findViewById(R.id.Lin_ScreenShots);
        }
    }

    public posts_Adapter(Context context, int layout, Cursor c,
                         String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        this.layout = layout;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View vView = mLayoutInflater.inflate(layout, parent, false);
        vView.setTag(new ViewHolder(vView));

        return vView;
    }

    @Override
    public void bindView(View v, final Context ctx, Cursor c) {
        final String MessageID = c.getString(c
                .getColumnIndex(TablePost.COLUMN_ID));
        final int UserID = c.getInt(c.getColumnIndex(TablePost.COLUMN_UserID));
        final String Text = c.getString(c.getColumnIndex(TablePost.COLUMN_Text));
        long Time = c.getLong(c.getColumnIndex(TablePost.COLUMN_Time));

        final int MyLike = c.getInt(c.getColumnIndex(TablePost.COLUMN_MyLike));
        final int MessageLikeCount = c.getInt(c.getColumnIndex(TablePost.COLUMN_LikeCount));
        int CommentCount = c.getInt(c.getColumnIndex(TablePost.COLUMN_CommentCount));
        final String MediaList = c.getString(c.getColumnIndex(TablePost.COLUMN_MediaList));

        final ViewHolder vh = (ViewHolder) v.getTag();

        vh.txt_MText.setText(Text);
        vh.txt_Date.setText(Public_Functions.getTimeAgo(Time * 1000, ctx));
        vh.txt_CommentCount.setText(String.valueOf(CommentCount));
        vh.txtTag.setText(String.valueOf(UserID));


        if (MyLike == 1)
            vh.img_like.setImageResource(R.drawable.ic_liked);
        else
            vh.img_like.setImageResource(R.drawable.ic_like);

        update(UserID, vh.txt_MOwnerName, vh.img_UserImage);

        vh.relative_parent.setPadding(32, 22, 32, 22);

        //        Like
        vh.LikeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (MyLike == 0) {
                        MainNetwork.Set_Like_or_Unlike(ctx, GCM_Listener,
                                GCM_ErrorListener, MessageID, "1");

                        final ContentValues like_values = new ContentValues();

                        like_values.put(TablePost.COLUMN_MyLike,
                                1);
                        like_values.put(
                                TablePost.COLUMN_LikeCount,
                                String.valueOf((Integer
                                        .valueOf(MessageLikeCount) + 1)));
//                        vh.img_like.setImageResource(R.drawable.ic_liked);
//                        vh.txt_LikeCount.setText(String.valueOf(Integer
//                                .valueOf(MessageLikeCount) + 1) + " " + ctx.getString(R.string.Likes));

                        ctx.getContentResolver()
                                .update(AppContentProvider.Posts_URI,
                                        like_values,
                                        TablePost.COLUMN_ID
                                                + "=?",
                                        new String[]{MessageID});

                        ctx.getContentResolver()
                                .notifyChange(
                                        AppContentProvider.Posts_URI,
                                        null);
                    } else if (MyLike == 1) {

                        MainNetwork.Set_Like_or_Unlike(ctx, GCM_Listener,
                                GCM_ErrorListener, MessageID, "0");

                        final ContentValues like_values = new ContentValues();
                        like_values.put(TablePost.COLUMN_MyLike,
                                0);
                        like_values.put(
                                TablePost.COLUMN_LikeCount,
                                String.valueOf((Integer
                                        .valueOf(MessageLikeCount) - 1)));
                        ctx.getContentResolver().update(
                                AppContentProvider.Posts_URI,
                                like_values,
                                TablePost.COLUMN_ID + "=?",
                                new String[]{MessageID});
                        ctx.getContentResolver().notifyChange(
                                AppContentProvider.Posts_URI,
                                null);
                    }
                } catch (Exception e) {
                }
            }
        });

        vh.txt_LikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikerListActivity L_L_A = new LikerListActivity();
                Bundle args = new Bundle();
                args.putString("ID", MessageID);
                L_L_A.setArguments(args);
                L_L_A.show(
                        ((FragmentActivity) ctx).getSupportFragmentManager(),
                        "LikerListActivity");
            }
        });

        if (MessageLikeCount == 0) {
            vh.txt_LikeCount.setText("0");
            vh.txt_LikeCount.setOnClickListener(null);
        } else if (MessageLikeCount == 1)
            vh.txt_LikeCount.setText(String.valueOf(MessageLikeCount) + " " + ctx.getString(R.string.Like));
        else if (MessageLikeCount >= 2)
            vh.txt_LikeCount.setText(String.valueOf(MessageLikeCount) + " " + ctx.getString(R.string.Likes));


        if (MediaList != null)
            if (MediaList.length() > 3) {
                String[] MediaListArray = MediaList.split(",");
//                    MessagePic = "";
                final ArrayList<Picture> tmp_New_Pictures = new ArrayList<Picture>();

                for (int i = 0; i < MediaListArray.length; i++)
                    tmp_New_Pictures.add(new Picture(MediaListArray[i]));

                vh.Lin_ScreenShots.removeAllViews();

                for (int i = 0; i < MediaListArray.length; i++) {
                    View My_UpdateView = new View(ctx);

                    My_UpdateView = LayoutInflater.from(ctx).inflate(
                            R.layout.screenshot, null);

                    final ImageView ImgScreenShot = (ImageView) My_UpdateView
                            .findViewById(R.id.ImgScreenShot);
                    final ProgressBar img_progresses = (ProgressBar) My_UpdateView
                            .findViewById(R.id.img_progresses);
                    final ImageView img_Error = (ImageView) My_UpdateView
                            .findViewById(R.id.img_Error);

                    ImgScreenShot.setTag(i);

                    String TheImgPath = Public_Data.MessagePictureBaseURL
                            + UserID + "/tn_" + MediaListArray[i];

                    ImgScreenShot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Show Full Screen
                            Intent ZoomPic_Intent = new Intent(ctx,
                                    ViewPagerZoomPictures.class);
                            ZoomPic_Intent.putExtra("BaseURL",
                                    Public_Data.MessagePictureBaseURL
                                            + UserID + "/tn_"
                            );
                            ZoomPic_Intent.putExtra("position", Integer
                                    .valueOf(ImgScreenShot.getTag()
                                            .toString()));
                            Public_Data.MyPictures.clear();
                            Public_Data.MyPictures.addAll(tmp_New_Pictures);
                            ctx.startActivity(ZoomPic_Intent);
                        }
                    });
                    Picasso.with(ctx).load(TheImgPath)
                            .into(ImgScreenShot, new Callback() {

                                @Override
                                public void onSuccess() {
                                    img_progresses.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    img_progresses.setVisibility(View.GONE);
                                    img_Error.setVisibility(View.VISIBLE);
                                }
                            });

                    LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    relativeParams.rightMargin = 18;

                    vh.Lin_ScreenShots.addView(My_UpdateView,
                            relativeParams);
                }
                vh.Lin_ScreenShots.setVisibility(View.VISIBLE);

            } else {
                vh.Lin_ScreenShots.removeAllViews();
                vh.Lin_ScreenShots.setVisibility(View.GONE);
            }
        else {
            vh.Lin_ScreenShots.removeAllViews();
            vh.Lin_ScreenShots.setVisibility(View.GONE);
        }
    }

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

    public Response.Listener<String> GCM_Listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        }
    };

    public Response.ErrorListener GCM_ErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };
}