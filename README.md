# DeviceLocationManager
Get user location in a simple way


Gradle
------------
Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency
```groovy
dependencies {
	implementation 'com.github.devta-D:StaggeredGridView:v1.0'
}
```

# Usage
--------
###java
``` 
  DeviceLocationManager locationManager = new DeviceLocationManager(activityContext);
  locationManager.createLocationRequest();
```

# CallBacks
To catch DeviceLocationManager events implement DeviceLocationManager.LocationCallBacks into your Activity as following:

```
AppCompatActivity implements DeviceLocationManager.LocationCallBacks{

    @Override
    public void onGpsNotAllowedByUser() {
        //this catches the event when user dis-allows the app to turn on GPS.
        //create a request here to get location using network:
        locationManager.createLocationRequestNetwork();
    }

    @Override
    public void onCurrentLocationFound(Location location) {
        //catches the user current location
    }

    @Override
    public void onLocationPermissionDenied() {
        //when location permissions are denied by user
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //when user allows the app to turn on device GPS, the result will come here in this method,
          just redirect the result to location manager and it will return a location by parsing the response.
        locationManager.onActivityResult(requestCode, resultCode);
    }
```

Developed By
--------

Divyanshu Tayal
<a href="https://www.linkedin.com/in/divyanshu-tayal-4a95b2aa/">
 <img alt="Follow me on LinkedIn"
 src="http://data.pkmmte.com/temp/social_linkedin_logo.png" />
</a>

<a href="https://tecorb.com/">TecOrb Technologies</a>
