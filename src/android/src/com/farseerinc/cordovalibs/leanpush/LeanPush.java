package com.farseerinc.cordovalibs.leanpush;

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

public class LeanPush extends CordovaPlugin {

    private static CordovaWebView mWebView;
    private static String mECB;

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
        PushService.setDefaultPushCallback(cordova.getActivity(), cordova.getActivity().getClass());
        mECB = null;
        mWebView = this.webView;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstallation")) {
            this.getInstallation(callbackContext);
            return true;
        }
        return false;
    }

    private void getInstallation(final CallbackContext callbackContext) {
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
        if (installationId == null) {
            callbackContext.error("Fail to get Installation.");
        } else {
            callbackContext.success("android," + installationId);
        }
    }
}
