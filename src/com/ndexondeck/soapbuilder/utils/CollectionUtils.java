package com.ndexondeck.soapbuilder.utils;

import java.util.*;

/**
 * Created by Nduka on 25/09/2018.
 * Common collection operations
 */
public class CollectionUtils {

    public static <T> Collection<T> difference(Collection<T> listA, Collection<T> listB) {

        Collection<T> listX = new ArrayList<>(listA);
        for (T item : listB) {
            if (listX.contains(item)) {
                listX.remove(item);
            }
        }
        return listX;
    }

    public static <T> Collection<T> union(Collection<T> listA, Collection<T> listB) {

        Collection<T> listX = new ArrayList<>(listA);
        for (T item : listB) {
            if (!listX.contains(item)) {
                listX.add(item);
            }
        }
        return listX;
    }


}
