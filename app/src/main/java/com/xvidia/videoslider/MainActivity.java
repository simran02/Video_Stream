package com.xvidia.videoslider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

//    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_TAKE_PHOTO = 0;
    public static final int REQUEST_TAKE_VIDEO = 1;
    public static final int REQUEST_PICK_PHOTO = 2;
    public static final int REQUEST_PICK_VIDEO = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    private Uri mMediaUri;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // ActivityContext = this;

    }

    @Override
    //function to check if photo/video has been recorded otherwise toast with error message is displayed.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_TAKE_PHOTO) {
//                Intent intent = new Intent(this, ViewImageActivity.class);
//                intent.setData(mMediaUri);
//                startActivity(intent);
//            }
            // else
            if (requestCode == REQUEST_TAKE_VIDEO) {

                //output.setText("Video File : " + data.getData());
                Toast.makeText(this, "Video saved to:" +
                        data.getData(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Intent.ACTION_VIEW,mMediaUri);
//                        intent.setDataAndType(mMediaUri,"video/*");
//                       startActivity(intent);
            }
        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }


//    @OnClick(R.id.takePhoto)
//    void takePhoto() {
//        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//        if (mMediaUri == null) {
//            Toast.makeText(this,
//                    "There was a problem accessing your device's external storage.",
//                    Toast.LENGTH_LONG).show();
//        }
//        else {
//            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
//        }
//    }

    void takeVideo(View view) {
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            Toast.makeText(this,
                    "There was a problem accessing your device's external storage.",
                    Toast.LENGTH_LONG).show();
        } else {
            //checks for rtp if the app has to access camera
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(this, ALLOW_KEY)) {

                    showSettingsAlert();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation to make the user understand that permission is required
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                        showAlert();
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            } else {
                openCamera();
            }
        }
    }

    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(MainActivity.this);

                    }
                });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (this, permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(MainActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }

        }
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(i);
    }

    private void openCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
    }


//    @OnClick(R.id.pickPhoto)
//    void pickPhoto() {
//
//    }

    void pickVideo() {

    }

    private  Uri getOutputMediaFileUri(int mediatype){

        return Uri.fromFile(getOutputMediaFile(mediatype));
    }


    private File getOutputMediaFile(int mediaType) {
        // check for external storage
        if (isExternalStorageAvailable()) {
            // get the URI

            // 1. Get the external storage directory
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyCameraVideo");
//            // Create the storage directory(MyCameraVideo) if it does not exist
//            if (!mediaStorageDir.exists()) {
//
//                if (!mediaStorageDir.mkdirs()) {
//
//                    // output.setText("Failed to create directory MyCameraVideo.");
//
//                    Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
//                            Toast.LENGTH_LONG).show();
//
//                    Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
//                    return null;
//                }
//            }
            // 2. Create a unique file name
            String fileName = "";
            String fileType = "";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if (mediaType == MEDIA_TYPE_IMAGE) {
                fileName = "IMG_" + timeStamp;
                fileType = ".jpg";
            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                fileName = "VID_" + timeStamp;
                fileType = ".mp4";
            } else {
                return null;
            }

            // 3. Create the file
            File mediaFile;
            if (mediaType == MEDIA_TYPE_VIDEO) {

                // For unique video file name appending current timeStamp with file name
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + timeStamp + ".mp4");

            } else {
                return null;
            }

            return mediaFile;
        }


        // something went wrong
        return null;
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void play_video (View view){
        Intent intent  = new Intent(this,VideoActivity.class);

        startActivity(intent);
    }

//    public void playbackRecordedVideo() {
//        VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
//        mVideoView.setVideoURI(videoUri);
//        mVideoView.setMediaController(new MediaController(this));
//        mVideoView.requestFocus();
//        mVideoView.start();
    }

}




