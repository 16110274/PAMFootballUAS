package corp.demo.sportapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Server on 13/09/2017.
 */

public class RetroServer {

    private  static  final String base_url = "https://naferofruid.000webhostapp.com/API/";
    public static final String img_url = "https://naferofruid.000webhostapp.com/API/login.php/Uploads/";

    private static Retrofit retrofit;


    public static Retrofit getClient()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return  retrofit;
    }
    // Mendeklarasikan Interface BaseApiService
    public static RestApi getAPIService(){
        return RetrofitClient.getClient(base_url).create(RestApi.class);
    }

}
