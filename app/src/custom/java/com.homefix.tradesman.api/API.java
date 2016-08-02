package com.homefix.tradesman.api;

import com.homefix.tradesman.model.Activity;
import com.homefix.tradesman.model.Attachment;
import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Payment;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceStatusFlow;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.TradesmanFinances;
import com.homefix.tradesman.model.TradesmanNotification;
import com.homefix.tradesman.model.TradesmanReview;

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

    @POST("user/signup")
    Call<HashMap<String, Object>> signup(
            @Query("apikey") String apikey,
            @Query("firstName") String firstName,
            @Query("lastName") String lastName,
            @Query("email") String email,
            @Query("password") String password,
            @Query("role") String role);

    @POST("user/login")
    Call<HashMap<String, Object>> login(
            @Query("apikey") String apikey,
            @Query("email") String email,
            @Query("password") String password);

    @DELETE("user/logout")
    Call<HashMap<String, Object>> logout(@Query("token") String token);

    @GET("tradesman/me")
    Call<Tradesman> getTradesman(@Query("token") String token);

    @PATCH("tradesman/me")
    Call<Tradesman> updateTradesmanDetails(
            @Query("token") String token,
            @QueryMap Map<String, Object> params);

    @GET("tradesman/me/private")
    Call<Tradesman> getTradesmanPrivate(
            @Query("apikey") String apikey,
            @Query("token") String token);

    @PATCH("tradesman/me/private")
    Call<Tradesman> updateTradesmanPrivateDetails(
            @Query("token") String token,
            @QueryMap Map<String, Object> params);

    @POST("tradesman/location")
    Call<Timeslot> updateLocation(
            @Query("token") String token,
            @QueryMap Map<String, Object> location);

    @GET("tradesman/notifications")
    Call<List<TradesmanNotification>> getTradesmanNotifications(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("tradesman/finance")
    Call<TradesmanFinances> getTradesmanFinances(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("tradesman/reviews")
    Call<List<TradesmanReview>> getTradesmanReviews(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("tradesman/timeslots")
    Call<List<Timeslot>> getTradesmanEvents(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @POST("tradesman/timeslot")
    Call<Timeslot> addTimeslot(
            @Query("token") String token,
            @Query("time_slot") HomeFix.TimeslotMap timeslotMap);

    @PATCH("tradesman/timeslot")
    Call<Timeslot> updateTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id,
            @Query("time_slot") HomeFix.TimeslotMap timeslotMap);

    @DELETE("tradesman/timeslot")
    Call<Map<String, Object>> deleteTimeslot(
            @Query("token") String token,
            @Query("original_timeslot_id") String original_timeslot_id);

    @GET("service")
    Call<Service> getService(
            @Query("token") String token,
            @Query("id") String id);

    @POST("service")
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

    @PATCH("service")
    Call<Service> updateService(
            @Query("id") String timeslotId,
            @Query("token") String token,
            @Query("changes") Map<String, Object> changes);

    // TODO: add Part calls

    @GET("service/current")
    Call<Service> getCurrentService(@Query("token") String token);

    @GET("service/next")
    Call<Service> getNextService(@Query("token") String token);

    @GET("service/types")
    Call<List<Problem>> getServiceTypes(@Query("token") String token);

    @GET("service/statuses")
    Call<List<ServiceStatusFlow>> getServiceStatusFlow(@Query("token") String token);

    @GET("services")
    Call<List<Service>> getServices(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("activities/tradesman")
    Call<List<Activity>> getTradesmanActivities(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("activities/service")
    Call<List<Activity>> getServiceActivities(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("attachment")
    Call<Attachment> getServiceAttachment(
            @Query("token") String token,
            @Query("id") String id);

    @POST("attachment")
    Call<Service> createServiceAttachment(
            @Query("token") String token,
            @Query("service_id") String service_id,
            @Query("type") String type,
            @Query("text") String text,
            @Query("file") String fileUrl);

    @GET("attachments")
    Call<List<Attachment>> getServiceAttachments(
            @Query("token") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("cca")
    Call<CCA> getCCA(@Query("apikey") String apikey, @Query("token") String token);

    @POST("service/charge")
    Call<Charge> addCharge(
            @Query("token") String token,
            @Query("charge") Charge charge);

    @PATCH("service/charge")
    Call<Charge> updateCharge(
            @Query("token") String token,
            @Query("original_charge_id") String original_charge_id,
            @Query("charge") Charge charge);

    @DELETE("service/charge")
    Call<Map<String, Object>> deleteCharge(
            @Query("token") String token,
            @Query("original_charge_id") String original_charge_id);

    @POST("service/payment")
    Call<Payment> addPayment(
            @Query("token") String token,
            @Query("payment") Payment payment);

    @PATCH("service/payment")
    Call<Payment> updatePayment(
            @Query("token") String token,
            @Query("original_payment_id") String original_payment_id,
            @Query("payment") Payment payment);

    @DELETE("service/payment")
    Call<Map<String, Object>> deletePayment(
            @Query("token") String token,
            @Query("original_payment_id") String original_payment_id);

}
