package corp.demo.sportapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import corp.demo.sportapp.Upcoming;
import corp.demo.sportapp.Upcoming;
import corp.demo.sportapp.databaseSQLITE.SportDbHelper;

public class UpcomingUtils {

    private static final String TAG = UpcomingUtils.class.getSimpleName();
    private static SportDbHelper mOpenHelper;

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static ArrayList<Upcoming> fetchData(String url) throws IOException {
        ArrayList<Upcoming> sports = new ArrayList<Upcoming>();
        try {

            URL new_url = new URL(url); //create a url from a String
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = IOUtils.toString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            parseJson(results,sports);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sports;
    }

    public static void parseJson(String data, ArrayList<Upcoming> list){


        try {
            JSONObject mainObject = new JSONObject(data);
            Log.v(TAG,mainObject.toString());
            JSONArray resArray = mainObject.getJSONArray("events"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                Upcoming upcoming = new Upcoming(); //New Movie object
                upcoming.setEvent_id(jsonObject.getLong("idEvent"));
                upcoming.setEvent_name(jsonObject.getString("strEvent"));
                upcoming.setEvent_competition(jsonObject.getString("strLeague"));
                upcoming.setEvent_time(jsonObject.getString("strTime"));
                upcoming.setEvent_date(jsonObject.getString("dateEvent"));
                //Adding a new movie object into ArrayList
                list.add(upcoming);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing");
        }

    }


    public static Boolean networkStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

}
