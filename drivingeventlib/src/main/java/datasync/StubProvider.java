package datasync;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class StubProvider extends ContentProvider {

    public static final Uri URI = new Uri.Builder().scheme( ContentResolver.SCHEME_CONTENT ).authority( SyncUtil.AUTHORITY ).build();

    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs ) {
        return 0;
    }

    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /*
     * query() always returns no results
     *
     */
    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
        return null;
    }

    /*
     * Return no type for MIME type
     */
    @Override
    public String getType( Uri uri ) {
        return null;
    }

    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert( Uri uri, ContentValues values ) {
        return null;
    }

    /*
     * update() always returns "no rows affected" (0)
     */
    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
        return 0;
    }
}
