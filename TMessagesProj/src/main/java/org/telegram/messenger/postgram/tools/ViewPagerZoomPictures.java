package org.telegram.messenger.postgram.tools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ir.android.telegram.post.R;

import java.io.File;

public class ViewPagerZoomPictures extends AppCompatActivity {
    ViewPager mViewPager;
    ProfilePagerAdapter mProfilePagerAdapter;
    static String BaseURL = "";
    int Position = 0;
    static boolean IsProfilePic = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pager);

        mProfilePagerAdapter = new ProfilePagerAdapter(
                getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(mProfilePagerAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BaseURL = extras.getString("BaseURL");
            Position = extras.getInt("position");
            IsProfilePic = extras.getBoolean("isPP");
        }

        mViewPager.setCurrentItem(Position);

    }

    public static class ProfilePagerAdapter extends FragmentStatePagerAdapter {

        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment ZoomInZoomOut = new ZoomInZoomOut();
            Bundle args = new Bundle();
            args.putInt("pos", position);
            ZoomInZoomOut.setArguments(args);
            return ZoomInZoomOut;
        }

        @Override
        public int getCount() {
            return Public_Data.MyPictures.size();

        }

    }

    public static class ZoomInZoomOut extends Fragment {
        private TouchImageView image;
        ImageView img_tumb;
        TextView txt_Loading;
        String ImgURL = "";

        /**
         * Called when the activity is first created.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_single_touchimageview,
                    container, false);
            setHasOptionsMenu(true);

            Bundle args = getArguments();
            int pos = args.getInt("pos");
            String tumbUrl = "";
            try {
                tumbUrl = BaseURL + Public_Data.MyPictures.get(pos).Name;
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            ImgURL = tumbUrl.replace("tn_", "");

            image = (TouchImageView) v.findViewById(R.id.img);
            img_tumb = (ImageView) v.findViewById(R.id.img_tumb);
            txt_Loading = (TextView) v.findViewById(R.id.txt_Loading);
            txt_Loading.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(tumbUrl)
                    .into(img_tumb, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity())
                                    .load(R.drawable.attach_gallery)
                                    .into(image);
                        }
                    });
            Picasso.with(getActivity()).load(ImgURL)
                    .into(image, new Callback() {

                        @Override
                        public void onSuccess() {
                            txt_Loading.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
            return v;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.zoom_in_zoom_out, menu);
//            if (IsProfilePic) {
//                menu.findItem(R.id.action_save).setVisible(false);
//                menu.findItem(R.id.action_share).setVisible(false);
//                menu.findItem(R.id.action_SetAsBG).setVisible(false);
//            }
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.action_save:
//                    Save_Pic();
//                    break;
//                case android.R.id.home:
//                    getActivity().finish();
//                    break;
//                case R.id.action_share:
//                    int res = Save_Pic();
//                    if (res > 0) {
//                        File file = new File(sdCardDirectory, String.valueOf(res)
//                                + ".png");
//                        Uri uri = Uri.fromFile(file);
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.setType("image/*");
//                        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
//                                getResources().getString(R.string.app_name));
//                        shareIntent.putExtra(Intent.EXTRA_STREAM,
//                                uri);
//                        startActivity(shareIntent);
//                    }
//                    break;
//                case R.id.action_SetAsBG:
//                    WallpaperManager myWallpaperManager = WallpaperManager
//                            .getInstance(getActivity());
//                    try {
//                        BitmapDrawable drawable = (BitmapDrawable) image
//                                .getDrawable();
//                        Bitmap bitmap = drawable.getBitmap();
//                        myWallpaperManager.setBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mToast.ShowToast(getActivity(),
//                            android.R.drawable.ic_dialog_info,
//                            getString(R.string.BackupIsSuccess));
//                    break;
//            }
            return super.onOptionsItemSelected(item);
        }

        File sdCardDirectory;

//        public int Save_Pic() {
//            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
//            if (drawable != null) {
//                Bitmap bitmap = drawable.getBitmap();
//
//                // /
//                sdCardDirectory = new File(Environment
//                        .getExternalStorageDirectory().toString()
//                        + "/"
//                        + "PTelegram");
//                if (!sdCardDirectory.exists())
//                    sdCardDirectory.mkdir();
//                // /
//                Random randomGenerator = new Random();
//                int randomInt = randomGenerator.nextInt(1000000000);
//                File image = new File(sdCardDirectory,
//                        String.valueOf(randomInt) + ".png");
//
//                boolean success = false;
//
//                // Encode the file as a PNG image.
//                FileOutputStream outStream;
//                try {
//
//                    outStream = new FileOutputStream(image);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                    /* 100 to keep full quality of the image */
//
//                    outStream.flush();
//                    outStream.close();
//                    success = true;
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (success) {
//
//                    Intent scanIntent = new Intent(
//                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    scanIntent.setData(Uri.fromFile(image));
//                    getActivity().sendBroadcast(scanIntent);
//
//                    MediaScannerConnection
//                            .scanFile(
//                                    getActivity(),
//                                    new String[]{image.toString()},
//                                    null,
//                                    new MediaScannerConnection.OnScanCompletedListener() {
//
//                                        public void onScanCompleted(
//                                                String path, Uri uri) {
//                                            // Log.i("ExternalStorage",
//                                            // "Scanned " + path +
//                                            // ":");
//                                            // Log.i("ExternalStorage",
//                                            // "-> uri=" + uri);
//                                        }
//                                    });
//
//                    mToast.ShowToast(getActivity(),
//                            android.R.drawable.ic_dialog_info, getResources()
//                                    .getString(R.string.save_successfully));
//                    return randomInt;
//                } else {
//                    mToast.ShowToast(getActivity(),
//                            android.R.drawable.ic_dialog_info,
//                            ImgURL.substring(ImgURL.indexOf("_")));
//                    return 0;
//                }
//            }
//            return 0;
//        }
    }
}