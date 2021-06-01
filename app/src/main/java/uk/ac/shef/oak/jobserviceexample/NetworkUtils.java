package uk.ac.shef.oak.jobserviceexample;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    //private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String BACKEND_BASE_URL_EMULATOR = "http://10.0.2.2:5000/getjobs/emulator";
    private static final String BACKEND_BASE_URL_HARDWARE = "http://192.168.100.7:5000/getjobs/hardware";
    private static final String BACKEND_BASE_URL_POST_EMULATOR = "http://10.0.2.2:5000/postresults";
    private static final String BACKEND_BASE_URL_POST_HARDWARE = "http://192.168.100.7:5000/postresults";

    public static int checkDevice() {
        if (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google"
                && Build.PRODUCT.startsWith("sdk_gphone_")
                && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
        ) {
            return 1;
        } else {
            return 2;
        }
    }
    public static String getPingInfo() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String pingJSONString = null;

        try {
            // URL
            URL requestURL = null;
            int x = checkDevice();
            if (x == 1) {
                requestURL = new URL(BACKEND_BASE_URL_EMULATOR);
            } else if (x == 2) {
                requestURL = new URL(BACKEND_BASE_URL_HARDWARE);
            }

            // Open network connection
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Input stream
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // StringBuilder for the incoming response
            StringBuilder builder = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if(builder.length() == 0) {
                //Stream was empty. Exit without parsing
                return null;
            }

            pingJSONString = builder.toString();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pingJSONString;
    }

    static String postPingInfo(String ping) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL requestUrl = null;
        int x = checkDevice();
        Log.i("checkdeviceresult", String.valueOf(x));
        String result = "";
        if (x == 1) {
            requestUrl = new URL(BACKEND_BASE_URL_POST_EMULATOR);
        } else if (x == 2) {
            requestUrl = new URL(BACKEND_BASE_URL_POST_HARDWARE);
        }
        Log.i("requsturl", requestUrl.toString());
        urlConnection = (HttpURLConnection) requestUrl.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        urlConnection.setRequestProperty("Accept", "text/plain; charset=utf-8");
        urlConnection.setDoOutput(true);

        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", "test");
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        }
        else {
            return "NOT OK";
        }
    }
}
