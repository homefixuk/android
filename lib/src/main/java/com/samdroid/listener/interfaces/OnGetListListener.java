package com.samdroid.listener.interfaces;

import java.util.List;

/**
 * Created by samuel on 1/28/2016.
 */
public interface OnGetListListener<O extends Object> {

    void onGetListFinished(List<O> list);

}
