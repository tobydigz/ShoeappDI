package com.digzdigital.shoeapp.dagger;

import com.digzdigital.shoeapp.navigation.NavigationActivity;
import com.digzdigital.shoeapp.navigation.NavigationPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Digz on 21/01/2017.
 */
@Singleton
@Component(modules = {AppModule.class, DeviceConnectorModule.class})
public interface AppComponent {
    void inject(NavigationPresenter presenter);
    void inject(NavigationActivity activity);
}
