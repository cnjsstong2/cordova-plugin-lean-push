package com.farseerinc.cordovalibs.leanpush;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.SaveCallback;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LeanPush extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstallation")) {
            this.getInstallation(callbackContext);
            return true;
        }
        return false;
    }

    private void getInstallation(final CallbackContext callbackContext) {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    callbackContext.success("android," + installationId);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    callbackContext.error("Fail to get Installation.");
                }
            }
        });
    }
}
