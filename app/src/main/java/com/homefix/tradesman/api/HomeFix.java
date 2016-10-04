package com.homefix.tradesman.api;

import android.support.annotation.NonNull;

import com.homefix.tradesman.BuildConfig;
import com.homefix.tradesman.R;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.Timeslot;
import com.samdroid.common.MyLog;
import com.samdroid.common.TimeUtils;
import com.samdroid.string.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by samuel on 6/15/2016.
 */
public class HomeFix {

    public static class TimeslotMap extends HashMap<String, Object> {

        public TimeslotMap(long startTimeInMillis, long endTimeInMillis, boolean isAvailable, @NonNull Timeslot.TYPE type) {
            put("start", "" + startTimeInMillis);
            put("end", "" + endTimeInMillis);
            put("isAvailable", isAvailable);
            put("type", type.name().toLowerCase());
            init(startTimeInMillis, endTimeInMillis);
        }

        public TimeslotMap(long startTimeInMillis, long endTimeInMillis, String serviceId) {
            put("start", "" + startTimeInMillis);
            put("end", "" + endTimeInMillis);
            put("isAvailable", false);
            put("type", Timeslot.TYPE.OWN_JOB.name().toLowerCase());
            put("serviceId", Strings.returnSafely(serviceId));
            init(startTimeInMillis, endTimeInMillis);
        }

        public void init(long startTimeInMillis, long endTimeInMillis) {
            put("slotLength", TimeUtils.getMillisInMinutes(endTimeInMillis - startTimeInMillis));
        }
    }

}
