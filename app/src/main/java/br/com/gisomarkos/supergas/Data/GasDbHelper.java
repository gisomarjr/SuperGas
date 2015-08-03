package br.com.gisomarkos.supergas.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by User on 02/08/2015.
 */
    public class GasDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;

        public static final String DATABASE_NAME = "supergas.db";

        public GasDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Create a table to hold fornecedors.  A fornecedor consists of the string supplied in the
            // fornecedor setting, the city name, and the latitude and longitude
            final String SQL_CREATE_FORNECEDOR_TABLE = "CREATE TABLE " + GasContract.FornecedorEntry.TABLE_NAME + " (" +
                    GasContract.FornecedorEntry._ID + " INTEGER PRIMARY KEY," +
                    GasContract.FornecedorEntry.COLUMN_FORNECEDOR_SETTING + " TEXT UNIQUE NOT NULL, " +
                    GasContract.FornecedorEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                    GasContract.FornecedorEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                    GasContract.FornecedorEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                    " );";

            final String SQL_CREATE_CLIENTE_TABLE = "CREATE TABLE " + ClienteEntry.TABLE_NAME + " (" +
                    // Why AutoIncrement here, and not above?
                    // Unique keys will be auto-generated in either case.  But for cliente
                    // forecasting, it's reasonable to assume the user will want information
                    // for a certain date and all dates *following*, so the forecast data
                    // should be sorted accordingly.
                    ClienteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    // the ID of the fornecedor entry associated with this cliente data
                    ClienteEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                    ClienteEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                    ClienteEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                    ClienteEntry.COLUMN_CLIENTE_ID + " INTEGER NOT NULL," +

                    ClienteEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                    ClienteEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                    ClienteEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                    ClienteEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                    ClienteEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                    ClienteEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                    // Set up the fornecedor column as a foreign key to fornecedor table.
                    " FOREIGN KEY (" + ClienteEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                    FornecedorEntry.TABLE_NAME + " (" + FornecedorEntry._ID + "), " +

                    // To assure the application have just one cliente entry per day
                    // per fornecedor, it's created a UNIQUE constraint with REPLACE strategy
                    " UNIQUE (" + ClienteEntry.COLUMN_DATE + ", " +
                    ClienteEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

            sqLiteDatabase.execSQL(SQL_CREATE_FORNECEDOR_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_CLIENTE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            // Note that this only fires if you change the version number for your database.
            // It does NOT depend on the version number for your application.
            // If you want to update the schema without wiping data, commenting out the next 2 lines
            // should be your top priority before modifying this method.
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FornecedorEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClienteEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

}
