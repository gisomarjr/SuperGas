package br.com.gisomarkos.supergas.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Marcos Bastos on 02/08/2015.
 */
public class GasContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.dddd
    public static final String CONTENT_AUTHORITY = "br.com.gisomarkos.supergas";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/cliente/ is a valid path for
    // looking at cliente data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_CLIENTE = "cliente";
    public static final String PATH_FORNECEDOR = "fornecedor";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the fornecedor table */
    public static final class FornecedorEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FORNECEDOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FORNECEDOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FORNECEDOR;

        // Table name
        public static final String TABLE_NAME = "fornecedor";

        // The fornecedor setting string is what will be sent to openclientemap
        // as the fornecedor query.
        public static final String COLUMN_FORNECEDOR_SETTING = "fornecedor_setting";

        // Human readable fornecedor string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_FORN_NOME = "nome";

        // In order to uniquely pinpoint the fornecedor on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openclientemap.
        public static final String COLUMN_FORN_CNPJ = "cnpj";
        public static final String COLUMN_FORN_PROP = "nome_proprietario";
        public static final String COLUMN_FORN_CPF = "cpf";
        public static Uri buildFornecedorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the cliente table */
    public static final class ClienteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENTE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTE;

        public static final String TABLE_NAME = "cliente";

        // Column with the foreign key into the fornecedor table.
        public static final String COLUMN_LOC_KEY = "fornecedor_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Cliente id as returned by API, to identify the icon to be used
        public static final String COLUMN_CLIENTE_ID = "cliente_id";

        // Short description and long description of the cliente, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildClienteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            Student: This is the buildClienteFornecedor function you filled in.
         */
        public static Uri buildClienteFornecedor(String fornecedorSetting) {
            return CONTENT_URI.buildUpon().appendPath(fornecedorSetting).build();
        }

        public static Uri buildClienteFornecedorWithStartDate(
                String fornecedorSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(fornecedorSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildClienteFornecedorWithDate(String fornecedorSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(fornecedorSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getFornecedorSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }

}
