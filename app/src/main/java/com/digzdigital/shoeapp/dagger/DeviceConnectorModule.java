package com.digzdigital.shoeapp.dagger;

import android.content.Context;

import com.digzdigital.shoeapp.device.BluetoothModule;
import com.digzdigital.shoeapp.device.DeviceConnector;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Digz on 21/01/2017.
 */

@Module
public class DeviceConnectorModule {

    @Provides @Singleton
    DeviceConnector providesDeviceConnector(Context context){
        return new BluetoothModule(context);
    }
}
