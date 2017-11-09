package com.willy.maziwa;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Alberto on 1/27/2016.
 */
public interface syncAPI {

    @FormUrlEncoded
    @POST("/webservice/albert/RegisterFarmer.php")
    public void sync(
            @Field("sno") String sno,
            @Field("quantity") String quantity,
            //@Field("phone_no") String phone_no,

            Callback<ArrayList<SyncCall>> callback);
}
