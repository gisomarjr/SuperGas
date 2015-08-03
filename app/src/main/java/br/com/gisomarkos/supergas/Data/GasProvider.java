package br.com.gisomarkos.supergas.Data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by User on 02/08/2015.
 */
public class GasProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ClienteDbHelper mOpenHelper;

    public static final int CLIENTE = 100;
    public static final int CLIENTE_WITH_FORNECEDOR = 101;
    public static final int CLIENTE_WITH_FORNECEDOR_AND_DATE = 102;
    public static final int FORNECEDOR = 300;

    private static final SQLiteQueryBuilder sClienteByFornecedorSettingQueryBuilder;

    static{
        sClienteByFornecedorSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //cliente INNER JOIN fornecedor ON cliente.fornecedor_id = fornecedor._id
        sClienteByFornecedorSettingQueryBuilder.setTables(
                ClienteContract.ClienteEntry.TABLE_NAME + " INNER JOIN " +
                        ClienteContract.FornecedorEntry.TABLE_NAME +
                        " ON " + ClienteContract.ClienteEntry.TABLE_NAME +
                        "." + ClienteContract.ClienteEntry.COLUMN_LOC_KEY +
                        " = " + ClienteContract.FornecedorEntry.TABLE_NAME +
                        "." + ClienteContract.FornecedorEntry._ID);
    }

    //fornecedor.fornecedor_setting = ?
    private static final String sFornecedorSettingSelection =
            ClienteContract.FornecedorEntry.TABLE_NAME+
                    "." + ClienteContract.FornecedorEntry.COLUMN_FORNECEDOR_SETTING + " = ? ";

    //fornecedor.fornecedor_setting = ? AND date >= ?
    private static final String sFornecedorSettingWithStartDateSelection =
            ClienteContract.FornecedorEntry.TABLE_NAME+
                    "." + ClienteContract.FornecedorEntry.COLUMN_FORNECEDOR_SETTING + " = ? AND " +
                    ClienteContract.ClienteEntry.COLUMN_DATE + " >= ? ";

    //fornecedor.fornecedor_setting = ? AND date = ?
    private static final String sFornecedorSettingAndDaySelection =
            ClienteContract.FornecedorEntry.TABLE_NAME +
                    "." + ClienteContract.FornecedorEntry.COLUMN_FORNECEDOR_SETTING + " = ? AND " +
                    ClienteContract.ClienteEntry.COLUMN_DATE + " = ? ";

    private Cursor getClienteByFornecedorSetting(Uri uri, String[] projection, String sortOrder) {
        String fornecedorSetting = ClienteContract.ClienteEntry.getFornecedorSettingFromUri(uri);
        long startDate = ClienteContract.ClienteEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sFornecedorSettingSelection;
            selectionArgs = new String[]{fornecedorSetting};
        } else {
            selectionArgs = new String[]{fornecedorSetting, Long.toString(startDate)};
            selection = sFornecedorSettingWithStartDateSelection;
        }

        return sClienteByFornecedorSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getClienteByFornecedorSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String fornecedorSetting = ClienteContract.ClienteEntry.getFornecedorSettingFromUri(uri);
        long date = ClienteContract.ClienteEntry.getDateFromUri(uri);

        return sClienteByFornecedorSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sFornecedorSettingAndDaySelection,
                new String[]{fornecedorSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the CLIENTE, CLIENTE_WITH_FORNECEDOR, CLIENTE_WITH_FORNECEDOR_AND_DATE,
        and FORNECEDOR integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    public static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ClienteContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ClienteContract.PATH_CLIENTE, CLIENTE);
        matcher.addURI(authority, ClienteContract.PATH_CLIENTE + "/*", CLIENTE_WITH_FORNECEDOR);
        matcher.addURI(authority, ClienteContract.PATH_CLIENTE + "/*/#", CLIENTE_WITH_FORNECEDOR_AND_DATE);

        matcher.addURI(authority, ClienteContract.PATH_FORNECEDOR, FORNECEDOR);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new ClienteDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ClienteDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case CLIENTE_WITH_FORNECEDOR_AND_DATE:
                return GasContract.ClienteEntry.CONTENT_ITEM_TYPE;
            case CLIENTE_WITH_FORNECEDOR:
                return ClienteContract.ClienteEntry.CONTENT_TYPE;
            case CLIENTE:
                return ClienteContract.ClienteEntry.CONTENT_TYPE;
            case FORNECEDOR:
                return ClienteContract.FornecedorEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "cliente/*/*"
            case CLIENTE_WITH_FORNECEDOR_AND_DATE:
            {
                retCursor = getClienteByFornecedorSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "cliente/*"
            case CLIENTE_WITH_FORNECEDOR: {
                retCursor = getClienteByFornecedorSetting(uri, projection, sortOrder);
                break;
            }
            // "cliente"
            case CLIENTE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ClienteContract.ClienteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "fornecedor"
            case FORNECEDOR: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ClienteContract.FornecedorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Fornecedors to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CLIENTE: {
                normalizeDate(values);
                long _id = db.insert(ClienteContract.ClienteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ClienteContract.ClienteEntry.buildClienteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FORNECEDOR: {
                long _id = db.insert(ClienteContract.FornecedorEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ClienteContract.FornecedorEntry.buildFornecedorUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CLIENTE:
                rowsDeleted = db.delete(
                        ClienteContract.ClienteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FORNECEDOR:
                rowsDeleted = db.delete(
                        ClienteContract.FornecedorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ClienteContract.ClienteEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(ClienteContract.ClienteEntry.COLUMN_DATE);
            values.put(ClienteContract.ClienteEntry.COLUMN_DATE, ClienteContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CLIENTE:
                normalizeDate(values);
                rowsUpdated = db.update(ClienteContract.ClienteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FORNECEDOR:
                rowsUpdated = db.update(ClienteContract.FornecedorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ClienteContract.ClienteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
