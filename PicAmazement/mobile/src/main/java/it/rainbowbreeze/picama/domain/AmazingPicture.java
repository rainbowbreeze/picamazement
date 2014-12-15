package it.rainbowbreeze.picama.domain;

import com.google.android.gms.wearable.DataMap;

import it.rainbowbreeze.picama.data.provider.picture.PictureContentValues;
import it.rainbowbreeze.picama.data.provider.picture.PictureCursor;

/**
 * Created by alfredomorresi on 09/11/14.
 */
public class AmazingPicture extends BaseAmazingPicture {
    public AmazingPicture() {
        super();
    }

    public void fillDataMap(DataMap dataMap) {
        dataMap.putLong(FIELD_ID, getId());
        dataMap.putString(FIELD_URL, getUrl());
        dataMap.putString(FIELD_TITLE, getTitle());
        dataMap.putString(FIELD_DESC, getDesc());
        dataMap.putString(FIELD_SOURCE, getSource());
    }

    public AmazingPicture fromCursor(PictureCursor c) {
        this
                .setId(c.getId())
                .setUrl(c.getUrl())
                .setDate(c.getDate())
                .setTitle(c.getTitle())
                .setDesc(c.getDesc())
                .setSource(c.getSource());
        return this;
    }

    public void fillContentValues(PictureContentValues values) {
        values
                .putTitle(getTitle())
                .putDesc(getDesc())
                .putUrl(getUrl())
                .putSource(getSource())
                .putDate(getDate());

    }
}
