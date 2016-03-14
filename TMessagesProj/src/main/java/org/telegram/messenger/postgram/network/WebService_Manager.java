package org.telegram.messenger.postgram.network;

import android.content.Context;

import org.telegram.messenger.postgram.tools.Public_Data;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class WebService_Manager {


    public static String SendMediaTemp(Context context, List<String> Media, String MYID) {

        String status = "";
        try {

            List<FileInputStream> fstrm = new ArrayList<FileInputStream>();

            try {
                for (int k = 0; k < Media.size(); k++) {
                    FileInputStream tmpfstrm = new FileInputStream(Media.get(k)
                            .toString());
                    fstrm.add(tmpfstrm);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No File Attached !");
            }

            HttpFileUpload hfu = new HttpFileUpload(Public_Data.BaseURL, Media,
                    context, "");

            List<String> MyParams = new ArrayList<String>();
            List<String> MyParamsValue = new ArrayList<String>();

            MyParams.add("FnName");
            MyParamsValue.add("SendMediaTemp");

            MyParams.add("UserID");
            MyParamsValue.add(MYID);

            status = hfu.Send_Now(fstrm, MyParams, MyParamsValue, Media);

        } catch (Exception e) {
        }
        return status;

    }
}