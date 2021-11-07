package ir.mohammadrezaarabi.neshan.view;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;

import ir.mohammadrezaarabi.neshan.BuildConfig;
import ir.mohammadrezaarabi.neshan.Helper;
import ir.mohammadrezaarabi.neshan.R;
import ir.mohammadrezaarabi.neshan.databinding.ActivityMainBinding;
import ir.mohammadrezaarabi.neshan.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mViewModel;

    // map UI element
    MapView map;
    ActivityMainBinding binding;
    boolean startNavigation = false;
    LatLng latLngDestination;
    Helper helper = new Helper();
    LatLng UserLocation;
    Marker userMarker, destinationMarker;
    Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.map.setOnMapClickListener(latLng -> {
            clearMap();
            destinationMarker = new Marker(latLng, helper.markerStyleLocation(MainActivity.this));
            binding.map.addMarker(destinationMarker);
            mViewModel.getReverseApi(latLng);
            latLngDestination = latLng;
        });

        binding.letsgo.setOnClickListener(v -> {
            if (startNavigation) {
                mViewModel.neshanRoutingApi(UserLocation, latLngDestination);
                startNavigation = false;
            }
        });



        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mViewModel.getCurrentLocation().observe(this, item -> {
            binding.map.moveCamera(new LatLng(item.getLatitude(), item.getLongitude()), 0);
            binding.map.setZoom(14, 0);
            binding.map.setMyLocationEnabled(true);
            UserLocation = new LatLng(item.getLatitude(), item.getLongitude());
            mViewModel.getCurrentLocation().removeObservers(this);
        });

        mViewModel.getAddress().observe(this, item -> {
            binding.address.setText(item.getAddress());
            startNavigation = true;
        });

        mViewModel.getRoute().observe(this, item -> {
            currentPolyline = item;
            binding.map.addPolyline(currentPolyline);
            binding.map.setTilt(60, 3f);
            binding.map.moveCamera(new LatLng(UserLocation.getLatitude(), UserLocation.getLongitude()), 0.5f);
            binding.map.setZoom(18, 0.5f);
        });

        checkPermission();
    }

    public void clearMap() {
        startNavigation = true;

        if (currentPolyline != null)
            binding.map.removePolyline(currentPolyline);

        if (destinationMarker != null) {
            binding.map.removeMarker(destinationMarker);
        }
    }

    public void checkPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mViewModel.startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}