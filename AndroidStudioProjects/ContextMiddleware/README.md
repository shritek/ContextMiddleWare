# ContextMiddleware

1. Run the ContextMiddleware, first.
2. Run the SampleForegroundApplication, next.

If you are using an emulator, you have to set the location explicitly for the middleware to understand that a change in location has taken place. You can either use the extended settings for the emulator or connect to the emulator using telnet.

It is not possible to simulate the headphone jack being connected or disconnected feature using the emulator. To test that functionality, use a physical device. Remember to enable USB debugging when using a physical device.

Testing the "Get Location" functionality
========================================
1. Once the application starts running and the service is connected, click on the "Get Location" button. 
2. Click the "More" button on the toolbar next to the emulator
![alt text](http://i.imgur.com/ngpim92.png | width="48")
3. Change your location in settings (give random coordinates) or use a mock location app on the physical device.
![alt text](http://i.imgur.com/TSRMb32.png =48x)
4. Once you update the coordinates, you should be able to see the location appear in the app. 

Whenever the location is changed, the application will show the new coordinates.

Testing the "Notify at NewYork" functionality
=============================================
1. Set a random location as specified in the "Get Location" functionality above.
2. Click on the "Notify at NewYork" button. This sets flag in the service to invoke the application when the user is detected to be in New York City
3. Change your location in settings (give random coordinates) or use a mock location app on the physical device to somewhere close to (or same as) the following coordinates: [latitude = 40.7128; longitude = -74.0059]

Testing the headset status functionality
========================================
1. Run the middleware, and then the application on a physical device.
2. Click on the "Check Headset Status" button.

A message will be shown whether the headphones are connected or not.
