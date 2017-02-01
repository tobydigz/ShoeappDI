package com.digzdigital.shoeapp.navigation;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.digzdigital.shoeapp.R;
import com.digzdigital.shoeapp.ShoeApplication;
import com.digzdigital.shoeapp.adapter.PlaceAutoCompleteAdapter;
import com.digzdigital.shoeapp.databinding.ActivityDirectionBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapzen.android.lost.api.Status;
import com.mapzen.android.routing.MapzenRouter;

import javax.inject.Inject;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener, NavigationContract.View, OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    @Inject
    public NavigationPresenter presenter;
    private ProgressDialog progressDialog;
    private ActivityDirectionBinding binding;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction);
        ((ShoeApplication) getApplication()).getAppComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter.setView(this);
        presenter.setupGoogleServices();
        setupMaps();


        binding.startTrip.setOnClickListener(this);
        binding.send.setOnClickListener(this);
        binding.goLeft.setOnClickListener(this);
        binding.goRight.setOnClickListener(this);
        setClickListeners();

        setTextWatchers();

        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            presenter.setBtDevice((BluetoothDevice) bundle.getParcelable("device"));
        } catch (Exception ignore) {

        }
    }

    private void setTextWatchers() {
        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        binding.startAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                presenter.setStartToNull();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.endAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                presenter.setEndToNull();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setClickListeners() {
        /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */
        binding.startAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                presenter.onStartAutocompleteClicked(position);

            }
        });
        binding.endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                presenter.onDestinationAutocompleteClicked(position);

            }
        });
    }

    private void setupMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.send:
                if (presenter.determineIfOnline()) {
                    presenter.route();
                } else {
                    Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.startTrip:
                progressDialog.show();
                presenter.startTrip();
                presenter.initialiseDevice();
                break;
            case R.id.goLeft:
                break;
            case R.id.goRight:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (presenter.deviceInitialised()) presenter.initiateConnectionToDevice();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter.deviceInitialised()) presenter.endConnectionToDevice();


    }

    @Override
    public void showProgressDialog(String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }


    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStartTripVisibility(int visibility) {
        binding.startTrip.setVisibility(visibility);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        presenter.setUpPlaceAutoCompleteAdapter();
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = NavigationActivity.this.map.getProjection().getVisibleRegion().latLngBounds;
                presenter.setAdapterBounds(bounds);
            }
        });

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(6.4654, -3.4066));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    @Override
    public void setPlaceAdapterToView(PlaceAutoCompleteAdapter adapter) {
        binding.startAutoComplete.setAdapter(adapter);
        binding.endAutoComplete.setAdapter(adapter);

    }

    @Override
    public void getResultForLost(Status status) {
        try {
            status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLeftRightVisibility(int visibility) {
        binding.goLeft.setVisibility(visibility);
        binding.goRight.setVisibility(visibility);
    }

    @Override
    public String getTextOfOriginField() {
        return binding.startAutoComplete.getText().toString();
    }

    @Override
    public String getTextOfDestinationField() {
        return binding.endAutoComplete.getText().toString();
    }

    @Override
    public void setErrorOnOriginTextField(String error) {
        binding.startAutoComplete.setError(error);
    }

    @Override
    public void setErrorOnDestinationTextField(String error) {
        binding.endAutoComplete.setError(error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                // Check the location settings again and continue
                presenter.checkLocationSettings();
                break;
            default:
                break;
        }
    }


    @Override
    public void centerCamera(CameraUpdate center) {
        map.moveCamera(center);
    }

    @Override
    public void zoomCamera(CameraUpdate zoom) {
        map.moveCamera(zoom);
    }

    @Override
    public Polyline drawOnMap(PolylineOptions polylineOptions) {
        return map.addPolyline(polylineOptions);
    }

    @Override
    public void addMapMarker(MarkerOptions markerOptions) {
        map.addMarker(markerOptions);
    }

    @Override
    public MapzenRouter getRouter(){
        return new MapzenRouter(this, "mapzen-4DXdxtn");
    }
}
