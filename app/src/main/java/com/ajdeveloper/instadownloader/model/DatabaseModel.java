package com.ajdeveloper.instadownloader.model;

import android.content.Context;

import com.ajdeveloper.instadownloader.entity.DaoMaster;


public class DatabaseModel {
    private static DatabaseModel instance;
    private DaoMaster.DevOpenHelper openHelper;

    public static DatabaseModel getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseModel.class) {
                if (instance == null) {
                    instance = new DatabaseModel(context);
                }
            }
        }
        return instance;
    }

    private DatabaseModel(Context context) {
        this.openHelper = new DaoMaster.DevOpenHelper(context, "downtube_video", null);
    }
}
