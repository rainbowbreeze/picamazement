package it.rainbowbreeze.picama.common;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.rainbowbreeze.picama.logic.ReceiveDataFromDeviceService;
import it.rainbowbreeze.picama.logic.SendDataToDeviceService;
import it.rainbowbreeze.picama.ui.PictureActivity;

/**
 * A Dagger module for Android-specific dependencies which require a {@link android.content.Context} or
 * {@link android.app.Application} to create.
 * <p/>
 * Created by alfredomorresi on 31/10/14.
 */
@Module(
        injects = {
                ReceiveDataFromDeviceService.class,
                SendDataToDeviceService.class,
                PictureActivity.class
        },
        includes = MobileModule.class,
        // True because it declares @Provides not used inside the class, but outside.
        // Once the code is finished, it should be possible to set to false and have
        //  all the consuming classes in the injects statement
        library = true,
        // Forces validates modules and injections at compile time.
        // If true, includes also additional modules that will complete the dependency graph
        //  using the includes statement for the class included in the injects statement
        complete = true
)
public class AndroidModule {
    private final MyApp mApp;

    public AndroidModule(MyApp app) {
        mApp = app;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link it.rainbowbreeze.picama.common.ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    public Context provideApplicationContext() {
        return mApp;
    }
}
