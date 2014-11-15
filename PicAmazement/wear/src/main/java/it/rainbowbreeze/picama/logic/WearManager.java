package it.rainbowbreeze.picama.logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import it.rainbowbreeze.picama.R;
import it.rainbowbreeze.picama.common.Bag;
import it.rainbowbreeze.picama.common.ForApplication;
import it.rainbowbreeze.picama.common.ILogFacility;
import it.rainbowbreeze.picama.domain.AmazingPicture;
import it.rainbowbreeze.picama.ui.PictureActivity;

/**
 * Created by alfredomorresi on 02/11/14.
 */
public class WearManager {
    /**
     * Dagger example, the class doesn't need a direct declaration on a module.
     * @Inject annotates the constructor, so the fields are injected, otherwise
     *  every field can be decorated with @Inject and there is no need to decorate
     *  the constructor, that can be without parameters
     *
    private final ILogFacility mLogFacility;
    private final Context mAppContect;
     @Inject
     public WearManager(ILogFacility logFacility, @ForApplication Context appContext) {
         mLogFacility = logFacility;
         mAppContect = appContext;
         mLogFacility.v(LOG_TAG, "Starting Wear Manager");
     }
     */

    private static final String LOG_TAG = WearManager.class.getSimpleName();
    public static final int GCLIENT_TIMEOUT = 30;
    private final ILogFacility mLogFacility;
    private final Context mAppContext;
    private Object mSync;
    private GoogleApiClient mGoogleApiClient;

    @Inject
    public WearManager(ILogFacility logFacility, @ForApplication Context appContext) {
        mLogFacility = logFacility;
        mAppContext = appContext;
    }

    public void init() {
        mGoogleApiClient = new GoogleApiClient.Builder(mAppContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        mLogFacility.v(LOG_TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        mLogFacility.v(LOG_TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        mLogFacility.v(LOG_TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connect to the client.
     * Do not use in the UI thread
     */
    public void onStart() {
        obtainWorkingClient();
    }

    /**
     * Connect to the client
     */
    public void onStartASync() {
        mGoogleApiClient.connect();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
    }


    /**
     * Returns a working Google API Client.
     * Cannot run in the UI thread
     * @return
     */
    public int obtainWorkingClient() {
        //TODO: manages isConnecting
        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(GCLIENT_TIMEOUT, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                mLogFacility.e(LOG_TAG, "Service failed to connect to GoogleApiClient.");
                return connectionResult.getErrorCode();
            }
        }
        return ConnectionResult.SUCCESS;
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        if (ConnectionResult.SUCCESS != obtainWorkingClient()) {
            mLogFacility.e(LOG_TAG, "Aborting get bitmap from asset");
            return null;
        }

        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            mLogFacility.i(LOG_TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    public void sendNewPictureNotificationASync(final AmazingPicture picture) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendNewPictureNotification(picture);
            }
        }).start();
    }

    /**
     * Cannot be called in the UI thread
     * @param picture
     */
    public void sendNewPictureNotification(AmazingPicture picture) {
        // Read the image from the asset
        Bitmap pictureBitmap = loadBitmapFromAsset(picture.getAssetPicture());
        Bag.putPictureBitmap(pictureBitmap);

        // Prepares the notification to open the new activity
        Intent fullscreenIntent = new Intent(mAppContext, PictureActivity.class);
        fullscreenIntent.putExtra(PictureActivity.INTENT_EXTRA_TITLE, picture.getTitle());
        fullscreenIntent.putExtra(PictureActivity.INTENT_EXTRA_IMAGEASSET, picture.getAssetPicture());
        fullscreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                mAppContext,
                0,
                fullscreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // And starts the activity
        //mAppContext.startActivity(startIntent);

        // Page to display information about the picture
        Notification infoPage = new Notification.Builder(mAppContext)
                .setContentTitle(picture.getSource())
                .setContentText(picture.getTitle())
                .build();

        // Action to remove the picture from the stream
        Intent removePicIntent = new Intent(Bag.INTENT_ACTION_REMOVEPICTURE);
        removePicIntent.putExtra(Bag.INTENT_EXTRA_PICTUREID, picture.getId());
        PendingIntent removePicPendingIntent = PendingIntent.getService(
                mAppContext, 0, removePicIntent, 0);
        Notification.Action removePicAction = new Notification.Action(
                R.drawable.ic_launcher,
                mAppContext.getString(R.string.common_removePicture),
                removePicPendingIntent);

        // Action to save the picture from the stream
        Intent savePicIntent = new Intent(Bag.INTENT_ACTION_SAVEPICTURE);
        savePicIntent.putExtra(Bag.INTENT_EXTRA_PICTUREID, picture.getId());
        PendingIntent savePicPendingIntent = PendingIntent.getService(
                mAppContext, 0, savePicIntent, 0);
        Notification.Action savePicAction = new Notification.Action(
                R.drawable.ic_launcher,
                mAppContext.getString(R.string.common_savePicture),
                savePicPendingIntent);

        Notification.WearableExtender wearableExtender = new Notification.WearableExtender()
                .setDisplayIntent(fullScreenPendingIntent)
                .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                //.setGravity(Gravity.CENTER)
                //.setHintShowBackgroundOnly(true) // Boh?
                .setBackground(pictureBitmap)
                .addPage(infoPage);
        Notification.Builder notificationBuilder = new Notification.Builder(mAppContext)
                //.setOngoing(true)  // Notification cannot be swiped right
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(picture.getSource())
                .addAction(removePicAction)
                .addAction(savePicAction)
                .extend(wearableExtender);
        //TODO: think about moving the actions in the WearableExtender
        NotificationManager nm = ((NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE));
        nm.notify(
                Bag.NOTIFICATION_ID_NEWIMAGE,
                notificationBuilder.build());

        mLogFacility.v(LOG_TAG, "Sent notification for picture " + picture.getTitle());
    }
}