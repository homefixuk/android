package com.homefix.tradesman.api;

import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.R;
import com.homefix.tradesman.model.CCA;
import com.homefix.tradesman.model.Timeslot;
import com.homefix.tradesman.model.Tradesman;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFix {

    public final static String HOST_NAME;
    public final static int API_KEY;

    private static final String
            HOST_APIARY_MOCK = "http://private-52b01-homefixtradesman.apiary-mock.com/",
            HOST_SALESFORCE_PROD = "https://fieldexprt-homefix.cs80.force.com/services/apexrest/",
            HOST_SALESFORCE_SANDBOX = "https://fieldexprt-homefix.eu5.force.com/services/apexrest/",
            HOST_CUSTOM_DEV = "http://atlas-jacob.codio.io:3000/api/v1/",
            HOST_CUSTOM_PROD = "http://ec2-52-90-29-184.compute-1.amazonaws.com/int/";

    public enum REQUEST_TYPE {

        GET, POST, PATCH, DELETE
    }

    public enum ROLE {

        CUST, TRADE

    }

    // setup the host base
    static {
        if (BuildConfig.DEBUG) {
            if (BuildConfig.FLAVOR.equals("salesforce")) HOST_NAME = HOST_SALESFORCE_SANDBOX;
            else if (BuildConfig.FLAVOR.equals("apiary_mock")) HOST_NAME = HOST_APIARY_MOCK;
            else if (BuildConfig.FLAVOR.equals("custom")) HOST_NAME = HOST_CUSTOM_DEV;
            else HOST_NAME = HOST_APIARY_MOCK;

            API_KEY = R.string.apikey_dev;

        } else {
            if (BuildConfig.FLAVOR.equals("salesforce")) HOST_NAME = HOST_SALESFORCE_PROD;
            else if (BuildConfig.FLAVOR.equals("apiary_mock")) HOST_NAME = HOST_APIARY_MOCK;
            else if (BuildConfig.FLAVOR.equals("custom")) HOST_NAME = HOST_CUSTOM_PROD;
            else HOST_NAME = HOST_APIARY_MOCK;

            API_KEY = R.string.apikey_prod;
        }
    }

    public interface CUSTOM_API {

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
        Call<Tradesman> getTradesman(@Query("token") String token);

        @POST("tradesman/me")
        Call<Tradesman> updateTradesmanDetails(@Query("token") String token, @QueryMap Map<String, Object> params);

        @GET("tradesman/timeslots")
        Call<List<Timeslot>> getTradesmanEvents(@Query("token") String token, @Query("filter") Map<String, Object> filter);

        @GET("cca")
        Call<CCA> getCCA(@Query("apikey") String apikey, @Query("token") String token);

        @POST("tradesman/location")
        Call<Timeslot> updateLocation(@Query("token") String token, @QueryMap Map<String, Object> location);

    }

    public interface MOCK_API {

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

    }

    /**
     * @return an instance of the Mock API that can be called
     */
    public static MOCK_API getMockAPI() {
        return ServiceFactory.createRetrofitService(HomeFix.MOCK_API.class, HomeFix.HOST_NAME);
    }


    /**
     * @return an instance of the custom back-end API that can be called
     */
    public static CUSTOM_API getAPI() {
        return ServiceFactory.createRetrofitService(HomeFix.CUSTOM_API.class, HomeFix.HOST_NAME);
    }

//    public static class API<T> {
//
//        public static MOCK_API mockApi;
//        public static CUSTOM_API customApi;
//
//        private void API() {
//        }
//
//        public API<T> getInstance() {
//            return new API();
//        }
//
//        public void signup(@Query("apikey") String apikey,
//                           @Query("firstName") String firstName,
//                           @Query("lastName") String lastName,
//                           @Query("email") String email,
//                           @Query("password") String password,
//                           @Query("role") String role,
//                           Callback<T> callback) {
//            if (BuildConfig.FLAVOR.equals("apiary_mock")) {
//                if (mockApi == null)
//                    mockApi = ServiceFactory.createRetrofitService(HomeFix.MOCK_API.class, HomeFix.HOST_NAME);
//
//
//                return;
//            }
//
//            if (BuildConfig.FLAVOR.equals("custom")) {
//                if (customApi == null)
//                    customApi = ServiceFactory.createRetrofitService(HomeFix.CUSTOM_API.class, HomeFix.HOST_NAME);
//
//                return;
//            }
//        }
//    }

    /**
     * Exception used when errors occur in the calling of the API
     */
    public static class HomeFixAPIException extends Exception {

        public static final int ERROR_REQUEST_TYPE = 801, ERROR_METHOD = 802;
        private int code;

        public HomeFixAPIException(int code, String detailMessage) {
            super(detailMessage);
            this.code = code;
        }

        public int getCode() {
            return code;
        }

    }

    public interface HomeFixAPIInterface {

        void onComplete(JSONObject results);

    }

    /**
     * Call the API in the current thread
     *
     * @param requestType GET, POST, DELETE, etc.
     * @param method      API function to call
     * @param params      parameters to send in the body
     * @return the JSONObject with the results or error information
     * @throws HomeFixAPIException
     */
    private static JSONObject call(REQUEST_TYPE requestType, String method, Map<String, Object> params) throws HomeFixAPIException {
        if (requestType == null)
            throw new HomeFixAPIException(HomeFixAPIException.ERROR_REQUEST_TYPE, "No request type");

        if (Strings.isEmpty(method))
            throw new HomeFixAPIException(HomeFixAPIException.ERROR_METHOD, "method is empty or invalid: " + Strings.returnSafely(method));

        JSONObject res = new JSONObject();

        try {
            res.put("status", -1);
            res.put("content", "");

            Client client = ClientBuilder.newClient();
            Entity<String> payload = Entity.text("");

            WebTarget target = client.target(HOST_NAME + method);

            Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);

            if (params != null) {
                Set<String> paramNames = params.keySet();
                for (String paramName : paramNames)
                    builder.property(paramName, params.get(paramName));
            }

            Response response = null;

            switch (requestType) {

                case GET:
                    response = builder.get();
                    break;
                case POST:
                    response = builder.post(payload);
                    break;
                case PATCH:
                    response = builder.post(payload);
                    break;
                case DELETE:
                    response = builder.delete();
                    break;
            }

            int status = response.getStatus();
            String result = response.readEntity(String.class);

            res.put("status", status);
            res.put("content", result);

        } catch (JSONException e) {
            MyLog.printStackTrace(e);
        }

        return res;
    }

    /**
     * Call the API in the current thread
     *
     * @param requestType GET, POST, DELETE, etc.
     * @param method      API function to call
     * @param params      parameters to send in the body
     */
    private static void callInBackground(final REQUEST_TYPE requestType, final String method, final Map<String, Object> params, final HomeFixAPIInterface callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject results = new JSONObject();
                try {
                    results = call(requestType, method, params);

                } catch (HomeFixAPIException e) {
                    MyLog.printStackTrace(e);
                    try {
                        results.put("status", e.getCode());
                        results.put("content", e.getMessage());
                    } catch (JSONException e2) {
                        MyLog.printStackTrace(e2);
                    }
                }

                if (callback != null) callback.onComplete(results);
            }

        }).start();
    }

    public static JSONObject tradesmanLogin(String email, String password) throws HomeFixAPIException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return call(REQUEST_TYPE.POST, "tradesman/login", params);
    }

    public static void tradesmanLoginInBackground(String email, String password, HomeFixAPIInterface callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        callInBackground(REQUEST_TYPE.POST, "tradesman/login", params, callback);
    }

}
