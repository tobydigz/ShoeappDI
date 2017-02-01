package com.digzdigital.shoeapp.dagger;

import android.content.Context;

import com.digzdigital.shoeapp.R;
import com.digzdigital.shoeapp.ShoeApplication;
import com.digzdigital.shoeapp.navigation.NavigationPresenter;
import com.digzdigital.shoeapp.navigation.directioning.DetermineDirection;
import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.android.routing.MapzenRouter_MembersInjector;
import com.mapzen.android.search.MapzenSearch;
import com.mapzen.helpers.RouteEngine;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {
    private final ShoeApplication app;

    public AppModule(ShoeApplication app){
        this.app = app;
    }

    @Provides @Singleton
    public Context provideContext(){
        return app;
    }

    @Provides @Singleton
    public NavigationPresenter providesNavigationPresenter(Context context){
        return new NavigationPresenter(context);
    }

    @Provides @Singleton
    public MapzenSearch providesMapzenSearch(Context context){
        return new MapzenSearch(context, "mapzen-4DXdxtn");
    }

    @Provides @Singleton
    public MapzenRouter providesMapzenRouter(Context context){
        return new MapzenRouter(context, "mapzen-4DXdxtn");
    }

    @Provides @Singleton
    public RouteEngine providesRouteEngine(){
        return new RouteEngine();
    }

    @Provides @Singleton
    public DetermineDirection providesDetermineDirection(){
        return new DetermineDirection();
    }
}
