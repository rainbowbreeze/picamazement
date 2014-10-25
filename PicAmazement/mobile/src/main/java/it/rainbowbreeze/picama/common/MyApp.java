package it.rainbowbreeze.picama.common;

import android.app.Application;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import it.rainbowbreeze.picama.shared.BuildConfig;

/**
 * This file is part of KeepMoving. KeepMoving is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * <p/>
 * Copyright Alfredo Morresi
 * <p/>
 * Created by Alfredo "Rainbowbreeze" Morresi on 15/06/14.
 */

/**
 * Builds Dagger's object graph.
 * For additional information
 * - http://www.joshlong.com/jl/blogPost/dependency_injection_with_dagger_on_android.html
 */
public class MyApp extends Application {
    public static final String APP_NAME_FOR_LOG = "PicAmazement";

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);
        Picasso.with(getApplicationContext()).setLoggingEnabled(BuildConfig.DEBUG);
    }

    /*
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(getModules().toArray());
    }

    /**
     * Cloud be overridden in tests, passing a different list of modules
     * @return
     */
    /*
    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new DaggerModule()
        );
    }

    /**
     * Used by activities to inject themselves with the required dependencies
     * @param object
     */
    /*
    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
    */

    private static ILogManager mLogManager;
    public static ILogManager getLogManager() {
        if (null == mLogManager) {
            mLogManager = new LogManager(APP_NAME_FOR_LOG);
        }
        return mLogManager;
    }
}
