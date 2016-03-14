package org.telegram.messenger.postgram.classes;

/**
 * Created by vahid on 1/10/16.
 */
public class Post {
    public long post_id;
    public long user_id;
    public String post_text;
    private int like_count;
    private int comment_count;
    private String media_list;

    public long getPost_id() {
        return post_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getPost_text() {
        return post_text;
    }

    public long getPost_time() {
        return post_time;
    }

    public long post_time;

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getMedia_list() {
        return media_list;
    }

    public void setMedia_list(String media_list) {
        this.media_list = media_list;
    }
}