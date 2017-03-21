package com.unated.askincht_beta.Network;

import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface NetworkServiceSimple {

    @GET(Constants.API.DEV)
    Call<SimpleResponse> login(@Query("sid") String sid);
}
