package org.rowanacm.android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AcmClient {

    @GET("sign-in")
    Call<AttendanceResult> signIn(@Query("token") String googleToken);


    @GET("set-committees")
    Call<AttendanceResult> setCommittees(@Query("token") String googleToken,
                                         @Query("committees") String committees);

}
