package it.rainbowbreeze.picama.logic;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import javax.inject.Inject;

import it.rainbowbreeze.picama.common.Bag;
import it.rainbowbreeze.picama.common.ILogFacility;
import it.rainbowbreeze.picama.common.MyApp;
import it.rainbowbreeze.picama.data.AmazingPictureDao;
import it.rainbowbreeze.picama.logic.storage.CloudStorageManager;

/**
 * Created by alfredomorresi on 15/11/14.
 */
public class ManipulatePictureService extends IntentService {
    private static final String LOG_TAG = ManipulatePictureService.class.getSimpleName();
    @Inject ILogFacility mLogFacility;
    @Inject AmazingPictureDao mAmazingPictureDao;
    @Inject CloudStorageManager mCloudStorageManager;

    public static final String ACTION_REMOVE_PICTURE_FROM_LIST = Bag.INTENT_ACTION_REMOVEPICTURE;
    public static final String ACTION_REMOVE_ALL_PICTURES = "it.rainbowbreeze.picama.Action.Picture.RemoveAll";
    public static final String ACTION_HIDE_ALL_VISIBLE_NOT_UPLOADED_PICTURES = "it.rainbowbreeze.picama.Action.Picture.HideAllVisibleNotUploaded";
    public static final String EXTRA_PARAM_PICTURE_ID = Bag.INTENT_EXTRA_PICTUREID;

    public ManipulatePictureService() {
        super(ManipulatePictureService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApp) getApplicationContext()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (TextUtils.isEmpty(intent.getAction())) {
            mLogFacility.i(LOG_TAG, "No action for the service, aborting");
            return;
        }

        if (ACTION_REMOVE_PICTURE_FROM_LIST.equals(intent.getAction())) {
            long pictureId = intent.getLongExtra(EXTRA_PARAM_PICTURE_ID, Bag.ID_NOT_SET);
            if (Bag.ID_NOT_SET == pictureId) {
                mLogFacility.i(LOG_TAG, "No valid parameter for action " + intent.getAction());
                return;
            }
            mLogFacility.v(LOG_TAG, "Hiding from the list picture with id " + pictureId);
            mAmazingPictureDao.hideById(pictureId);

        } else if (ACTION_REMOVE_ALL_PICTURES.equals(intent.getAction())) {
            mLogFacility.v(LOG_TAG, "Removing all pictures from the list");
            mAmazingPictureDao.removeAll();

        } else if (ACTION_HIDE_ALL_VISIBLE_NOT_UPLOADED_PICTURES.equals(intent.getAction())) {
            mLogFacility.v(LOG_TAG, "Hiding all visible but not uploaded pictures from the list");
            mAmazingPictureDao.HideAllVisibleAndNotUploaded();

        } else {
            mLogFacility.e(LOG_TAG, "Cannot process the requested action");
        }
    }
}
