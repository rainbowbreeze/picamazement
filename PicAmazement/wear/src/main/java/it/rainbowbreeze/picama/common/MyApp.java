package it.rainbowbreeze.picama.common;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

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
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {

        super.onCreate();
        mObjectGraph = ObjectGraph.create(getModules().toArray());
    }

    /**
     * Could be overridden in tests, passing a different list of modules
     * @return
     */
    private List<Object> getModules() {
        // If you define all the dependencies directly in the module class annotation
        // using includes, you don't need to list all the modules here. Otherwise,
        // this is the right place to define all modules
//        return Arrays.asList(
//                new AndroidModule(this),
//                new MobileModule()
//        );
        return Arrays.asList(
                (Object) new AndroidModule(this)
        );
    }

    /**
     * Used by activities to inject themselves with the required dependencies
     * @param object
     */
    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

}
