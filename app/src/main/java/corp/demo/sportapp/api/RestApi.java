package corp.demo.sportapp.api;

import corp.demo.sportapp.model.ResponseModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface RestApi {
    //inser
    @FormUrlEncoded
    @POST("insert.php")
    Call<ResponseModel> sendBiodata(@Field("nama") String nama,
                                    @Field("harga") String harga,
                                    @Field("foto") String foto);

    //read
    @GET("read.php")
    Call<ResponseModel> getData();

    //update menggunakan 3 parameter
    @FormUrlEncoded
    @POST("update.php")
    Call<ResponseModel> updateData(@Field("id") String id,
                                   @Field("nama") String nama,
                                   @Field("harga") String harga);

    //delete menggunakan parameter id
    @FormUrlEncoded
    @POST("delete.php")
    Call<ResponseModel> deleteData(@Field("id") String id);


    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/login.php
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);

    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/register.php
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);
}