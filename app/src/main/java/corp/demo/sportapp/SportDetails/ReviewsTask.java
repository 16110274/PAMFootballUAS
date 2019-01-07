package corp.demo.sportapp.SportDetails;
import android.os.AsyncTask;
import android.util.Log;

import corp.demo.sportapp.BuildConfig;
import corp.demo.sportapp.SportComponents.Reviews;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsTask extends AsyncTask<Long, Void, List<Reviews>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = ReviewsTask.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when reviews are loaded.
     */
    interface Listener {
        void on_reviews_loaded(List<Reviews> reviews);
    }
    public ReviewsTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Reviews> doInBackground(Long... params) {
        List<Reviews> reviews = new ArrayList<>();
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        try {

            URL url;
            url = new URL("https://www.thesportsdb.com/api/v1/json/1/eventsnext.php?id="+movieId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = IOUtils.toString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            parseJson(results,reviews);
            inputStream.close();
            return reviews;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void parseJson(String data, List<Reviews> list){
        Reviews reviews = new Reviews();

        try {
            JSONObject mainObject = new JSONObject(data);
            Log.v(LOG_TAG,mainObject.toString());
            JSONArray resArray = mainObject.getJSONArray("events"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                reviews.setmId(jsonObject.getString("idEvent"));
                reviews.setmAuthor(jsonObject.getString("strFilename"));
                reviews.setmContent(jsonObject.getString("dateEvent"));
                //Log.e(LOG_TAG, jsonObject.toString());
                list.add(reviews);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error occurred during JSON Parsing");
        }

    }

    @Override
    protected void onPostExecute(List<Reviews> reviews) {
        if (reviews != null) {
            mListener.on_reviews_loaded(reviews);
        } else {
            mListener.on_reviews_loaded(new ArrayList<Reviews>());
        }
    }
}