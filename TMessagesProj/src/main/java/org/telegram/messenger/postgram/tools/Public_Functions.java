package org.telegram.messenger.postgram.tools;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vahid on 2/17/16.
 */
public class Public_Functions {

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(long time, Context ctx) {

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static void NoMedia(final Context ctx, String path,
                               boolean CreateNoMedia) {
        File File = new File(path + "/.nomedia");
        // File File2 = new File(sdCard + "/" + path);
        if (CreateNoMedia) {
            try {
                File.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File.delete();
            } catch (Exception e) {
            }

        }

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(File));
        ctx.sendBroadcast(scanIntent);

        MediaScannerConnection.scanFile(ctx, new String[]{File.toString()},
                null, new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":");
                        // Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


    public static String GetMyID() {
        TLRPC.User ME = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        return String.valueOf(ME.id);
    }
}
