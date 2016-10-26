package com.homefix.tradesman.timeslot.base_service;

import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.timeslot.base_timeslot.BaseTimeslotView;

import java.util.Map;

/**
 * Created by samuel on 7/19/2016.
 */

public interface BaseServiceView extends BaseTimeslotView {

    ServiceSet getServiceSet();

    Map<Long, String> getNewServiceUpdates();

    Map<Long, String> getAllServiceUpdates();

}
