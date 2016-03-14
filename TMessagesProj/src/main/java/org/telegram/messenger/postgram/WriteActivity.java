package org.telegram.messenger.postgram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.telegram.messenger.MessagesController;
import ir.android.telegram.post.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.postgram.network.WebService_Manager;
import org.telegram.messenger.postgram.tools.Public_Data;
import org.telegram.messenger.postgram.tools.Public_Functions;
import org.telegram.messenger.postgram.tools.ResizeImage;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.codetail.animation.SupportAnimator;
import vn.tungdx.mediapicker.MediaItem;
import vn.tungdx.mediapicker.MediaOptions;
import vn.tungdx.mediapicker.activities.MediaPickerActivity;

public class WriteActivity extends AppCompatActivity {

    EditText edt_postText;
    String MYID;
    ProgressDialog pd;
    private View theMenu, Attach_Audio, Attach_Camera, Attach_Gallery,
            Attach_Hide, overlay;//Attach_Video
    private boolean menuOpen = false;
    private List<MediaItem> mMediaSelectedList;
    private List<String> MyMediaPath = new ArrayList<String>();
    Gallery gallery;
    private String SendMessage_Result;
    String Images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_postText = (EditText) findViewById(R.id.EDTText);

        pd = new ProgressDialog(WriteActivity.this);
        pd.setTitle(getString(R.string.SendingPost));
        pd.setMessage(getString(R.string.please_wait));
        gallery = (Gallery) findViewById(R.id.ImageGallery);
        gallery.setFocusableInTouchMode(false);
        gallery.setCallbackDuringFling(false);

        TLRPC.User ME = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        MYID = String.valueOf(ME.id);
        edt_postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 2) {
                    icSend.setIcon(R.mipmap.attach_send2_enabled);
                    icSend.setEnabled(true);
                } else {
                    icSend.setIcon(R.mipmap.attach_send2_disabled);
                    icSend.setEnabled(false);
                }
            }
        });
        Define_MenuAttachMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.write, menu);
        icSend = menu.findItem(R.id.Action_Send);
        icSend.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    MenuItem icSend;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.Action_Send) {
//            pd.show();
            SendPost();
        } else if (id == R.id.Action_attach) {
            if (menuOpen) {
                hideMenu();
            } else {
                revealMenu();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    Response.Listener<String> Post_Response = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (!isFinishing()) {
                try {
                    pd.dismiss();
                    finish();
                } catch (Exception e) {

                }
            }
        }
    };

    private void Define_MenuAttachMenuItems() {

        theMenu = findViewById(R.id.the_menu);
        Attach_Audio = findViewById(R.id.Attach_Audio);
        Attach_Camera = findViewById(R.id.Attach_Camera);
        Attach_Gallery = findViewById(R.id.Attach_Gallery);
//        Attach_Video = findViewById(R.id.Attach_Video);
        Attach_Hide = findViewById(R.id.Attach_Hide);
        overlay = findViewById(R.id.overlay);

        Attach_Hide.setOnClickListener(ClickHandler);
    }

    View.OnClickListener ClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.Imageattach:
//                    if (menuOpen) {
//                        hideMenu();
//                    } else {
//                        revealMenu();
//                    }
//                    break;
//                case R.id.imgAttach:
//                    if (menuOpen) {
//                        hideMenu();
//                    } else {
//                        revealMenu();
//                    }
//                    break;
                case R.id.Attach_Hide:
                    hideMenu();
                    break;

                default:
                    break;
            }

        }

    };


    public void revealMenu() {
        menuOpen = true;

        theMenu.setVisibility(View.INVISIBLE);
        // int cx = theMenu.getRight() - 200;
        // int cy = theMenu.getTop();
        //
        // int finalRadius = Math.max(theMenu.getWidth(), theMenu.getHeight());
        // SupportAnimator anim =
        // ViewAnimationUtils.createCircularReveal(theMenu,
        // cx, cy, 0, finalRadius);
        // anim.setDuration(600);
        // theMenu.setVisibility(View.VISIBLE);
        // overlay.setVisibility(View.VISIBLE);
        // anim.start();

        Animation slide_Down = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.slide_down);
        theMenu.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        theMenu.startAnimation(slide_Down);

        // Animate The Icons One after the other, I really would like to know if
        // there is any
        // simpler way to do it
        Animation popeup1 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        Animation popeup2 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        Animation popeup3 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        Animation popeup4 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        Animation popeup5 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        Animation popeup6 = AnimationUtils.loadAnimation(this, R.anim.popup_in);
        popeup1.setStartOffset(50);
        popeup2.setStartOffset(100);
        popeup3.setStartOffset(150);
        popeup4.setStartOffset(200);
        popeup5.setStartOffset(250);
        popeup6.setStartOffset(300);
        Attach_Audio.startAnimation(popeup1);
        Attach_Camera.startAnimation(popeup2);
        Attach_Gallery.startAnimation(popeup3);
//        Attach_Video.startAnimation(popeup4);
        Attach_Hide.startAnimation(popeup5);

    }

    public void hideMenu() {
        menuOpen = false;
        int cx = theMenu.getRight() - 200;
        int cy = theMenu.getTop();
        int initialRadius = theMenu.getWidth();
        SupportAnimator anim = io.codetail.animation.ViewAnimationUtils.createCircularReveal(theMenu,
                cx, cy, initialRadius, 0);

        SupportAnimator.SimpleAnimatorListener mCard2AnimatorListener = new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                theMenu.setVisibility(View.INVISIBLE);
                theMenu.setVisibility(View.GONE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.GONE);
            }
        };

        anim.addListener(mCard2AnimatorListener);

        anim.start();
    }

    public void menuClick(View view) {
        /**
         * 1 Camera 2 Gallery 3 Video 4 Audio 5 File 6 Contact 7 Location 0 Exit
         **/
        hideMenu();

        int id = Integer.valueOf(view.getTag().toString());

        MediaOptions.Builder MediaOptionsBuilder = new MediaOptions.Builder();
        MediaOptions options = null;

        int REQUEST_MEDIA = 0;
        switch (id) {
            case 0:
                // back
                break;

            case 1:
//                TakedPhotoPath = new File(Public_Data.tmpAddress + "/"
//                        + System.currentTimeMillis() + ".jpg");
//
//                try {
//                    TakedPhotoPath.createNewFile();
//                } catch (Exception e) {
//                }
//
//                Uri outputFileUri = Uri.fromFile(TakedPhotoPath);
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//                startActivityForResult(cameraIntent, 11);
                break;
            case 2:
                options = MediaOptionsBuilder.canSelectMultiPhoto(true)
                        .canSelectMultiVideo(true).canSelectBothPhotoVideo()
                        .setMediaListSelected(mMediaSelectedList).build();

                REQUEST_MEDIA = 12;

                break;
            case 3:
//                // Intent Videointent = new Intent(Intent.ACTION_GET_CONTENT);
//                // Videointent.setType("video/*");
//                // startActivityForResult(Videointent, 13);
//
//                mToast.ShowToast(Send_Message_Activity.this,
//                        android.R.drawable.ic_dialog_alert,
//                        getString(R.string.YouCanUseLater));

                break;
            case 4:
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                        Send_Message_Activity.this);
//                builder.setTitle(getString(R.string.Aparat_Link));
//                // builder.setMessage(getString(R.string.Aparat_Link));
//                // Set up the input
//                final EditText input = new EditText(Send_Message_Activity.this);
//                // Specify the type of input expected; this, for example, sets the
//                // input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                input.setHint(getString(R.string.PasteAparatLink));
//                builder.setView(input);
//                builder.setCancelable(false);
//                // Set up the buttons
//                builder.setPositiveButton(getString(R.string.taiid),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                AparatUrl = input.getText().toString();
//                                MyMediaPath.clear();
//                                gallery.setVisibility(View.GONE);
//                                try {
//                                    mMediaSelectedList.clear();
//                                } catch (Exception e) {
//
//                                }
//                                if (AparatUrl.length() > 10
//                                        && AparatUrl.trim().toString()
//                                        .contains("aparat")
//                                        && AparatUrl.contains(".mp4")) {
//                                    RelAttachHelp.setVisibility(View.GONE);
//                                    linAparatVideoSelectedMsg
//                                            .setVisibility(View.VISIBLE);
//
//
//                                    dialog.dismiss();
//                                } else {
//                                    AparatUrl = "";
//                                    RelAttachHelp.setVisibility(View.VISIBLE);
//                                    linAparatVideoSelectedMsg
//                                            .setVisibility(View.GONE);
//
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(
//                                            Send_Message_Activity.this);
//                                    builder.setTitle(getString(R.string.Warning));
//                                    builder.setMessage(getString(R.string.err_Aparat));
//                                    // Set up the buttons
//                                    builder.setPositiveButton(
//                                            getString(R.string.taiid),
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(
//                                                        DialogInterface dialog,
//                                                        int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//
//                                    builder.show();
//                                }
//
//                            }
//                        });
//                builder.setNegativeButton(getString(R.string.Cancel),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                builder.setNeutralButton(getString(R.string.aparat_learning),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String url = "http://ba-ham.com/guide/clip/";
//                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                i.setData(Uri.parse(url));
//                                startActivity(i);
//                            }
//                        });
//                builder.setCancelable(false);
//                builder.show();

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:

                break;

            default:
                break;
        }
        if (options != null) {
            MediaPickerActivity.open(this, REQUEST_MEDIA, options);
        }
        //
        // Toast.makeText(this, view.getTag() + " Clicked", Toast.LENGTH_LONG)
        // .show();
    }

    // /
    Response.ErrorListener Post_Error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.print("");
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
//            case 11: // Camera
//                if (resultCode == Activity.RESULT_OK) {
//                    FillMedia(data, 1);
//                } else {
//                    try {
//                        TakedPhotoPath.delete();
//                    } catch (Exception e) {
//                    }
//                }
//                break;
            case 12: // Gallery
                if (resultCode == Activity.RESULT_OK) {
                    FillMedia(data, 0);
                }
                break;
//            case 13: // Video
//                if (resultCode == Activity.RESULT_OK) {
//                    String Path = Public_Function.getPath(getBaseContext(),
//                            data.getData());
//                    String extention = Path
//                            .substring((Path.lastIndexOf(".") + 1), Path.length())
//                            .toLowerCase().trim();
//                    File f = new File(Path);
//                    if (Arrays.asList(Public_Data.VideoExtention).contains(
//                            Public_Function.GetExtention(Path))
//                            && (f.length() < (10 * 1024 * 1024))) {
//                        MyMediaPath.clear();
//                        MyMediaPath.add(Path);
//                        // pd.show();
//                        // we need to get screen shot
//                        get_screenshot(Path);
//                        //
//                        // we need to get video size
//                        // Flenght = Public_Function.get_media_lenght(Path,
//                        // Send_Message_Activity.this);
//                        //
//                        // SendRequest();
//                        mToast.ShowToast(this, android.R.drawable.ic_dialog_info,
//                                getString(R.string.SendingMessage));
//                    } else {
//                        mToast.ShowToast(this, android.R.drawable.ic_dialog_info,
//                                getString(R.string.upload_error));
//                    }
//                }
//                break;
//
//            case 1067: // image is selected on old
//                if (resultCode == RESULT_OK) {
//                    try {
//                        MyMediaPath.clear();
//                        Uri selectedImageUri = data.getData();
//                        String path = getPath(Send_Message_Activity.this,
//                                selectedImageUri);
//                        Bitmap bitmap = ResizeImage.loadBitmap(path, null,
//                                ResizeImage.getPhotoSize(),
//                                ResizeImage.getPhotoSize(), getBaseContext());
//                        if (bitmap == null && ResizeImage.getPhotoSize() != 800) {
//                            bitmap = ResizeImage.loadBitmap(path, null, 800, 800,
//                                    getBaseContext());
//                        }
//                        path = Public_Data.tmpAddress
//                                + "/"
//                                + ResizeImage.scaleAndSaveImage(bitmap,
//                                ResizeImage.getPhotoSize(),
//                                ResizeImage.getPhotoSize(), 80, false, 0,
//                                0, Public_Data.tmpAddress);
//                        if (bitmap != null) {
//                            bitmap.recycle();
//                        }
//                        //
//                        //
//                        MyMediaPath.add(path);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;

            default:
                break;
        }
    }

    private void FillMedia(Intent data, int type) {
        MyMediaPath.clear();
        // 12 imageGallery
        String Path = "";
        if (type == 0) { // Selected Image From Gallery
            try {
                mMediaSelectedList = MediaPickerActivity
                        .getMediaItemSelected(data);
                if (mMediaSelectedList.size() > 15) {
                    Toast.makeText(getBaseContext(), "Your file is too long", Toast.LENGTH_LONG).show();
                }
                if (mMediaSelectedList.size() > 0) {
                    for (int i = 0; i < 15; i++) {
                        Path = mMediaSelectedList.get(i).getPathOrigin(
                                getBaseContext());
                        AddImageToMediaArray(Path);
                    }
                }
            } catch (Exception e) {
            }
        } else { // Taked Photo From Camera
//            Path = TakedPhotoPath.getPath().toString();
//            AddImageToMediaArray(Path);
        }

        gallery.setAdapter(new ImageGalleryAdapter(this));
        gallery.setVisibility(View.VISIBLE);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // display the images selected
                // ImageView imageView = (ImageView) findViewById(R.id.image1);
                // imageView.setImageResource(imageIDs[position]);
            }
        });

//        if (MyMediaPath.size() > 0) {
//            RelAttachHelp.setVisibility(View.GONE);
//        } else {
//            RelAttachHelp.setVisibility(View.VISIBLE);
//        }
//
//        try {
//            TakedPhotoPath.delete();
//        } catch (Exception e) {
//        }
    }


    private void AddImageToMediaArray(String Path) {
//        AparatUrl = "";
//        linAparatVideoSelectedMsg.setVisibility(View.GONE);

        Bitmap bitmap = ResizeImage.loadBitmap(Path, null,
                ResizeImage.getPhotoSize(), ResizeImage.getPhotoSize(),
                getBaseContext());
        if (bitmap == null && ResizeImage.getPhotoSize() != 800) {
            bitmap = ResizeImage.loadBitmap(Path, null, 800, 800,
                    getBaseContext());
        }

        File tmpFile = new File(Public_Data.tmpAddress);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
            Public_Functions.NoMedia(getBaseContext(), tmpFile.toString(), true);

        }
        Path = Public_Data.tmpAddress
                + "/"
                + ResizeImage.scaleAndSaveImage(bitmap,
                ResizeImage.getPhotoSize(), ResizeImage.getPhotoSize(),
                80, false, 0, 0, Public_Data.tmpAddress);
        if (bitmap != null) {
            bitmap.recycle();
        }

        String NewName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File f = new File(Path);
        File fnewname = new File(Public_Data.tmpAddress + "/" + NewName);
        f.renameTo(fnewname);

        Path = Public_Data.tmpAddress + "/" + NewName;
        MyMediaPath.add(Path);

        // mToast.ShowToast(this, android.R.drawable.ic_dialog_info,
        // getString(R.string.SendingMessage));

        f = null;

    }

    private void get_screenshot(String fileName) {
        try {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fileName,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            if (thumb != null) {
                // save bitmap and get path
                String ScreenPath = Save_Pic(thumb);
                if (ScreenPath != "") {
                    String final_path = get_standardpic(ScreenPath);
                    if (!final_path.equals(""))
                        MyMediaPath.add(final_path);
                    File f = new File(ScreenPath);
                    f.delete();
                }
                thumb.recycle();
            }
        } catch (Exception e) {
        }

    }

    public String Save_Pic(Bitmap bitmap) {
        String res = "";
        // /
        File sdCardDirectory = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/"
                + "PTelegram");
        if (!sdCardDirectory.exists())
            sdCardDirectory.mkdir();
        // /
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(1000000000);
        File image = new File(sdCardDirectory, String.valueOf(randomInt)
                + ".png");

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            if (bitmap != null) {
                bitmap.recycle();
            }
            res = Environment.getExternalStorageDirectory().toString() + "/"
                    + "PTelegram" + "/"
                    + String.valueOf(randomInt) + ".png";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String get_standardpic(String Path) {
        String res = "";
        try {
            Bitmap bitmap = ResizeImage.loadBitmap(Path, null,
                    ResizeImage.getPhotoSize(), ResizeImage.getPhotoSize(),
                    getBaseContext());
            if (bitmap == null && ResizeImage.getPhotoSize() != 800) {
                bitmap = ResizeImage.loadBitmap(Path, null, 800, 800,
                        getBaseContext());
            }

            File tmpFile = new File(Public_Data.tmpAddress);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
                Public_Functions.NoMedia(getBaseContext(), tmpFile.toString(),
                        true);
            }
            res = Public_Data.tmpAddress
                    + "/"
                    + ResizeImage.scaleAndSaveImage(bitmap,
                    ResizeImage.getPhotoSize(),
                    ResizeImage.getPhotoSize(), 80, false, 0, 0,
                    Public_Data.tmpAddress);
            if (bitmap != null) {
                bitmap.recycle();
            }
        } catch (Exception e) {
        }
        return res;
    }

    public class ImageGalleryAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;

        public ImageGalleryAdapter(Context c) {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(
                    R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();

        }

        // returns the number of images
        public int getCount() {
            return MyMediaPath.size();
        }

        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    Gallery.LayoutParams.WRAP_CONTENT,
                    Gallery.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setBackgroundResource(itemBackground);
            File f = new File(MyMediaPath.get(position));
            Picasso.with(context).load(f).into(imageView);
            return imageView;
        }
    }

    boolean iHaveError = false;
    String ListSendedImages = "";
    ProgressDialog PD;
    AlertDialog fragment;

    private void SendPost() {

        fragment = new AlertDialog.Builder(WriteActivity.this)
                .create();
        fragment.setTitle(getString(R.string.ErrorOccurred));

        if (edt_postText.getText().toString().trim().length() > 3
                || MyMediaPath.size() > 0) {
            PD = ProgressDialog
                    .show(WriteActivity.this,
                            getResources().getString(
                                    R.string.Send),
                            getResources().getString(
                                    R.string.SendingPost), true);
//            PD.setCancelable(true);
            PD.setCanceledOnTouchOutside(false);
            PD.show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final List<String> ImagesList = new ArrayList<String>();
                    if (MyMediaPath.size() > 0) {

                        ImagesList.clear();

                        final String Total = String
                                .valueOf(MyMediaPath.size());

                        for (int i = 0; i < MyMediaPath.size(); i++) {
                            if (!iHaveError) {
                                final List<String> tmpFilePath = new ArrayList<String>();
                                tmpFilePath.add(0, MyMediaPath.get(i));

                                final String SendedItem = String
                                        .valueOf(i + 1);

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        PD.setMessage(getString(R.string.SendingPhoto)
                                                + " "
                                                + SendedItem
                                                + " "
                                                + getString(R.string.From)
                                                + " " + Total);
                                        PD.show();
                                    }
                                });

                                SendMessage_Result = WebService_Manager
                                        .SendMediaTemp(getBaseContext(),
                                                tmpFilePath, MYID);
                                if (!(WriteActivity.this
                                        .isFinishing())) {
                                    try {
                                        JsonParser parser = new JsonParser();
                                        JsonObject element = (JsonObject) parser
                                                .parse(SendMessage_Result);

                                        int ResCode = element.get("error")
                                                .getAsInt();
                                        final String ResponseMessage = element
                                                .get("str").getAsString();

                                        if (ResCode == -1) {
                                            // ERROR !
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    iHaveError = true;
                                                    fragment.setTitle(getResources()
                                                            .getString(
                                                                    R.string.ErrorOccurred));
                                                    fragment.setMessage(ResponseMessage);
//                                                    fragment.setCancelable(false);
                                                    PD.dismiss();
                                                    fragment.show();
                                                    return;

                                                }
                                            });
                                        } else {
                                            // Success !-
                                            JsonObject Responsereturnobject = null;
                                            try {
                                                Responsereturnobject = element
                                                        .get("return")
                                                        .getAsJsonObject();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            final String Responsereturn = Responsereturnobject
                                                    .get("MID")
                                                    .getAsString();
                                            ListSendedImages = ListSendedImages
                                                    + "," + Responsereturn;

                                        }

                                    } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                iHaveError = true;
                                                fragment.setTitle("Error");
                                                fragment.setMessage(getResources()
                                                        .getString(R.string.ErrorOccurred));
//                                                fragment.setCancelable(false);
                                                PD.dismiss();
                                                fragment.show();
                                                return;

                                            }
                                        });
                                    }

                                }

                            }
                        }
                    }

                    if (!iHaveError) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                PD.setMessage(getResources().getString(
                                        R.string.Sending_Message));
                            }
                        });


                        if (ListSendedImages.length() > 0) {
                            Images = ListSendedImages.substring(1,
                                    ListSendedImages.length());
                        } else {
                            Images = "";
                        }

                        MainNetwork.SendPost(getBaseContext(), Post_Response, Post_Error, edt_postText.getText().toString(), MYID, Images);


                    }
                }
            }).start();


        } else {

            fragment.setMessage("The Message Is Short");
            fragment.show();


        }

    }
}

