package com.digzdigital.shoeapp;

import android.app.Application;

import com.digzdigital.shoeapp.dagger.AppComponent;
import com.digzdigital.shoeapp.dagger.AppModule;
import com.digzdigital.shoeapp.dagger.DaggerAppComponent;

/**
 * Created by Digz on 21/01/2017.
 */

public class ShoeApplication extends Application{

    private static ShoeApplication instance = new ShoeApplication();
    private static AppComponent appComponent;
    
    public static ShoeApplication getInstance(){
        return instance;
    }

    public AppComponent getAppComponent() {
        if (appComponent == null){
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return appComponent;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        getAppComponent();
    }
}
