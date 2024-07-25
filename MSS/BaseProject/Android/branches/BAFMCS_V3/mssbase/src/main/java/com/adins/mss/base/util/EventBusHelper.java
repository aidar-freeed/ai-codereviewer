package com.adins.mss.base.util;

import de.greenrobot.event.EventBus;

/**
 * Created by angga.permadi on 3/8/2016.
 */
public class EventBusHelper {
    public static void registerEventBus(Object subcriber) {
        if (subcriber != null && !EventBus.getDefault().isRegistered(subcriber)) {
            EventBus.getDefault().register(subcriber);
        }
    }

    public static void unregisterEventBus(Object subcriber) {
        if (subcriber != null && EventBus.getDefault().isRegistered(subcriber)) {
            EventBus.getDefault().unregister(subcriber);
        }
    }

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

}
