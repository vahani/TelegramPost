package org.telegram.messenger.postgram.classes;

public class Comment {
    private int comment_id;
    private String comment_text;
    private String regtime;
    private int user_id = 0;


    public int getCommentID() {
        return comment_id;
    }

    public void setCommentID(int commentID) {
        comment_id = commentID;
    }

    public String getCommentText() {
        return comment_text;
    }

    public void setCommentText(String commentText) {
        comment_text = commentText;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
