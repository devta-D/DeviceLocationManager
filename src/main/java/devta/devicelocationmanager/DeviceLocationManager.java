package devta.devicelocationmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * author @Divyanshu Tayal
 */

public class DeviceLocationManager implements PermissionListener,
        OnSuccessListener<LocationSettingsResponse>, OnFailureListener {

    private LocationCallBacks locationCallBacks;
    private Context context;
    private int REQUEST_CHECK_SETTINGS = 101;

    public DeviceLocationManager(@NonNull Activity context) {
        this.context = context;
    }

    public void createLocationRequest() {
        if(context==null)return;
        if (locationCallBacks == null && this.context instanceof LocationCallBacks) {
            locationCallBacks = (LocationCallBacks) this.context;
            checkForPermissions();
        }
    }

    private void checkForPermissions(){
        TedPermission.with(context)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(this)
                .check();
    }

    @Override
    public void onPermissionGranted() {
        createLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationRequest(int provider) {
        if(context==null)return;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(provider);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) context, this);

        task.addOnFailureListener((Activity) context, this);
    }

    @Override
    public void onSuccess(LocationSettingsResponse response) {
        if (response.getLocationSettingsStates().isGpsPresent() &&
                response.getLocationSettingsStates().isLocationPresent()) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.
                    ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null && locationCallBacks!=null) {
                                    locationCallBacks.onCurrentLocationFound(location);
                                }
                            }
                        });
            }else {
                checkForPermissions();
            }
        }
    }

    public void createLocationRequestNetwork(){
        createLocationRequest(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof ResolvableApiException) {
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                ResolvableApiException resolvable = (ResolvableApiException)e;
                resolvable.startResolutionForResult((Activity)context, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                // Ignore the error.
            }
        }
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        if(locationCallBacks!=null)locationCallBacks.onLocationPermissionDenied();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==Activity.RESULT_OK){
            createLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else if(requestCode==REQUEST_CHECK_SETTINGS){
            if(locationCallBacks!=null)locationCallBacks.onGpsNotAllowedByUser();
        }
    }

    public interface LocationCallBacks{

        void onGpsNotAllowedByUser();

        void onCurrentLocationFound(Location location);

        void onLocationPermissionDenied();

    }
}
