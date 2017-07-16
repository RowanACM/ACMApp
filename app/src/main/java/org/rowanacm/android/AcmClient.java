package org.rowanacm.android;

import org.rowanacm.android.authentication.UserInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AcmClient {

    @GET("sign-in")
    Call<AttendanceResult> signIn(@Query("token") String googleToken);


    @GET("set-committees")
    Call<ServerResponse> setCommittees(@Query("token") String googleToken,
                                       @Query("committees") String committees);


    @GET("get-user-info")
    Call<UserInfo> getUserInfo(@Query("token") String googleToken);

}
