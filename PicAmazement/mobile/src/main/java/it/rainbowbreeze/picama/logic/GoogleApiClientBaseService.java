package it.rainbowbreeze.picama.logic;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;

import it.rainbowbreeze.picama.common.ILogFacility;


/**
 * Base Service to manage Google API Client requests.
 *
 * Extends {@link it.rainbowbreeze.picama.logic.GoogleApiClientBaseService#doYourStuff(android.content.Intent)}
 * with the code requested by your application
 *
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public abstract class GoogleApiClientBaseService extends IntentService {
    private final String LOG_TAG;
    private ILogFacility mLogFacility;
    protected GoogleApiClient mGoogleApiClient;
    private boolean mIsRequestedApiAvailable;

    public GoogleApiClientBaseService(String serviceName) {
        super(serviceName);
        LOG_TAG = getLogTag();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = createGoogleApiClient()
                .build();
    }

    /**
     * Returns the log tag to use
     * @return
     */
    protected abstract String getLogTag();

    /**
     * Returns the required Google API Client API to connect with.
     * Could be Drive.API, Plus.API, Wearable.API etc
     * @return
     */
    protected abstract Api getApi();

    /**
     * Returns the Log facility to use for logging
     * @return
     */
    protected abstract ILogFacility getLogFacility();

    /**
     * Executes the real service code. Is called inside the {@link android.app.IntentService#onHandleIntent(android.content.Intent)}
     * method.<p/>
     * All the required checks (Intent not null, API available) have been already done once
     * arrived here, so go straight with your code :)<p/>
     * This method is executed on a separate thread, as for as {@link android.app.IntentService}
     *
     * @param intent
     */
    public abstract void doYourStuff(Intent intent);

    @Override
    protected void onHandleIntent(Intent intent) {
        assignLogFacility();
        if (!isValidIntent(intent)) {
            mLogFacility.i(LOG_TAG, "Intent received is not valid, aborting");
            return;
        }
        ConnectionResult connectionResult = connectToGoogleApi();
        analyzeConnectionResult(connectionResult);

        if (mGoogleApiClient.isConnected() && isRequestedApiAvailable()) {
            doYourStuff(intent);
            disconnectFromGoogleApi();
        } else {
            mLogFacility.i(LOG_TAG, "Google Api Client isn't connected, aborting");
        }
    }


    /**
     * Checks is Log facility is available
     */
    protected void assignLogFacility() {
        if (null == mLogFacility) {
            mLogFacility = getLogFacility();
            if (null == mLogFacility) {
                throw new IllegalArgumentException("You need a logger that implement ILogFacility");
            }
        }
    }

    /**
     * Checks if given intent is valid and can be processed by {@link it.rainbowbreeze.picama.logic.GoogleApiClientBaseService#onHandleIntent(android.content.Intent)}.
     * Base implementation only checks for null intent
     *
     * Change this method and add your own custom validation logic
     *
     * @param intent
     * @return true if the intent is valid, otherwise false
     */
    protected boolean isValidIntent(Intent intent) {
        return (null != intent);
    }

    /**
     * Analyze connection result to investigate potential issues (like API not
     * supported etc)
     * @param result
     */
    protected void analyzeConnectionResult(ConnectionResult result) {
        if (null == result || result.isSuccess()) {
            mIsRequestedApiAvailable = true;
            mLogFacility.v(LOG_TAG, "Google Api Client connected");
        } else if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            mIsRequestedApiAvailable = false;
            mLogFacility.i(LOG_TAG, "Connection to Google Api successful, but API not supported");
        } else {
            mIsRequestedApiAvailable = false; // Conservative approach
            mLogFacility.i(LOG_TAG, "Connection to Google Api client failed with error " +
                    result.getErrorCode());
        }
    }

    /**
     * Checks if the requested API is available
     * @return true is available, otherwise false
     */
    protected boolean isRequestedApiAvailable() {
        return mIsRequestedApiAvailable;
    }

    /**
     * Google API Client Connection time before giving an error, in seconds
     * Default is 30.
     * @return
     */
    protected int getMaxConnectionTime() {
        return 30;
    }

    /**
     * Creates the basic Google Api Client. Override and add your Builder's methods
     * to customize the final client
     * @return
     */
    protected GoogleApiClient.Builder createGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addApi(getApi());
    }

    /**
     * Connects to Goole API Client
     */
    protected ConnectionResult connectToGoogleApi() {
        return mGoogleApiClient.blockingConnect(
                getMaxConnectionTime(),
                TimeUnit.SECONDS);
    }

    /**
     * Disconnects from Google API Client
     */
    protected void disconnectFromGoogleApi() {
        mGoogleApiClient.disconnect();
    }
}
