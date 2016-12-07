package edu.ncsu.csc450.contextmiddleware;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class ContextMiddlewareService extends Service {
    boolean getLocationUpdates = false;
    boolean notifyAtNewyork = false;
    IContextCallback locationUpdateCallback, batteryStatusCallback;
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private Intent batteryStatus;
    private Boolean batteryFlag;
    private int flag = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        batteryStatus = this.registerReceiver(null, ifilter);

        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "Failed to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "Network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "Failed to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "GPS provider does not exist " + ex.getMessage());
        }

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if(isCharging)
            {
                Log.e(TAG,"CHARGING");
                //batteryStatusCallback.
                try {
                    batteryStatusCallback.batteryStatusCallback(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                int level = intent.getIntExtra("level", 0);
                Log.e("BATTERY", String.valueOf(level) + "%");

                if(level<110)
                {
                    try {
                        batteryStatusCallback.batteryStatusCallback(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("IRemote", "Bind Carrem");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            try {
                if (location != null) {
                    if (getLocationUpdates) {
                        /*
                         * The callback function called here is implemented in the client application. The data sent back can be
                         * handled appropriately by changing the implementation on the application.
                         */
                        locationUpdateCallback.locationUpdateCallback(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    /*
     * Initializing listeners for GPS and Network
     */
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    /*
    *   Implement functions in the interface.
    *   Since some functions and/or variables of the superclass might be required, it is better to call an external function from
    *   the interface.
     */
    private final IContextInterface.Stub mBinder = new IContextInterface.Stub() {

        /*
         * The void functions are asynchronous. Once these functions have been called, the Service completes execution and uses
         * the callback interface to communicate with the application.
         */
        public void registerForLocationUpdates(final IContextCallback callback) throws RemoteException {
            enableLocationUpdate(callback);
        }

        @Override
        public void getBatteryUpdates(IContextCallback callback) throws RemoteException {
            enableBatteryUpdate(callback);
        }

        @Override
        public boolean isJackPluggedIn() throws RemoteException {
            return false;
        }

    };

    /*
     * enableLocationUpdate and enableNotificationatNewyork are used to set the flags and the callbacks.
     */

    void enableLocationUpdate(IContextCallback tCallback) {
        this.getLocationUpdates = true;
        this.locationUpdateCallback = tCallback;
    }


    void enableBatteryUpdate(IContextCallback tCallback) {
        //this.getBatteryUpdates = true;
        this.batteryStatusCallback = tCallback;
    }
}
