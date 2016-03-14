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
import android.view.View.OnClickListener;
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

public class MessageAdapter extends SimpleCursorAdapter {

    private LayoutInflater mLayoutInflater;
    private int layout;

    private class ViewHolder {
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

        ViewHolder(View v) {
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
                    .findViewById(R.id.relative_parent);
            txtTag = (TextView) v.findViewById(R.id.txtTag);

            txtTime = (TextView) v.findViewById(R.id.txtTime);
            txtVideoTitle = (TextView) v.findViewById(R.id.txtVideoTitle);
            img_play = (ImageView) v.findViewById(R.id.img_play);
            linPicLines = (LinearLayout) v.findViewById(R.id.linPicLines);
            img_like = (ImageView) v.findViewById(R.id.img_like);
            relative_parent = (RelativeLayout) v.findViewById(R.id.relative_parent);
        }

    }

    public MessageAdapter(Context context, int layout, Cursor c, String[] from,
                          int[] to, int flags) {
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
