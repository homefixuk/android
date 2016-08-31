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
            @Query("timeSlot") HomeFix.TimeslotMap timeslotMap);

    @PATCH("/tradesman/timeslot/{timeslotId}")
    Call<Timeslot> updateTimeslot(
            @Header("Authorization") String token,
            @Path("timeslotId") String originalTimeslotId,
            @QueryMap HomeFix.TimeslotMap timeslotMap);

    @DELETE("/tradesman/timeslot/{timeslotId}")
    Call<Map<String, Object>> deleteTimeslot(
            @Header("Authorization") String token,
            @Path("timeslotId") String originalTimeslotId);

    @GET("/service/{serviceId}")
    Call<Service> getService(
            @Header("Authorization") String token,
            @Path("serviceId") String id);

    @POST("/service")
    Call<Service> createService(
            @Header("Authorization") String token,
            @Query("customerName") String customerName,
            @Query("customerEmail") String customerEmail,
            @Query("customerPhone") String customerPhone,
            @Query("customerPropertyRelationship") String customerPropertyRelationship,
            @Query("addressLine1") String addressLine1,
            @Query("addressLine2") String addressLine2,
            @Query("addressLine3") String addressLine3,
            @Query("postcode") String postcode,
            @Query("country") String country,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("problemName") String problemName,
            @Query("startTime") double startTime,
            @Query("endTime") double endTime,
            @Query("tradesmanNotes") String tradesmanNote);

    @PATCH("/service/{serviceId}")
    Call<Service> updateService(
            @Path("serviceId") String timeslotId,
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
            @Query("serviceId") String serviceId,
            @Query("type") String type,
            @Query("text") String text,
            @Query("file") String fileUrl);

    @GET("/attachment/{attachmentId}")
    Call<Attachment> getAttachment(
            @Header("Authorization") String token,
            @Path("attachmentId") String id);

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

    @PATCH("/charge/{chargeId}")
    Call<Charge> updateCharge(
            @Header("Authorization") String token,
            @Path("chargeId") String originalChargeId,
            @Query("charge") Charge charge);

    @DELETE("/charge/{chargeId}")
    Call<Map<String, Object>> deleteCharge(
            @Header("Authorization") String token,
            @Path("chargeId") String originalChargeId);

    @POST("/payment")
    Call<Payment> addPayment(
            @Header("Authorization") String token,
            @Query("payment") Payment payment);

    @PATCH("/payment/{paymentId}")
    Call<Payment> updatePayment(
            @Header("Authorization") String token,
            @Path("paymentId") String originalPaymentId,
            @Query("payment") Payment payment);

    @DELETE("/payment/{paymentId}")
    Call<Map<String, Object>> deletePayment(
            @Header("Authorization") String token,
            @Path("paymentId") String originalPaymentId);

    @POST("/part")
    Call<Payment> addPart(
            @Header("Authorization") String token,
            @Query("part") Part part);

    @PATCH("/part/{partId}")
    Call<Payment> updatePart(
            @Header("Authorization") String token,
            @Path("partId") String originalPartId,
            @Query("part") Part part);

    @DELETE("/part/{partId}")
    Call<Map<String, Object>> deletePart(
            @Header("Authorization") String token,
            @Path("partId") String originalPaymentId);

}
