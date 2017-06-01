package org.rowanacm.android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AttendanceClient {

    @GET("prod/attendance")
    Call<Integer> signIn(@Query("uid") String uid,
                         @Query("name") String name,
                         @Query("email") String email,
                         @Query("method") String method);

}
