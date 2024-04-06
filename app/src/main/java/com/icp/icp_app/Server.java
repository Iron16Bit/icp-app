package com.icp.icp_app;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    private boolean active;
    public Server() {
        super(8080);
        active = false;
    }

    public void launch() throws IOException {
        if (!active) {
            active = true;
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String answer = "";
        Log.w("HTTPD", session.getUri());
        Log.w("HTTPD", session.getParameters().toString());

        Log.w("HTTPD", "Method is: "+session.getMethod().toString());
        Log.w("HTTPD", "Header is: "+session.getMethod().toString());

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        FileInputStream fis = null;
        try {
            if (session.getUri().equals("/")) {
                fis = new FileInputStream(path + "/MyFiles/index.html");
                Log.w("HTTPD", session.getUri() + " found");
            } else {
                fis = new FileInputStream(path + "/MyFiles/" + session.getUri());
                Log.w("HTTPD", session.getUri() + " found");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String mime = getMimeType(session.getUri());

        Log.w("HTTPD", "MIME is: " + mime);

        return newChunkedResponse(Response.Status.OK, mime, fis);
    }

    public static String getMimeType(String url) {
        if (url.endsWith(".js")) {
            return "text/javascript";
        }

        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }
}
