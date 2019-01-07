package corp.demo.sportapp.databaseSQLITE;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SportDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sport.db";

    public SportDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_SPORT_TABLE = "CREATE TABLE " + SportContract.SportEntry.TABLE_NAME
                + " (" +
                SportContract.SportEntry._ID + " INTEGER PRIMARY KEY," +
                SportContract.SportEntry.COLUMN_SPORT_ID_TEAM + " INTEGER NOT NULL, " +
                SportContract.SportEntry.COLUMN_SPORT_STR_TEAM + " TEXT NOT NULL, " +
                SportContract.SportEntry.COLUMN_SPORT_STR_STADIUM_THUMB + " TEXT NOT NULL," +
                SportContract.SportEntry.COLUMN_SPORT_STR_DESCRIPTION_EN + " TEXT NOT NULL, " +
                SportContract.SportEntry.COLUMN_SPORT_INT_FORMED_YEAR + " TEXT NOT NULL, " +
                SportContract.SportEntry.COLUMN_SPORT_STR_TEAM_BADGE + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(CREATE_SPORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportContract.SportEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}