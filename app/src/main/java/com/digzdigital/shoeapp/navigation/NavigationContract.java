package com.digzdigital.shoeapp.navigation;

import android.bluetooth.BluetoothDevice;
import android.content.res.Resources;

import com.digzdigital.shoeapp.adapter.PlaceAutoCompleteAdapter;
import com.digzdigital.shoeapp.device.DeviceConnector;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapzen.android.lost.api.Status;
import com.mapzen.android.routing.MapzenRouter;

import org.w3c.dom.Text;

/**
 * Created by Digz on 20/01/2017.
 */

public interface NavigationContract {
    interface View{
        void showProgressDialog(String title, String message);
        void dismissProgressDialog();
        void showToast(String message);
        void setStartTripVisibility(int visibility);
        String getTextOfOriginField();
        String getTextOfDestinationField();
        void setErrorOnOriginTextField(String textField);
        void setErrorOnDestinationTextField(String textField);
        void setPlaceAdapterToView(PlaceAutoCompleteAdapter adapter);
        void getResultForLost(Status status);
        void setLeftRightVisibility(int visibility);
        void centerCamera(CameraUpdate center);
        void zoomCamera(CameraUpdate zoom);
        Polyline drawOnMap(PolylineOptions polylineOptions);
        void addMapMarker(MarkerOptions markerOptions);
        MapzenRouter getRouter();
    }

    interface Presenter{
        void setView(NavigationActivity view);
        void initialiseDevice();
        void setStartToNull();
        void setEndToNull();
        boolean determineIfOnline();
        void setBtDevice(BluetoothDevice device);
        void setLocationListener();
        boolean deviceInitialised();
        void initiateConnectionToDevice();
        void endConnectionToDevice();
        void setupGoogleServices();
        void setUpPlaceAutoCompleteAdapter();
        void route();
        void onDestinationAutocompleteClicked(int position);
        void onStartAutocompleteClicked(int position);
        void createLostApiClient();
        void checkLocationSettings();
        void startTrip();
        void sendLeftToDevice();
        void sendRightToDevice();
        void setAdapterBounds(LatLngBounds bounds);
    }
}
