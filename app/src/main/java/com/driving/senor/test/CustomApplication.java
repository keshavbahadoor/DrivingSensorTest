package com.driving.senor.test;

import android.app.Application;

import services.DrivingService;

/**
 * Created by Keshav on 6/12/2016.
 */
public class CustomApplication extends Application {

    private DrivingService drivingService;

    public DrivingService getDrivingService() {
        return drivingService;
    }

    public void setDrivingService(DrivingService drivingService) {
        this.drivingService = drivingService;
    }
}
