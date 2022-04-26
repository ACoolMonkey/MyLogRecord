package com.hys.mylogrecord.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录工具类
 *
 * @author Robert Hou
 * @since 2022年04月24日 18:36
 **/
public class LogRecordUtils {

    public static final String RELATION_ID = "relationId";
    public static final String OPERATOR_ID = "operatorId";
    public static final String DESCRIPTION = "description";
    public static final String SNAPSHOT = "snapshot";

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

    /**
     * 深拷贝
     */
    public static <T> T deepCopy(T src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked") T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
