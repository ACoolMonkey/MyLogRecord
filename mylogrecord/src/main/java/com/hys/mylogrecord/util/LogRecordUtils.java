package com.hys.mylogrecord.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录工具类
 *
 * @author Robert Hou
 * @since 2022年04月24日 18:36
 **/
public class LogRecordUtils {

    private static final ThreadLocal<List<Object>> SNAPSHOT_CACHE = ThreadLocal.withInitial(ArrayList::new);

    private LogRecordUtils() {
    }

    public static void setSnapshotCache(Object object) {
        List<Object> snapshots = SNAPSHOT_CACHE.get();
        if (snapshots == null) {
            snapshots = new ArrayList<>(1);
        }
        snapshots.add(object);
        SNAPSHOT_CACHE.set(snapshots);
    }

    public static List<Object> getSnapshotCache() {
        return SNAPSHOT_CACHE.get();
    }

    public static void remove() {
        SNAPSHOT_CACHE.remove();
    }
}
