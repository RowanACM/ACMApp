package org.rowanacm.android;

import org.rowanacm.android.announcement.Announcement;
import org.rowanacm.android.authentication.UserInfo;
import org.rowanacm.android.model.AttendanceResult;
import org.rowanacm.android.model.ServerResponse;

import java.util.List;

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


    @GET("get-announcements")
    Call<List<Announcement>> getAnnouncements();

    @GET("post-announcement")
    Call<ServerResponse> postAnnouncement(@Query("token") String googleToken,
                                          @Query("title") String title,
                                          @Query("body") String body,
                                          @Query("committee") String committee,
                                          @Query("also_post_on_slack") boolean alsoPostOnSlack);



}
