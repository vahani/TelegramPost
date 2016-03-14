package org.telegram.messenger.postgram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.postgram.classes.LikerList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

import java.util.List;

public class LikersListAdapter extends BaseAdapter {

    public List<LikerList> LikersList;
    private Context My_Context;
    public MenuInflater MyMenu;
    private boolean ownlist = false;

    public LikersListAdapter(Context context, List<LikerList> MyMessages,
                             boolean listStatus) {
        LikersList = MyMessages;
        My_Context = context;

        ownlist = listStatus;
    }

    public void add(List<LikerList> All_Messages) {
        LikersList.addAll(All_Messages);
    }

    public int getCount() {
        return LikersList.size();
    }

    public Object getItem(int position) {
        return LikersList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) My_Context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.activity_liker_list_detail, parent, false);
        }

        TextView txtSender = (TextView) v.findViewById(R.id.txtSender);
        BackupImageView UserImage = (BackupImageView) v.findViewById(R.id.UserImage);
        UserImage.setRoundRadius(AndroidUtilities.dp(26));

        int UserID = (int) LikersList.get(position).user_id;

        update(UserID, txtSender, UserImage);


        return v;
    }

    public void update(int UserID, TextView nameTextView, BackupImageView avatarImageView) {
        TLRPC.User muser = MessagesController.getInstance().getUser(UserID);
        TLObject currentObject = muser;
        AvatarDrawable avatarDrawable = new AvatarDrawable(muser,true);
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