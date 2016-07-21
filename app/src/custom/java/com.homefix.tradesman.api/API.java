package com.homefix.tradesman.api;

import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceType;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.Tradesman;

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

    @GET("service/types")
    Call<List<ServiceType>> getServiceTypes(@Query("token") String token);

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
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("problem_name") String problem_name,
            @Query("start_time") String start_time,
            @Query("end_time") String end_time,
            @Query("tradesman_notes") String tradesman_note);

}
