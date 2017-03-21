package com.unated.askincht_beta.Network;

import com.unated.askincht_beta.Pojo.BusMessages.ExactReq;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



public interface NetworkServiceArt {


    @POST("api.php")
    @FormUrlEncoded
    Call<ExactReq> getExactRequests(@Field("sid") String sid,@Field("action") String action,@Field("text") String text);
}
