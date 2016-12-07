// ILocationCallback.aidl
package edu.ncsu.csc450.contextmiddleware;

/*
 * Callback methods have to be implemented for every function in ILocationInterface that works asynchronously. The results of the functions
 * in the interface is returned as parameters in the callback, which can be used in the client application as required.
 */
interface IContextCallback {

    void locationUpdateCallback(String latitude, String longitude);

    //void notifyAtNewyorkCallback();

    void batteryStatusCallback(boolean batteryStatus);
}
