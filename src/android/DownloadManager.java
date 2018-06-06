package downloadmanager;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * This class echoes a string called from JavaScript.
 */
public class DownloadManager extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("download")) {
            String message = args.getString(0);
            String token = null;
            JSONObject options = args.getJSONObject(1);
            if(options.has("token")) {
                token = options.getString("token");
            }
            String filename = null;
            if(options.has("filename")) {
                filename = options.getString("filename");
            }
            String description = "Download";
            if(options.has("description")) {
                description = options.getString("description");
            }
            String mimeType = null;
            if(options.has("description")) {
                mimeType = options.getString("mimeType");
            }
            this.startDownload(message, token, filename, description, mimeType, callbackContext);
            return true;
        }
        return false;
    }

    private void startDownload(String message, String token, String filename, String description, String mimeType, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            if(filename == null) {
                filename = message.substring(message.lastIndexOf("/")+1, message.length());
            }
            try {
                filename = URLDecoder.decode(filename,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                callbackContext.error("Error in converting filename");
            }
            android.app.DownloadManager downloadManager = (android.app.DownloadManager) cordova.getActivity().getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);            
            Uri Download_Uri = Uri.parse(message);
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Download_Uri);
            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI | android.app.DownloadManager.Request.NETWORK_MOBILE);
            //Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(true);
            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(filename);
            //Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription(description);
            //Set the local destination for the downloaded file to a path within the application's external files directory            
            request.setDestinationInExternalFilesDir(cordova.getActivity().getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, filename);
            //Set visiblity after download is complete
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            //add request header
            if(token != null && !token.isEmpty()) {
                request.addRequestHeader("Authorization", "Bearer " + token);
            }
            
            //set mime type
            if(mimetype != null && !mimetype.isEmpty()) {
                request.setMimeType(mimetype);
            }
            
            long downloadReference = downloadManager.enqueue(request);
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
