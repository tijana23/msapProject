package uk.ac.shef.oak.jobserviceexample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncBackend extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... params) {
        String pingInfo = HttpPost.getPingInfo();
        try {
            Log.i("onPostTAG", " result = " + pingInfo);
            JSONArray jsonArray = new JSONArray(pingInfo);
            JSONObject object = null;
            if (jsonArray.length() == 1) {
                object = jsonArray.getJSONObject(0);
            }
            try {
                String date = object.getString("date");
                String host = object.getString("host");
                String count = String.valueOf(object.getInt("count"));
                String packetSize = String.valueOf(object.getInt("packetSize"));
                String jobPeriod = String.valueOf(object.getInt("jobPeriod"));
                String jobType = object.getString("jobType");
                Log.i("MY_TAG", date + host + count + packetSize + jobPeriod + jobType);

                try {
                    // CHANGE
                    // TODO
                    //String pingCmd = jobType.toLowerCase() + " -s " + packetSize + " -c " + count + " -i " + jobPeriod + " " + host;
                    String pingCmd = jobType.toLowerCase() + " -s " + packetSize + " -c " + count + " " + host;
                    String pingResult = "";
                    Runtime r = Runtime.getRuntime();
                    Process p = r.exec(pingCmd);
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        pingResult += inputLine;
                    }
                    Log.i("tagggg", pingResult);
                    in.close();
                    //String postResult = HttpPost.post();
                    //Log.i("POSTRESULT", postResult);
                    String postResponse = HttpPost.post(pingResult);
                    return postResponse;
                } catch (IOException e) {
                    System.out.println(e);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "There is an error!";
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i("insidePostExecute", s);
        super.onPostExecute(s);
    }
}
