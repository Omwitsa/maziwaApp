package com.willy.maziwa;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Alberto on 1/18/2016.
 */
public interface RegisterAPI {

    @FormUrlEncoded
    @POST("/webservice/albert/RegisterFarmer.php")
    public void loginUser(
            @Field("fullname") String fullname,
            @Field("id_no") String id_no,
            @Field("phone_no") String phone_no,
            @Field("bbranch") String bbranch,
            @Field("bank") String bank,
            @Field("account") String account,
            @Field("branch") String branch,
            @Field("location") String location,
            Callback<ArrayList<User>> callback);
}
