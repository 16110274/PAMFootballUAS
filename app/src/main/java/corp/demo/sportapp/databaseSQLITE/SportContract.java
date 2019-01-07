package corp.demo.sportapp.databaseSQLITE;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class SportContract {

    public static final String CONTENT_AUTHORITY = "corp.demo.sportapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SPORT = "sport";

    public static final class SportEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPORT).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPORT;

        public static final String TABLE_NAME = "sport";
        public static final String COLUMN_SPORT_ID_TEAM = "id_team";
        public static final String COLUMN_SPORT_STR_TEAM = "team_name";
        public static final String COLUMN_SPORT_STR_STADIUM_THUMB = "team_stadium";
        public static final String COLUMN_SPORT_STR_DESCRIPTION_EN = "team_description";
        public static final String COLUMN_SPORT_INT_FORMED_YEAR = "team_formed";
        public static final String COLUMN_SPORT_STR_TEAM_BADGE = "team_badge";


        public static Uri buildSportUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] SPORT_COLUMNS = {
                COLUMN_SPORT_ID_TEAM,
                COLUMN_SPORT_STR_TEAM,
                COLUMN_SPORT_STR_STADIUM_THUMB,
                COLUMN_SPORT_STR_DESCRIPTION_EN,
                COLUMN_SPORT_INT_FORMED_YEAR,
                COLUMN_SPORT_STR_TEAM_BADGE
        };

        public static final int COL_SPORT_ID_TEAM = 0;
        public static final int COL_SPORT_STR_TEAM = 1;
        public static final int COL_SPORT_STR_STADIUM_THUMB = 2;
        public static final int COL_SPORT_STR_DESCRIPTION_EN = 3;
        public static final int COL_SPORT_INT_FORMED_YEAR = 4;
        public static final int COL_SPORT_STR_TEAM_BADGE = 5;

    }
}