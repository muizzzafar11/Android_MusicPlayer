package com.example.musicplayer;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

class Permissions {
    private static final Integer READ_STORAGE_PERMISSION_REQUEST_CODE = 0;
    private static final Integer MODIFY_AUDIO_SETTINGS_REQUEST_CODE = 1;


    private Activity activity;
    Permissions(Activity _activity) {
        activity = _activity;
    }

    boolean readExternalStoragePermission() {
        if(!hasPermission())
            askPermission();
        return hasPermission();
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(result == PackageManager.PERMISSION_GRANTED)
                result = activity.checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_STORAGE_PERMISSION_REQUEST_CODE);

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                MODIFY_AUDIO_SETTINGS_REQUEST_CODE);

    }
}

