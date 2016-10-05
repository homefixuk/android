package com.homefix.tradesman.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.CustomerProperty;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Timeslot;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnFinishListener;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseUtils {

    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private static FirebaseDatabase firebaseDatabase;

    public static final String
            REF_NAME_USERS = "users",
            REF_NAME_TRADESMAN = "tradesman",
            REF_NAME_CUSTOMERS = "customers",
            REF_NAME_PROPERTIES = "properties",
            REF_NAME_SERVICE_SETS = "serviceSets",
            REF_NAME_SERVICES = "services",
            REF_NAME_TIMESLOTS = "timeslots",
            REF_NAME_TRADESMAN_PRIVATES = "tradesmanPrivates";

    public static String getValidObjectName(String id) {
        return Strings.returnSafely(id)
                .replace(".", "!")
                .replace("#", "|")
                .replace("", "")
                .replace("[", "(")
                .replace("]", ")")
                .replace("$", "^")
                .replace("/", "\\");
    }

    public static String normaliseObjectName(String id) {
        return Strings.returnSafely(id)
                .replace("!", ".")
                .replace("|", "#")
                .replace("", "")
                .replace("(", "[")
                .replace(")", "]")
                .replace("^", "$")
                .replace("\\", "/");
    }


    ///////////////////////////////////////////////
    /////////// Database References ///////////////
    ///////////////////////////////////////////////


    public static DatabaseReference getBaseRef() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();

            // only enable this once in the whole app and set by the remote config
            try {
                if (CacheUtils.readObjectFile("set_data_persistence_enabled", Boolean.class))
                    firebaseDatabase.setPersistenceEnabled(true);
            } catch (Exception e) {
                MyLog.printStackTrace(e);
                FirebaseCrash.report(e);
            }
        }

        return firebaseDatabase.getReference();
    }

    public static DatabaseReference getPrivateRef() {
        return getBaseRef().child("private");
    }

    public static DatabaseReference getTradesmanRef() {
        return getBaseRef().child(REF_NAME_TRADESMAN);
    }

    public static DatabaseReference getTradesmanPrivatesRef() {
        return getBaseRef().child(REF_NAME_TRADESMAN_PRIVATES);
    }

    public static DatabaseReference getCustomersRef() {
        return getBaseRef().child(REF_NAME_CUSTOMERS);
    }

    public static DatabaseReference getPropertiesRef() {
        return getBaseRef().child(REF_NAME_PROPERTIES);
    }

    public static DatabaseReference getServicesRef() {
        return getBaseRef().child(REF_NAME_SERVICES);
    }

    public static DatabaseReference getSpecificServiceRef(String id) {
        if (!Strings.isEmpty(id)) return getBaseRef().child(REF_NAME_SERVICES).child(id);
        return null;
    }

    public static DatabaseReference getSpecificServiceSetRef(String id) {
        if (!Strings.isEmpty(id)) return getBaseRef().child(REF_NAME_SERVICE_SETS).child(id);
        return null;
    }

    public static DatabaseReference getSpecificTimeslotRef(String id) {
        if (!Strings.isEmpty(id)) return getBaseRef().child(REF_NAME_TIMESLOTS).child(id);
        return null;
    }

    public static DatabaseReference getServiceSetsRef() {
        return getBaseRef().child(REF_NAME_SERVICE_SETS);
    }

    public static DatabaseReference getTimeslotsRef() {
        return getBaseRef().child(REF_NAME_TIMESLOTS);
    }

    public static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getCurrentTradesmanId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static DatabaseReference getCurrentTradesmanRef() {
        return getSpecificTradesmanRef(getCurrentTradesmanId());
    }

    public static DatabaseReference getSpecificTradesmanRef(String uid) {
        if (!Strings.isEmpty(uid)) return getBaseRef().child(REF_NAME_TRADESMAN).child(uid);
        return null;
    }

    public static DatabaseReference getCurrentTradesmanPrivateRef() {
        return getSpecificTradesmanPrivateRef(getCurrentTradesmanId());
    }

    public static DatabaseReference getSpecificTradesmanPrivateRef(String uid) {
        if (!Strings.isEmpty(uid))
            return getBaseRef().child(REF_NAME_TRADESMAN_PRIVATES).child(uid);
        return null;
    }

    /**
     * @param map
     * @return convert the keys in the map to raw package names
     */
    public static List<String> getNormalisedPackageNames(Map<String, Object> map) {
        if (map == null || map.size() == 0) return new ArrayList<>();

        List<String> names = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) names.add(normaliseObjectName(key));
        return names;
    }

    /**
     * @param names
     * @return convert the keys in the map to raw package names
     */
    public static List<String> getNormalisedPackageNames(List<String> names) {
        if (names == null || names.size() == 0) return new ArrayList<>();

        List<String> normals = new ArrayList<>();
        for (String key : names) normals.add(normaliseObjectName(key));
        return normals;
    }

    /**
     * Send the users registration token to the server
     *
     * @param token
     * @param callback
     */
    public static void sendRegistrationToServer(String token, @Nullable final OnGotObjectListener<Boolean> callback) {
        if (Strings.isEmpty(token)) {
            if (callback != null) callback.onGotThing(false);
            return;
        }

        String userId = FirebaseUtils.getCurrentTradesmanId();

        if (Strings.isEmpty(userId)) {
            if (callback != null) callback.onGotThing(false);
            return;
        }

        // save the token to /
        FirebaseUtils
                .getPrivateRef()
                .child("user_tokens")
                .child(userId)
                .setValue(token, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (callback != null) callback.onGotThing(databaseError != null);
                    }
                });
    }

    /**
     * Increment the number of app opens from the current user
     */
    public static void incrementNumberAppOpens() {
        DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();

        if (ref == null) return;

        ref.child("numberAppOpens").runTransaction(getIncrementHandler(1));
    }

    /**
     * @return a Transaction Handler that increments a number
     */
    private static Transaction.Handler getIncrementHandler(final int increment) {
        return getIncrementHandler(increment, null);
    }

    /**
     * @return a Transaction Handler that increments a number
     */
    private static Transaction.Handler getIncrementHandler(final int increment, final OnGotObjectListener<Integer> callback) {
        return new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer count = mutableData.getValue(Integer.class);
                if (count == null) count = 0;
                mutableData.setValue(count + increment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (callback != null) {
                    int result = -1;

                    if (databaseError != null || dataSnapshot == null) {
                        callback.onGotThing(result);
                        return;
                    }

                    callback.onGotThing(dataSnapshot.getValue(Integer.class));
                }
            }
        };
    }

    /**
     * @return a Transaction Handler that increments a number
     */
    private static Transaction.Handler getLongIncrementHandler(long increment) {
        return getLongIncrementHandler(increment, null);
    }

    /**
     * @return a Transaction Handler that increments a number
     */
    private static Transaction.Handler getLongIncrementHandler(final long increment, final OnGotObjectListener<Long> callback) {
        return new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long count = mutableData.getValue(Long.class);
                if (count == null) count = 0L;
                mutableData.setValue(count + increment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (callback != null) {
                    long result = -1L;

                    if (databaseError != null || dataSnapshot == null) {
                        callback.onGotThing(result);
                        return;
                    }

                    callback.onGotThing(dataSnapshot.getValue(Long.class));
                }
            }
        };
    }

    /**
     * Update the current user
     *
     * @param listName         the list of things to add to add base/listName/{uid}
     * @param newListItem      the key for the new item to add base/listName/{uid}/newListItem
     * @param newListItemValue the value to set for the new item base/listName/{uid}/newListItem/newListItemValue
     * @param counterName      the counter name to increment users/{uid}/counterName/{counter++}
     */
    public static void addAndIncrementForUser(final String listName, String newListItem, Object newListItemValue, String counterName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = getCurrentTradesmanRef();
        if (user == null || ref == null) return;

        if (!Strings.isEmpty(listName) && !Strings.isEmpty(newListItem) && newListItemValue != null) {
            getBaseRef().child(listName).child(user.getUid()).child(newListItem).setValue(newListItemValue);
        }

        if (!Strings.isEmpty(counterName)) {
            ref.child(counterName).runTransaction(getIncrementHandler(1));
        }
    }

    @IgnoreExtraProperties
    public static class TradesmanTimeslot {

        private long startTime, endTime;

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    public static void getCurrentService(@NonNull final OnGotObjectListener<Timeslot> onGotObjectListener) {
        String id = getCurrentTradesmanId();
        if (Strings.isEmpty(id)) {
            onGotObjectListener.onGotThing(null);
            return;
        }

        // start checking jobs from 8 hours ago
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY) - 8);
        long startTime = start.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        // get all keys for the Timeslots for this tradesman that started in the last 8 hours
        Query query = getBaseRef().child("tradesmanTimeslots").child(id).orderByChild("startTime").startAt(startTime).endAt(endTime);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();

                String timeslotId = null;
                DataSnapshot child;
                TradesmanTimeslot tradesmanTimeslot;
                boolean hasCurrentTimeslotId = false;
                while (!hasCurrentTimeslotId && iterator.hasNext()) {
                    child = iterator.next();
                    tradesmanTimeslot = child.getValue(TradesmanTimeslot.class);
                    if (tradesmanTimeslot.getStartTime() < System.currentTimeMillis() && tradesmanTimeslot.getEndTime() > System.currentTimeMillis()) {
                        timeslotId = child.getKey();
                        hasCurrentTimeslotId = true;
                    }
                }

                if (Strings.isEmpty(timeslotId)) {
                    onGotObjectListener.onGotThing(null);
                    return;
                }

                // now fetch the timeslot
                getTimeslotsRef().child(timeslotId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || !dataSnapshot.exists()) {
                            onCancelled(null);
                            return;
                        }

                        onGotObjectListener.onGotThing(dataSnapshot.getValue(Timeslot.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        MyLog.e(TAG, "Get current timeslot error: " + (databaseError != null ? databaseError.getMessage() : "unknown"));
                        onGotObjectListener.onGotThing(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGotObjectListener.onGotThing(null);
            }
        });
    }

    public static void getNextService(@NonNull final OnGotObjectListener<Timeslot> onGotObjectListener) {
        String id = getCurrentTradesmanId();
        if (Strings.isEmpty(id)) {
            onGotObjectListener.onGotThing(null);
            return;
        }

        // get all keys for the Timeslots for this tradesman that started in the last 8 hours
        Query query = getBaseRef().child("tradesmanTimeslots").child(id).orderByChild("startTime").startAt(System.currentTimeMillis()).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String timeslotId = dataSnapshot != null && dataSnapshot.exists() ? dataSnapshot.getKey() : null;
                if (Strings.isEmpty(timeslotId)) {
                    onGotObjectListener.onGotThing(null);
                    return;
                }

                // now fetch the timeslot
                getTimeslotsRef().child(timeslotId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || !dataSnapshot.exists()) {
                            onCancelled(null);
                            return;
                        }

                        onGotObjectListener.onGotThing(dataSnapshot.getValue(Timeslot.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        MyLog.e(TAG, "Get next timeslot error: " + (databaseError != null ? databaseError.getMessage() : "unknown"));
                        onGotObjectListener.onGotThing(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGotObjectListener.onGotThing(null);
            }
        });
    }

    public static void createService(final boolean isOwnJob, @NonNull final Calendar start, @NonNull final Calendar end, String jobType, String addressLine1, String addressLine2,
                                     String addressLine3, String postcode, String country, Double latitude, Double longitude,
                                     String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description,
                                     @NonNull final OnGotObjectListener<Timeslot> listener) {
        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(tradesmanId)) {
            MyLog.e(TAG, "[createService] TradesmanId is empty");
            listener.onGotThing(null);
            return;
        }

        String customerKey = getCustomersRef().push().getKey();
        String propertyKey = getPropertiesRef().push().getKey();
        String customerPropertyInfoKey = getBaseRef().child("customerPropertyInfos").push().getKey();
        String serviceKey = getServicesRef().push().getKey();
        String serviceSetKey = getServiceSetsRef().push().getKey();
        final String timeslotKey = getTimeslotsRef().push().getKey();

        updateJob(timeslotKey, serviceKey, serviceSetKey, customerKey, propertyKey, customerPropertyInfoKey,
                isOwnJob, start, end, jobType, addressLine1, addressLine2, addressLine3, postcode, country,
                latitude, longitude, customerName, customerEmail, customerPhone, customerPropertyRelationship,
                description, listener);
    }

    public static void updateJob(
            final String timeslotKey,
            final String serviceKey,
            final String serviceSetKey,
            final String customerKey,
            final String propertyKey,
            final String customerPropertyInfoKey,
            final boolean isOwnJob, final Calendar start, final Calendar end, String jobType, String addressLine1, String addressLine2,
            String addressLine3, String postcode, String country, Double latitude, Double longitude,
            String customerName, String customerEmail, String customerPhone, String customerPropertyRelationship, String description,
            @NonNull final OnGotObjectListener<Timeslot> listener) {

        String tradesmanId = FirebaseUtils.getCurrentTradesmanId();
        if (Strings.isEmpty(tradesmanId) || Strings.isEmpty(timeslotKey) || Strings.isEmpty(serviceKey)
                || Strings.isEmpty(serviceSetKey) || Strings.isEmpty(customerKey)
                || Strings.isEmpty(propertyKey) || Strings.isEmpty(customerPropertyInfoKey)) {
            MyLog.e(TAG, "[updateJob] a key is empty");
            listener.onGotThing(null);
            return;
        }

        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();

        Customer customer = new Customer();
        customer.setId(customerKey);
        customer.setHomeAddressLine1(addressLine1);
        customer.setHomeAddressLine2(addressLine2);
        customer.setHomeAddressLine3(addressLine3);
        customer.setHomeCountry(country);
        customer.setHomePostcode(postcode);
        customer.setBillingAddressLine1(addressLine1);
        customer.setBillingAddressLine2(addressLine2);
        customer.setBillingAddressLine3(addressLine3);
        customer.setBillingCountry(country);
        customer.setBillingPostcode(postcode);
        customer.setName(customerName);
        customer.setEmail(customerEmail);
        customer.setHomePhone(customerPhone);

        Property property = new Property();
        property.setId(propertyKey);
        property.setAddressLine1(addressLine1);
        property.setAddressLine2(addressLine2);
        property.setAddressLine3(addressLine3);
        property.setCountry(country);
        property.setPostcode(postcode);
        if (latitude != null && longitude != null) {
            property.setLatitude(latitude);
            property.setLongitude(longitude);
        }

        CustomerProperty customerProperty = new CustomerProperty();
        customerProperty.setId(customerPropertyInfoKey);
        customerProperty.setType(customerPropertyRelationship);
        customerProperty.setCustomerId(customerKey);
        customerProperty.setPropertyId(propertyKey);

        ServiceSet serviceSet = new ServiceSet();
        serviceSet.setId(serviceSetKey);
        serviceSet.setCustomerPropertyId(customerPropertyInfoKey);
        Map<String, Object> serviceSetServices = new HashMap<>();
        serviceSetServices.put(serviceKey, true);
        serviceSet.setServices(serviceSetServices);
        serviceSet.setCreatedAt(System.currentTimeMillis());
        serviceSet.setNumberServices(1);
        serviceSet.setAmountPaid(0);
        serviceSet.setTotalCost(0);

        Service service = new Service(serviceKey);
        service.setOwnJob(isOwnJob);
        service.setEstimatedDuration(endTime - startTime);
        service.setTradesmanId(tradesmanId);
        service.setServiceType(jobType);
        service.setTradesmanNotes(description);
        service.setServiceSetId(serviceSetKey);

        final Timeslot timeslot = new Timeslot(timeslotKey);
        timeslot.setType((isOwnJob ? Timeslot.TYPE.OWN_JOB : Timeslot.TYPE.SERVICE).getName());
        timeslot.setStartTime(startTime);
        timeslot.setEndTime(endTime);
        timeslot.setTradesmanId(tradesmanId);
        timeslot.setServiceId(serviceKey);

        Map<String, Object> map = new HashMap<>();

        map.put("/customers/" + customerKey, customer.toMap());
        map.put("/properties/" + propertyKey, property.toMap());
        map.put("/customerPropertyInfos/" + customerPropertyInfoKey, customerProperty.toMap());
        map.put("/customerProperties/" + customerKey + "/" + propertyKey, customerPropertyInfoKey);
        map.put("/propertyCustomers/" + propertyKey + "/" + customerKey, customerPropertyInfoKey);
        map.put("/serviceSets/" + serviceSetKey, serviceSet.toMap());
        map.put("/services/" + serviceKey, service.toMap());
        map.put("/timeslots/" + timeslotKey, timeslot.toMap());
        map.put("/tradesmanTimeslots/" + tradesmanId + "/" + timeslotKey, timeslot.toMap());

        // update all the references at the same that, that way either all or none get set
        FirebaseUtils.getBaseRef().updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    MyLog.e(TAG, "Something went wrong updating timeslot and service");
                    MyLog.e(TAG, databaseError.getDetails());
                    MyLog.e(TAG, databaseError.getMessage());
                    MyLog.printStackTrace(databaseError.toException());
                    listener.onGotThing(null);
                    return;
                }

                listener.onGotThing(timeslot);

//                FirebaseUtils.getSpecificTimeslotRef(timeslotKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Timeslot timeslot1 = dataSnapshot != null ? dataSnapshot.getValue(Timeslot.class) : timeslot;
//                        listener.onGotThing(timeslot1);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        MyLog.e(TAG, "Something went wrong fetching Timeslot");
//                        if (databaseError != null) {
//                            MyLog.e(TAG, databaseError.getDetails());
//                            MyLog.printStackTrace(databaseError.toException());
//                        }
//                        listener.onGotThing(timeslot);
//                    }
//                });
            }
        });
    }


    //////////////////////////////////////////////////
    /////////// Multi Function Results ///////////////
    //////////////////////////////////////////////////


    public static class FirebaseValueListener implements ValueEventListener {

        int requestId;
        ValueEventListener valueEventListener;
        OnFinishListener onFinishListener;

        public FirebaseValueListener(int requestId, ValueEventListener valueEventListener, OnFinishListener onFinishListener) {
            this.requestId = requestId;
            this.valueEventListener = valueEventListener;
            this.onFinishListener = onFinishListener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (valueEventListener != null) valueEventListener.onDataChange(dataSnapshot);

            if (onFinishListener != null) onFinishListener.onThingFinished(requestId);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            if (valueEventListener != null) valueEventListener.onCancelled(databaseError);

            if (onFinishListener != null) onFinishListener.onThingFinished(requestId);
        }

    }

    public static class MultiEventListenerOnComplete implements OnFinishListener {

        OnFinishListener onFinishListener;
        List<String> paths = new ArrayList<>();
        List<FirebaseValueListener> eventListeners = new ArrayList<>();
        boolean canAdd = true;
        int runSize = 0, numberFinished = 0;

        public MultiEventListenerOnComplete(OnFinishListener onFinishListener) {
            this.onFinishListener = onFinishListener;
        }

        public boolean add(String path, ValueEventListener valueEventListener) {
            if (!getCanAdd() || Strings.isEmpty(path) || valueEventListener == null)
                return false;

            FirebaseValueListener listener = new FirebaseValueListener(paths.size() + 1, valueEventListener, this);

            paths.add(path);
            eventListeners.add(listener);
            return true;
        }

        public synchronized void setCanAdd(boolean canAdd) {
            this.canAdd = canAdd;
        }

        public synchronized boolean getCanAdd() {
            return canAdd;
        }

        public void run() {
            setCanAdd(false);

            runSize = paths.size();
            numberFinished = 0;
            for (int i = 0; i < paths.size(); i++) {
                FirebaseUtils.getBaseRef().child(paths.get(i)).addListenerForSingleValueEvent(eventListeners.get(i));
            }
        }

        @Override
        public void onThingFinished(int requestId) {
            numberFinished++;

            // if all have finished
            if (numberFinished >= runSize) {
                if (onFinishListener != null) onFinishListener.onThingFinished(0);
            }
        }
    }

    public static class GetMultiFirebaseObjects<O extends Object> implements Runnable, OnGotObjectListener<O> {

        final Class<O> clss;
        final OnGetListListener<O> listener;
        final List<O> list = new ArrayList<>();
        final List<String> paths = new ArrayList<>();
        final List<GetFirebaseObjectValueListener<O>> eventListeners = new ArrayList<>();
        boolean canAdd = true;
        int runSize = 0, numberFinished = 0;

        public GetMultiFirebaseObjects(Class<O> clss, OnGetListListener<O> listener) {
            this.clss = clss;
            this.listener = listener;
        }

        public boolean add(String path) {
            if (!getCanAdd() || Strings.isEmpty(path)) return false;

            paths.add(path);
            eventListeners.add(new GetFirebaseObjectValueListener(clss, this));
            return true;
        }

        public void addAll(List<String> paths) {
            if (paths == null || paths.isEmpty()) return;

            for (String path : paths) add(path);
        }

        @Override
        public void onGotThing(O o) {
            numberFinished++;

            if (o != null) list.add(o);

            // if all have finished
            if (numberFinished >= runSize) {
                if (listener != null) listener.onGetListFinished(list);
            }
        }

        private class GetFirebaseObjectValueListener<O extends Object> implements ValueEventListener {

            Class<O> clss;
            OnGotObjectListener<O> onGotObjectListener;

            public GetFirebaseObjectValueListener(Class clss, OnGotObjectListener<O> onGotObjectListener) {
                this.clss = clss;
                this.onGotObjectListener = onGotObjectListener;
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onGotObjectListener == null) return;

                onGotObjectListener.onGotThing(dataSnapshot.exists() ? dataSnapshot.getValue(clss) : null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onGotObjectListener == null) return;

                onGotObjectListener.onGotThing(null);
            }

        }

        public synchronized void setCanAdd(boolean canAdd) {
            this.canAdd = canAdd;
        }

        public synchronized boolean getCanAdd() {
            return canAdd;
        }

        @Override
        public void run() {
            setCanAdd(false);

            runSize = paths.size();

            // if there is nothing to fetch, return
            if (runSize == 0) {
                if (listener != null) listener.onGetListFinished(new ArrayList<O>());
                return;
            }

            // add all the paths to get
            numberFinished = 0;
            for (int i = 0; i < paths.size(); i++) {
                FirebaseUtils.getBaseRef().child(paths.get(i)).addListenerForSingleValueEvent(eventListeners.get(i));
            }
        }
    }


}
