package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class ForceUpdateChecker {


    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "app_current_version";
    public static final String KEY_UPDATE_URL = "app_play_store_url";
    public static final String CLIENT_KEY = "client_key";
    public static final String API_SECRET = "secret_key";
    private static final String TAG = ForceUpdateChecker.class.getSimpleName();
    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
            String appVersion = getAppVersion(context);
            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            if (!TextUtils.equals(currentVersion, appVersion)
                    && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl);
            }
        }
    }
    public void getRemoteKeys(){
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    }
    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl);
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();
            forceUpdateChecker.getRemoteKeys();

            return forceUpdateChecker;
        }
    }
}