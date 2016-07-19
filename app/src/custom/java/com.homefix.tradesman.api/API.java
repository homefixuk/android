package com.homefix.tradesman.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by samuel on 7/19/2016.
 */

public interface API {

    @POST("signup")
    Call<HashMap<String, Object>> signup(
            @Query("apikey") String apikey,
            @Query("firstName") String firstName,
            @Query("lastName") String lastName,
            @Query("email") String email,
            @Query("password") String password,
            @Query("role") String role);

    @POST("login")
    Call<HashMap<String, Object>> login(
            @Query("apikey") String apikey,
            @Query("email") String email,
            @Query("password") String password);

    @GET("tradesman/me")
    Call<com.homefix.tradesman.model.Tradesman> getTradesman(@Query("token") String token);

    @POST("tradesman/me")
    Call<com.homefix.tradesman.model.Tradesman> updateTradesmanDetails(@Query("token") String token, @QueryMap Map<String, Object> params);

    @GET("tradesman/timeslots")
    Call<List<com.homefix.tradesman.model.Timeslot>> getTradesmanEvents(@Query("token") String token, @Query("filter") Map<String, Object> filter);

    @GET("cca")
    Call<com.homefix.tradesman.model.CCA> getCCA(@Query("apikey") String apikey, @Query("token") String token);

    @POST("tradesman/location")
    Call<com.homefix.tradesman.model.Timeslot> updateLocation(@Query("token") String token, @QueryMap Map<String, Object> location);

    @POST("tradesman/timeslot")
    Call<com.homefix.tradesman.model.Timeslot> addTimeslot(@Query("token") String token, @Query("time_slot") com.homefix.tradesman.api.HomeFix.TimeslotMap timeslotMap);

    @PATCH("tradesman/timeslot")
    Call<com.homefix.tradesman.model.Timeslot> updateTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id,
            @Query("time_slot") com.homefix.tradesman.api.HomeFix.TimeslotMap timeslotMap);

    @DELETE("tradesman/timeslot")
    Call<Map<String, Object>> deleteTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id);
}
