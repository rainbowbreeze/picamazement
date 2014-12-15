package it.rainbowbreeze.picama.data.provider.picture;

import java.util.Date;

import android.database.Cursor;

import it.rainbowbreeze.picama.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code picture} table.
 */
public class PictureCursor extends AbstractCursor {
    public PictureCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code url} value.
     * Cannot be {@code null}.
     */
    public String getUrl() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.URL);
        return getString(index);
    }

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    public String getTitle() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.TITLE);
        return getString(index);
    }

    /**
     * Get the {@code desc} value.
     * Cannot be {@code null}.
     */
    public String getDesc() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.DESC);
        return getString(index);
    }

    /**
     * Get the {@code author} value.
     * Can be {@code null}.
     */
    public String getAuthor() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.AUTHOR);
        return getString(index);
    }

    /**
     * Get the {@code source} value.
     * Cannot be {@code null}.
     */
    public PictureSource getSource() {
        Integer intValue = getIntegerOrNull(PictureColumns.SOURCE);
        if (intValue == null) return null;
        return PictureSource.values()[intValue];
    }

    /**
     * Get the {@code date} value.
     * Cannot be {@code null}.
     */
    public Date getDate() {
        return getDate(PictureColumns.DATE);
    }

    /**
     * Get the {@code visible} value.
     */
    public boolean getVisible() {
        return getBoolean(PictureColumns.VISIBLE);
    }

    /**
     * Get the {@code saveasked} value.
     */
    public boolean getSaveasked() {
        return getBoolean(PictureColumns.SAVEASKED);
    }

    /**
     * Get the {@code savefinished} value.
     */
    public boolean getSavefinished() {
        return getBoolean(PictureColumns.SAVEFINISHED);
    }
}
