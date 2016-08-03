package com.homefix.tradesman.api;

import com.homefix.tradesman.model.Activity;
import com.homefix.tradesman.model.Attachment;
import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Part;
import com.homefix.tradesman.model.Payment;
import com.homefix.tradesman.model.Problem;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceStatusFlow;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.TradesmanFinances;
import com.homefix.tradesman.model.TradesmanNotification;
import com.homefix.tradesman.model.TradesmanPrivate;
import com.homefix.tradesman.model.TradesmanReview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @DELETE("/user/logout")
    Call<HashMap<String, Object>> logout(@Header("Authorization") String token);

    @GET("/tradesman/me")
    Call<Tradesman> getTradesman(@Header("Authorization") String token);

    @PATCH("/tradesman/me")
    Call<Tradesman> updateTradesmanDetails(
            @Header("Authorization") String token,
            @QueryMap Map<String, Object> params);

    @GET("/tradesman/me/private")
    Call<TradesmanPrivate> getTradesmanPrivate(
            @Query("apikey") String apikey,
            @Header("Authorization") String token);

    @PATCH("/tradesman/me/private")
    Call<Tradesman> updateTradesmanPrivateDetails(
            @Header("Authorization") String token,
            @Query("apikey") String apikey,
            @QueryMap Map<String, Object> params);

    @POST("/tradesman/location")
    Call<Timeslot> updateLocation(
            @Header("Authorization") String token,
            @QueryMap Map<String, Object> location);

    @GET("/tradesman/notifications")
    Call<List<TradesmanNotification>> getTradesmanNotifications(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("/tradesman/finance")
    Call<TradesmanFinances> getTradesmanFinances(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("/tradesman/reviews")
    Call<List<TradesmanReview>> getTradesmanReviews(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("/tradesman/timeslots")
    Call<List<Timeslot>> getTradesmanEvents(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @POST("/tradesman/timeslot")
    Call<Timeslot> addTimeslot(
            @Header("Authorization") String token,
            @Query("time_slot") HomeFix.TimeslotMap timeslotMap);

    @PATCH("/tradesman/timeslot/{timeslot_id}")
    Call<Timeslot> updateTimeslot(
            @Header("Authorization") String token,
            @Path("timeslot_id") String original_timeslot_id,
            @QueryMap HomeFix.TimeslotMap timeslotMap);

    @DELETE("/tradesman/timeslot/{timeslot_id}")
    Call<Map<String, Object>> deleteTimeslot(
            @Header("Authorization") String token,
            @Path("timeslot_id") String original_timeslot_id);

    @GET("/service/{service_id}")
    Call<Service> getService(
            @Header("Authorization") String token,
            @Path("service_id") String id);

    @POST("/service")
    Call<Service> createService(
            @Header("Authorization") String token,
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

    @PATCH("/service/{service_id}")
    Call<Service> updateService(
            @Path("service_id") String timeslotId,
            @Header("Authorization") String token,
            @Query("changes") Map<String, Object> changes);

    @GET("/service/current")
    Call<Service> getCurrentService(@Header("Authorization") String token);

    @GET("/service/next")
    Call<Service> getNextService(@Header("Authorization") String token);

    @GET("/service/types")
    Call<List<Problem>> getServiceTypes(@Header("Authorization") String token);

    @GET("/service/statuses")
    Call<List<ServiceStatusFlow>> getServiceStatusFlow(@Header("Authorization") String token);

    @GET("/services")
    Call<List<Service>> getServices(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("/activities")
    Call<List<Activity>> getActivities(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @POST("/attachment")
    Call<Service> createAttachment(
            @Header("Authorization") String token,
            @Query("service_id") String service_id,
            @Query("type") String type,
            @Query("text") String text,
            @Query("file") String fileUrl);

    @GET("/attachment/{attachment_id}")
    Call<Attachment> getAttachment(
            @Header("Authorization") String token,
            @Path("attachment_id") String id);

    @GET("/attachments")
    Call<List<Attachment>> getAttachments(
            @Header("Authorization") String token,
            @Query("filter") Map<String, Object> filter);

    @GET("/cca")
    Call<CCA> getCCA(@Query("apikey") String apikey, @Header("Authorization") String token);

    @POST("/charge")
    Call<Charge> addCharge(
            @Header("Authorization") String token,
            @Query("charge") Charge charge);

    @PATCH("/charge/{charge_id}")
    Call<Charge> updateCharge(
            @Header("Authorization") String token,
            @Path("charge_id") String original_charge_id,
            @Query("charge") Charge charge);

    @DELETE("/charge/{charge_id}")
    Call<Map<String, Object>> deleteCharge(
            @Header("Authorization") String token,
            @Path("charge_id") String original_charge_id);

    @POST("/payment")
    Call<Payment> addPayment(
            @Header("Authorization") String token,
            @Query("payment") Payment payment);

    @PATCH("/payment/{payment_id}")
    Call<Payment> updatePayment(
            @Header("Authorization") String token,
            @Path("payment_id") String original_payment_id,
            @Query("payment") Payment payment);

    @DELETE("/payment/{payment_id}")
    Call<Map<String, Object>> deletePayment(
            @Header("Authorization") String token,
            @Path("payment_id") String original_payment_id);

    @POST("/part")
    Call<Payment> addPart(
            @Header("Authorization") String token,
            @Query("part") Part part);

    @PATCH("/part/{part_id}")
    Call<Payment> updatePart(
            @Header("Authorization") String token,
            @Path("part_id") String original_part_id,
            @Query("part") Part part);

    @DELETE("/part/{part_id}")
    Call<Map<String, Object>> deletePart(
            @Header("Authorization") String token,
            @Path("part_id") String original_payment_id);

}
