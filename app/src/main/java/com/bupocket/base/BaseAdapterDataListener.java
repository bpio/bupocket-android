package com.bupocket.base;

import android.support.annotation.NonNull;

import java.util.List;

/**
 *
 */
public interface BaseAdapterDataListener<T> {
    /**
     * new data
     * @param newData
     */
    void setNewData(@NonNull List<T> newData);

    /**
     * more data
     * @param moreData
     */
    void addMoreDataList(@NonNull List<T> moreData);

}
