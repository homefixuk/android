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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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
            HOST_CUSTOM_DEV = "http://ec2-52-90-29-184.compute-1.amazonaws.com/int/", //"http://atlas-jacob.codio.io:3000/api/v1/",
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

//            API_KEY = R.string.apikey_dev;
            API_KEY = R.string.apikey_prod;

        } else {
            if (BuildConfig.FLAVOR.equals("salesforce")) HOST_NAME = HOST_SALESFORCE_PROD;
            else if (BuildConfig.FLAVOR.equals("apiary_mock")) HOST_NAME = HOST_APIARY_MOCK;
            else if (BuildConfig.FLAVOR.equals("custom")) HOST_NAME = HOST_CUSTOM_PROD;
            else HOST_NAME = HOST_APIARY_MOCK;

            API_KEY = R.string.apikey_prod;
        }
    }

    public static API getAPI() {
        return ServiceFactory.createRetrofitService(API.class, HomeFix.HOST_NAME);
    }

    /**
     * Exception used when errors occur in the calling of the com.homefix.tradesman.api.API
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
     * Call the com.homefix.tradesman.api.API in the current thread
     *
     * @param requestType GET, POST, DELETE, etc.
     * @param method      com.homefix.tradesman.api.API function to call
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
     * Call the com.homefix.tradesman.api.API in the current thread
     *
     * @param requestType GET, POST, DELETE, etc.
     * @param method      com.homefix.tradesman.api.API function to call
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

    public static class TimeslotMap extends HashMap<String, Object> {

        public TimeslotMap(long start_time, long end_time, boolean is_available, Timeslot.TYPE type) {
            put("start_time", "" + start_time);
            put("end_time", "" + end_time);
            put("is_available", is_available);
            put("type", type.name());
        }
    }

}
