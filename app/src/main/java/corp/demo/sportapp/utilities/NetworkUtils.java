package corp.demo.sportapp.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import corp.demo.sportapp.Sport;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import corp.demo.sportapp.databaseSQLITE.SportDbHelper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static SportDbHelper mOpenHelper;

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static ArrayList<Sport> fetchData(String url) throws IOException {
        ArrayList<Sport> sports = new ArrayList<Sport>();
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

    public static void parseJson(String data, ArrayList<Sport> list){


        try {
            JSONObject mainObject = new JSONObject(data);
            Log.v(TAG,mainObject.toString());
            JSONArray resArray = mainObject.getJSONArray("teams"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                Sport sport = new Sport(); //New Movie object
                sport.setId_team(jsonObject.getLong("idTeam"));
                sport.setTeam_name(jsonObject.getString("strTeam"));
                sport.setTeam_stadium(jsonObject.getString("strStadiumThumb"));
                sport.setTeam_description(jsonObject.getString("strDescriptionEN"));
                sport.setTeam_formed(jsonObject.getString("intFormedYear"));
                sport.setTeam_badge(jsonObject.getString("strTeamBadge"));
                //Adding a new movie object into ArrayList
                list.add(sport);
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
