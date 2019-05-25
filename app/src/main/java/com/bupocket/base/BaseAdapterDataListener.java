package com.bupocket.base;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * yanyi on 16/7/25.
 */
public interface BaseAdapterDataListener<T> {
    /**
     * 更改数据，用于首次更新，比如下拉更新取最新的数据
     *
     * @param newData 覆盖的数据
     */
    void setNewData(@NonNull List<T> newData);

    /**
     * 追加数据，在数据源后面添加数据
     *
     * @param moreData 追加的数据
     */
    void addMoreDataList(@NonNull List<T> moreData);

}
