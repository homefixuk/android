package com.homefix.tradesman.api;

import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.Tradesman;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clojure.lang.Obj;
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

    @POST("/user/signup")
    Call<HashMap<String, Object>> signup(
            @Query("apikey") String apikey,
            @Query("firstName") String firstName,
            @Query("lastName") String lastName,
            @Query("email") String email,
            @Query("password") String password,
            @Query("role") String role);

    @POST("/user/login")
    Call<HashMap<String, Object>> login(
            @Query("apikey") String apikey,
            @Query("email") String email,
            @Query("password") String password);

    @GET("/tradesman/me")
    Call<Tradesman> getTradesman(@Query("token") String token);

    @POST("/tradesman/me")
    Call<Tradesman> updateTradesmanDetails(@Query("token") String token, @QueryMap Map<String, Object> params);

    @GET("/tradesman/timeslots")
    Call<List<Timeslot>> getTradesmanEvents(@Query("token") String token, @Query("filter") Map<String, Object> filter);

    @GET("/cca")
    Call<CCA> getCCA(@Query("apikey") String apikey, @Query("token") String token);

    @POST("/tradesman/location")
    Call<Timeslot> updateLocation(@Query("token") String token, @QueryMap Map<String, Object> location);

    @POST("/tradesman/timeslot")
    Call<Timeslot> addTimeslot(@Query("token") String token, @Query("time_slot") HomeFix.TimeslotMap timeslotMap);

    @PATCH("/tradesman/timeslot")
    Call<Timeslot> updateTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id,
            @Query("time_slot") HomeFix.TimeslotMap timeslotMap);

    @DELETE("/tradesman/timeslot")
    Call<Map<String, Object>> deleteTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id);

    @GET("/service/types")
    Call<List<Problem>> getServiceTypes(@Query("token") String token);

    @POST("/service")
    Call<Service> createService(
            @Query("token") String token,
            @Query("customer_name") String customer_name,
            @Query("customer_email") String customer_email,
            @Query("customer_phone") String customer_phone,
            @Query("customer_property_relationship") String customer_property_relationship,
            @Query("address_line_1") String address_line_1,
            @Query("address_line_2") String address_line_2,
            @Query("address_line_3") String address_line_3,
            @Query("postcode") String postcode,
            @Query("country") String country,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("problem_name") String problem_name,
            @Query("start_time") double start_time,
            @Query("end_time") double end_time,
            @Query("tradesman_notes") String tradesman_note);

    @PATCH("/service")
    Call<Service> updateService(
            @Query("id") String timeslotId,
            @Query("token") String token,
            @Query("changes") Map<String, Object> changes);

}
