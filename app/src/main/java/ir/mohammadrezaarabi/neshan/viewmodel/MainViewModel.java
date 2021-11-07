package ir.mohammadrezaarabi.neshan.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.servicessdk.direction.NeshanDirection;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

import ir.mohammadrezaarabi.neshan.Helper;
import ir.mohammadrezaarabi.neshan.model.NeshanAddress;
import ir.mohammadrezaarabi.neshan.network.RetrofitClientInstance;
import ir.mohammadrezaarabi.neshan.network.ReverseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    final int REQUEST_CODE = 123;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private Location userLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private final Application application;

    ArrayList<LatLng> decodedStepByStepPath;
    private final MutableLiveData<Location> location = new MutableLiveData<>();
    private final MutableLiveData<NeshanAddress> retrofitResponseAddress = new MutableLiveData<>();
    private final MutableLiveData<Polyline> polyLineMap = new MutableLiveData<>();

    public static final ReverseService getDataService = RetrofitClientInstance.getRetrofitInstance().create(ReverseService.class);

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        initLocation();
    }

    public void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //noinspection MissingPermission
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        });
    }

    private void initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.getApplicationContext());
        settingsClient = LocationServices.getSettingsClient(application.getApplicationContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                userLocation = locationResult.getLastLocation();
                location();
            }
        };


        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

    }

    public void location() {
        if (userLocation != null) {
            location.setValue(userLocation);
        }
    }

    public LiveData<Location> getCurrentLocation() {
        return location;
    }

    public LiveData<Polyline> getRoute() {
        return polyLineMap;
    }

    public LiveData<NeshanAddress> getAddress() {
        return retrofitResponseAddress;
    }

    public void neshanRoutingApi(LatLng start, LatLng end) {
        new NeshanDirection.Builder("service.VNlPhrWb3wYRzEYmstQh3GrAXyhyaN55AqUSRR3V", start, end)
                .build().call(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {
                Route route = response.body().getRoutes().get(0);
                decodedStepByStepPath = new ArrayList<>();

                for (DirectionStep step : route.getLegs().get(0).getDirectionSteps()) {
                    decodedStepByStepPath.addAll(PolylineEncoding.decode(step.getEncodedPolyline()));
                }
                Polyline onMapPolyline = new Polyline(decodedStepByStepPath, new Helper().getLineStyle());
                polyLineMap.setValue(onMapPolyline);
            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {

            }
        });
    }

    public void getReverseApi(LatLng desLocation) {
        getDataService.getReverse(desLocation.getLatitude(), desLocation.getLongitude()).enqueue(new Callback<NeshanAddress>() {
            @Override
            public void onResponse(Call<NeshanAddress> call, Response<NeshanAddress> response) {
                String address = response.body().getAddress();
                if (address != null && !address.isEmpty()) {
                    retrofitResponseAddress.setValue(response.body());
                } else {
                    retrofitResponseAddress.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NeshanAddress> call, Throwable t) {
            }
        });
    }

}