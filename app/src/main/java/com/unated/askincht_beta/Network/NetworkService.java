package com.unated.askincht_beta.Network;

import com.unated.askincht_beta.Pojo.AuthRegister;
import com.unated.askincht_beta.Pojo.AuthResponse;
import com.unated.askincht_beta.Pojo.BalanceResponse;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.ElectResponse;
import com.unated.askincht_beta.Pojo.IsSocialResponse;
import com.unated.askincht_beta.Pojo.RecallsResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.ShopResponse;
import com.unated.askincht_beta.Pojo.MessagesResponse;
import com.unated.askincht_beta.Pojo.MyRequestResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.ProfileResponse;
import com.unated.askincht_beta.Pojo.RequestResponse;
import com.unated.askincht_beta.Pojo.ShopsResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Pojo.Unread;
import com.unated.askincht_beta.Pojo.UserRequestResponse;
import com.unated.askincht_beta.Utils.Constants;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface NetworkService {

    @POST(Constants.API.AUTH)
    @FormUrlEncoded
    Call<AuthResponse> login(@Field("phone") String email, @Field("pw") String pw);

    @POST(Constants.API.LOGOUT)
    @FormUrlEncoded
    Call<LogoutResponse> logout(@Field("sid") String sid, @Field("token") String token);
    @POST(Constants.API.REFRESH)
    @FormUrlEncoded
    Call<RefreshResponse> refresh(@Field("sid") String sid, @Field("token") String token);

    @POST(Constants.API.GET_MY_REQUESTS)
    @FormUrlEncoded
    Call<MyRequestResponse> getMyRequestList(@Field("sid") String sid, @Field("all") boolean bool, @Field("token") String token);

    @POST(Constants.API.REGISTER_DEVICE)
    @FormUrlEncoded
    Call<AuthResponse> devRegistration(@Field("deviceid") String device_id, @Field("token") String token);

    @POST(Constants.API.GET_REQUESTS_MESSAGES)
    @FormUrlEncoded
    Call<MessagesResponse> getRequestMessages(@Field("sid") String sid, @Field("shop_id") String shop_id, @Field("request_id") String request_id, @Field("token") String token);

    @POST(Constants.API.GET_REQUESTS)
    @FormUrlEncoded
    Call<UserRequestResponse> getUserRequests(@Field("sid") String sid, @Field("all") boolean bool, @Field("token") String token);

    @POST(Constants.API.SAVE_TOKEN)
    @FormUrlEncoded
    Call<SimpleResponse> saveToken(@Field("sid") String sid, @Field("push_token") String push_token, @Field("token") String token);

    @POST(Constants.API.GET_MY_SHOP)
    @FormUrlEncoded
    Call<MyShopResponse> getMyShop(@Field("sid") String sid, @Field("token") String token);

    @POST(Constants.API.SET_READ)
    @FormUrlEncoded
    Call<SimpleResponse> setRead(@Field("sid") String sid, @Field("shop_id") String shop_id, @Field("request_id") String request_id, @Field("token") String token);

    @POST(Constants.API.GET_PROFILE)
    @FormUrlEncoded
    Call<ProfileResponse> getProfile(@Field("sid") String sid, @Field("token") String token);

    @POST(Constants.API.GET_REQUEST_SHOPS)
    @FormUrlEncoded
    Call<ShopsResponse> getShops(@Field("sid") String sid, @Field("request_id") String request_id, @Field("all") boolean bool, @Field("token") String token);

    @POST(Constants.API.GET_RECALL)
    @FormUrlEncoded
    Call<RecallsResponse> getRecall(@Field("sid") String sid, @Field("shop_id") String shop_id/*, @Field("all") boolean bool*/, @Field("token") String token);


    @POST(Constants.API.SET_RECALL)
    @FormUrlEncoded
    Call<RecallsResponse> setRecall(@Field("sid") String sid, @Field("shop_id") int shop_id, @Field("request_id") int request_id, @Field("text") String text, @Field("rating") float rating, @Field("token") String token);

    @POST(Constants.API.DELETE_REQUEST)
    @FormUrlEncoded
    Call<SimpleResponse> deleteRequest(@Field("sid") String sid, @Field("request_id") String request_id, @Field("token") String token);

    @POST(Constants.API.DELETE_MY_REQUEST)
    @FormUrlEncoded
    Call<SimpleResponse> deleteMyRequest(@Field("sid") String sid, @Field("request_id") String request_id, @Field("token") String token);
    @POST(Constants.API.GET_UNREADCOUNTS)
    @FormUrlEncoded
    Call<Unread> getUnreadCounts(@Field("sid") String sid, @Field("token") String token);
    @POST(Constants.API.GET_ELECT)
    @FormUrlEncoded
    Call<ElectResponse> getElect(@Field("sid") String sid, @Field("token") String token);
    @POST(Constants.API.SET_ELECT)
    @FormUrlEncoded
    Call<SimpleResponse> setElect(@Field("sid") String sid,@Field("shop_id") int shop_id, @Field("token") String token);
    @POST(Constants.API.UNSET_ELECT)
    @FormUrlEncoded
    Call<SimpleResponse> unSetElect(@Field("sid") String sid,@Field("shop_id") int shop_id, @Field("token") String token);
    @POST(Constants.API.REQUEST)
    @FormUrlEncoded
    Call<MyRequestResponse> getMyRequest(@Field("sid") String sid, @Field("request_id") int request_id, @Field("token") String token);
    @POST(Constants.API.REQUEST)
    @FormUrlEncoded
    Call<RequestResponse> getRequest(@Field("sid") String sid, @Field("request_id") int request_id, @Field("token") String token);
    @POST(Constants.API.LOAD)
    @FormUrlEncoded
    Call<SimpleResponse> getFile(@Field("shop_id") int shop_id, @Field("request_id") int request_id, @Field("file_name") String file_name, @Field("token") String token);
    @POST(Constants.API.GET_MY_SHOP)
    @FormUrlEncoded
    Call<ShopResponse> getShop(@Field("sid") String sid, @Field("shop_id") int shop_id, @Field("token") String token);
    @POST(Constants.API.AUTH_REGISTER)
    @FormUrlEncoded
    Call<AuthRegister> registerUser(@Field("phone") String phone, @Field("pw") String pw, @Field("code") String code);
    @POST(Constants.API.BALANCE)
    @FormUrlEncoded
    Call<BalanceResponse> getBalance(@Field("sid") String sid, @Field("token") String token);
    @POST(Constants.API.BALANCE_SHOP)
    @FormUrlEncoded
    Call<BalanceResponse> getShopBalance(@Field("sid") String sid, @Field("token") String token);
    @POST(Constants.API.AUTH_SOCIAL)
    @FormUrlEncoded
    Call<SimpleResponse> setSocial(@Field("sid") String sid,@Field("id") String id,@Field("type") String type, @Field("token") String token);
    @POST(Constants.API.AUTH_isSOCIAL)
    @FormUrlEncoded
    Call<IsSocialResponse> isSocial(@Field("sid") String sid, @Field("type") String type, @Field("token") String token);
    @POST(Constants.API.RESTORE)
    @FormUrlEncoded
    Call<SimpleResponse> setNewPass(@Field("pw") String pwd,@Field("code") String code, @Field("token") String token);

    @Multipart
    @POST(Constants.API.UPLOAD)
    Call<ResponseBody> sendFiles(@Part("token") RequestBody sid, @Part("request_id")RequestBody request_id, @Part("shop_id")RequestBody shop_id, @Part("uniq")RequestBody uniq, @Part MultipartBody.Part file);
}
