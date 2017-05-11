package com.ikholopov.personal.myyandextranslate.data;

import android.content.Context;

/**Container with singletons of DBProvider and Notification Manager
 * Created by igor on 4/15/17.
 */

public class TranslateDBContainer {

    private static volatile TranslateDBProvider providerInstance;
    private static volatile NotificationManager notificationManagerInstance;


    public static TranslateDBProvider getProviderInstance(Context context) {
        TranslateDBProvider local = providerInstance;
        if(local == null) {
            synchronized (TranslateDBContainer.class) {                                             //Thread safe solution with lazy initialization
                local = providerInstance;
                if(local == null) {
                    providerInstance = new TranslateDBProvider(context);
                    local = providerInstance;
                }
            }
        }
        return local;
    }

    public static NotificationManager getNotificationManagerInstance() {
        NotificationManager local = notificationManagerInstance;
        if(local == null) {
            synchronized (TranslateDBContainer.class) {
                local = notificationManagerInstance;
                if(local == null) {
                    notificationManagerInstance = new NotificationManager();
                    local = notificationManagerInstance;
                }
            }
        }
        return local;
    }
}
