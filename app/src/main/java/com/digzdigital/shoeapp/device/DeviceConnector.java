package com.digzdigital.shoeapp.device;

/**
 * Created by Digz on 20/01/2017.
 */

public interface DeviceConnector {
    void initialiseDevice();
    void setDevice(Object object);
    void initiateConnectionToDevice();
    void endConnectionToDevice();
    void sendLeftToDevice();
    void sendRightToDevice();

}
