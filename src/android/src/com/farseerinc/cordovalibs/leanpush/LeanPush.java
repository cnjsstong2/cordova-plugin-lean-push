package com.farseerinc.cordovalibs.leanpush;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Override;
import java.util.Iterator;

public class LeanPush extends CordovaPlugin {

    private static final String TAG = "LeanPush Plugin";

    private static CordovaWebView mWebView;
    private static String mECB;
    private static String gCachedJSONString = null;

    private static boolean gForeground = false;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d("LeanPush", "initialize");
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
                if (e == null) {
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.d("LeanPush", "got installationId: " + installationId);
                } else {
                    Log.d("LeanPush", "fail to get installationId", e);
                }
            }
        });
        PushService.setDefaultPushCallback(cordova.getActivity(), LeanPushHandlerActivity.class);
        mECB = null;
        gForeground = true;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstallation")) {
            return this.getInstallation(callbackContext);
        } else if (action.equals("register")) {
            return this.register(args, callbackContext);
        }
        return false;
    }

    private boolean getInstallation(final CallbackContext callbackContext) {
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
        if (installationId == null) {
            callbackContext.error("Fail to get Installation.");
        } else {
            callbackContext.success("android," + installationId);
        }
        return true;
    }

    private boolean register(JSONArray data, final CallbackContext callbackContext) {

        Log.d(TAG, "execute: data=" + data.toString());

        try {
            JSONObject jo = data.getJSONObject(0);
            mWebView = this.webView;
            mECB = (String) jo.get("ecb");
            callbackContext.success();
        } catch (JSONException e) {
            Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
            callbackContext.error(e.getMessage());
        }
        if (gCachedJSONString != null) {
            Log.d(TAG, "sending cached extras");
            sendJSONString(gCachedJSONString);
            gCachedJSONString = null;
        }
        return true;
    }

    /*
     * Sends a json object to the client as parameter to a method which is defined in mECB.
     */
    public static void sendJSONString(String _jsonString) {
        String _d = "javascript:" + mECB + "(" + _jsonString + ")";
        Log.d(TAG, "sendJavascript: " + _d);

        if (mECB != null && mWebView != null) {
            mWebView.sendJavascript(_d);
        } else {
            gCachedJSONString = _jsonString;
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        gForeground = false;
        final NotificationManager notificationManager = (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        gForeground = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gForeground = false;
        mECB = null;
        mWebView = null;
    }

    public static boolean isActive() {
        return mWebView != null;
    }

    public static boolean isInForeground() {
        return gForeground;
    }

    /**
     * Gets the application context from cordova's main activity.
     *
     * @return the application context
     */
    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

}
