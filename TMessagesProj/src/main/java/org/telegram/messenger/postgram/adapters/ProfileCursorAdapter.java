package org.telegram.messenger.postgram.adapters;

/**
 * Created by Hatami on 3/2/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
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
import org.telegram.messenger.postgram.MessageActivity;
import org.telegram.messenger.postgram.ProfileActivity;
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

/**
 * Created by skyfishjy on 10/31/14.
 */
public class ProfileCursorAdapter extends CursorRecyclerViewAdapter<ProfileCursorAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private int layout;
    private Context ctx;

    public ProfileCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        ctx = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_MOwnerName;
        TextView txt_MText;
        BackupImageView img_UserImage;
        TextView txt_Date;
        ImageView Img_JokePic, img_play, img_loading, img_like;
        TextView txt_CommentCount;
        TextView txt_LikeCount;
        RelativeLayout LikeArea;
        RelativeLayout Rel_MainPost;
        RelativeLayout Rel_FooterPm;
        RelativeLayout Rel_Header, relative_parent;
        TextView txt_Date_Comment;
        TextView txt_MText_Comment;
        RelativeLayout Rel_relativeLayout1;
        TextView txtTag, txtTime, txtVideoTitle;
        LinearLayout linPicLines;
        LinearLayout Lin_ScreenShots;

        public ViewHolder(View v) {
            super(v);
            txt_MOwnerName = (TextView) v.findViewById(R.id.txtSender);
            txt_MText = (TextView) v.findViewById(R.id.txtMessage);
            img_UserImage = (BackupImageView) v
                    .findViewById(R.id.UserImage);
            img_UserImage.setRoundRadius(AndroidUtilities.dp(26));
            txt_Date = (TextView) v.findViewById(R.id.textDate);
            Img_JokePic = (ImageView) v.findViewById(R.id.Img_JokePic);
            img_loading = (ImageView) v.findViewById(R.id.img_loading);
            txt_CommentCount = (TextView) v.findViewById(R.id.CommentCount);
            txt_LikeCount = (TextView) v.findViewById(R.id.txtLike);
            Rel_MainPost = (RelativeLayout) v.findViewById(R.id.main_post);
            Rel_FooterPm = (RelativeLayout) v.findViewById(R.id.FooterPM);
            Rel_Header = (RelativeLayout) v.findViewById(R.id.Rel_Header);
            txt_Date_Comment = (TextView) v.findViewById(R.id.textDate_comment);
            txt_MText_Comment = (TextView) v
                    .findViewById(R.id.TxtMessage_Comment);
            LikeArea = (RelativeLayout) v.findViewById(R.id.LikeArea);

            Rel_relativeLayout1 = (RelativeLayout) v
                    .findViewById(R.id.main_post);
            txtTag = (TextView) v.findViewById(R.id.txtTag);

            txtTime = (TextView) v.findViewById(R.id.txtTime);
            txtVideoTitle = (TextView) v.findViewById(R.id.txtVideoTitle);
            img_play = (ImageView) v.findViewById(R.id.img_play);
            linPicLines = (LinearLayout) v.findViewById(R.id.linPicLines);
            img_like = (ImageView) v.findViewById(R.id.img_like);
            relative_parent = (RelativeLayout) v.findViewById(R.id.relative_parent);
            Lin_ScreenShots = (LinearLayout) v
                    .findViewById(R.id.Lin_ScreenShots);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postgram_posts_row, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, Cursor c) {
        final String MessageText = c.getString(c.getColumnIndex(TablePost.COLUMN_Text));
        final int UserID = c.getInt(c.getColumnIndex(TablePost.COLUMN_UserID));
        long MessageTime = c.getLong(c.getColumnIndex(TablePost.COLUMN_Time));
        final String MessageLikeCount = c.getString(c.getColumnIndex(TablePost.COLUMN_LikeCount));
        String MessageCommentCount = c.getString(c.getColumnIndex(TablePost.COLUMN_CommentCount));
        final String MessageID = c.getString(c.getColumnIndex(TablePost.COLUMN_ID));
        final int MyLike = c.getInt(c.getColumnIndex(TablePost.COLUMN_MyLike));
        String MediaList = c.getString(c.getColumnIndex(TablePost.COLUMN_MediaList));

        update(UserID, vh.txt_MOwnerName, vh.img_UserImage);

        vh.Rel_relativeLayout1.setPadding(0, 30, 0, 0);

        try {
            if (Integer.valueOf(MessageCommentCount) < 0) {
                vh.Rel_FooterPm.setVisibility(View.GONE);
                vh.Rel_MainPost.setVisibility(View.GONE);
                vh.txt_LikeCount.setVisibility(View.GONE);
                vh.txt_Date.setVisibility(View.INVISIBLE);
                vh.txt_Date_Comment.setVisibility(View.VISIBLE);
                vh.txt_MText_Comment.setVisibility(View.VISIBLE);
                vh.Rel_Header.setBackgroundResource(R.color.items_1);
                vh.Rel_Header.setPadding(15, 15, 15, 15);
            } else {
                vh.Rel_FooterPm.setVisibility(View.VISIBLE);
                vh.Rel_MainPost.setVisibility(View.VISIBLE);
                vh.txt_LikeCount.setVisibility(View.VISIBLE);
                vh.txt_Date.setVisibility(View.VISIBLE);

                vh.txt_Date_Comment.setVisibility(View.GONE);
                vh.txt_MText_Comment.setVisibility(View.GONE);

                vh.Rel_Header.setBackgroundResource(R.color.White);
                vh.Rel_Header.setPadding(5, 5, 5, 5);
            }
        } catch (Exception e) {
            vh.Rel_FooterPm.setVisibility(View.VISIBLE);
            vh.Rel_MainPost.setVisibility(View.VISIBLE);
        }
        // ////////////////////////////////////

        if (MessageText.length() < 2)
            vh.txt_MText.setVisibility(View.GONE);
        else
            vh.txt_MText.setVisibility(View.VISIBLE);

        if (MyLike == 1)
            vh.img_like.setImageResource(R.drawable.ic_liked);
        else
            vh.img_like.setImageResource(R.drawable.ic_like);

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

        vh.txt_MText.setText(MessageText);
        vh.txt_MText_Comment.setText(MessageText);


//        vh.img_UserImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent Profile_Intent = new Intent(ctx, ProfileActivity.class);
//                Profile_Intent.putExtra("userid", UserID);
//                ctx.startActivity(Profile_Intent);
//            }
//        });


        vh.txt_Date.setText(Public_Functions.getTimeAgo(MessageTime * 1000, ctx));
        vh.txt_Date_Comment
                .setText(Public_Functions.getTimeAgo(MessageTime * 1000, ctx));

        vh.txtTime.setVisibility(View.GONE);
        vh.txtVideoTitle.setVisibility(View.GONE);
        vh.img_play.setVisibility(View.GONE);

//        vh.Rel_relativeLayout1
//                .setOnLongClickListener(new OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
////                        CommentMenuActivity CMF = new CommentMenuActivity();
////                        Bundle args = new Bundle();
////                        args.putString("ID", MessageID);
////                        args.putInt("CommentID", Integer.valueOf(MessageID));
////                        args.putString("CommentText", MessageText);
////                        args.putInt("CommentOwnerID",
////                                Integer.valueOf(MessageOwnerID));
////                        args.putLong("MOwnerID", Public_Data.MOwnerID);
////                        args.putLong("MessageOwnerID",
////                                Public_Data.thisMessageOwnerID);
////
////                        CMF.setArguments(args);
////
////                        CMF.show(((FragmentActivity) ctx)
////                                .getSupportFragmentManager(), "CMF");
//
//                        return true;
//                    }
//                });

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

                                                       ctx.getContentResolver()
                                                               .update(AppContentProvider.Posts_URI,
                                                                       like_values,
                                                                       TablePost.COLUMN_ID
                                                                               + "=?",
                                                                       new String[]{MessageID});

                                                       ctx.getContentResolver()
                                                               .notifyChange(
                                                                       AppContentProvider.Comments_URI,
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
                                                               AppContentProvider.Comments_URI,
                                                               null);
                                                   }
                                               } catch (Exception e) {
                                               }
                                           }
                                       }
        );

        int lCount = 0;
        try {
            lCount = Integer.valueOf(MessageLikeCount);
        } catch (Exception e) {
        }
        if (lCount == 0) {
            vh.txt_LikeCount.setText("0");
            vh.txt_LikeCount.setOnClickListener(null);
        } else if (lCount == 1)
            vh.txt_LikeCount.setText(String.valueOf(MessageLikeCount) + " " + ctx.getString(R.string.Like));
        else if (lCount >= 2)
            vh.txt_LikeCount.setText(String.valueOf(MessageLikeCount) + " " + ctx.getString(R.string.Likes));

        vh.txt_CommentCount.setText(MessageCommentCount);


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

        vh.relative_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, MessageActivity.class);
                i.putExtra("id", Long.valueOf(MessageID));
                i.putExtra("MOwnerID", Long.valueOf(UserID));
//              i.putExtra("mystatus", c.getInt(c.getColumnIndex(TablePost.)));
                ctx.startActivity(i);
            }
        });
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