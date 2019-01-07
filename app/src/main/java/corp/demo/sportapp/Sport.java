package corp.demo.sportapp;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

public class Sport implements Parcelable {

    public static final String LOG_TAG = Sport.class.getSimpleName();


    @SerializedName("idTeam")
    private Long id_team;

    @SerializedName("strTeam")
    private String team_name;
    @SerializedName("strStadiumThumb")
    private String team_stadium;
    @SerializedName("strDescriptionEN")
    private String team_description;
    @SerializedName("intFormedYear")
    private String team_formed;
    @SerializedName("strTeamBadge")
    private String team_badge;

    public Sport(){

    }

    public Sport(long id_team,
                 String team_name,
                 String team_stadium,
                 String team_description,
                 String team_formed,
                 String team_badge)
    {
        this.id_team = id_team;
        this.team_name = team_name;
        this.team_stadium = team_stadium;
        this.team_description = team_description;
        this.team_formed = team_formed;
        this.team_badge = team_badge;
    }

    protected Sport(Parcel in){
        id_team = in.readLong();
        team_name = in.readString();
        team_stadium = in.readString();
        team_description = in.readString();
        team_formed = in.readString();
        team_badge = in.readString();
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id_team);
        parcel.writeString(team_name);
        parcel.writeString(team_stadium);
        parcel.writeString(team_description);
        parcel.writeString(team_formed);
        parcel.writeString(team_badge);
    }

    //Getter Methods
    public Long getId_team() {
        return id_team;
    }

    public void setId_team(Long id) {
        this.id_team = id;
    }


    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_stadium() {
        if (team_stadium != null && !team_stadium.isEmpty()) {
            if(!team_stadium.toLowerCase().contains("https://")){
                return "https://www.thesportsdb.com/images/media/team/stadium/" + team_stadium;
            }else{
                return team_stadium;
            }

        }
        return null; //Use Picasso to put placeholder for poster
    }

    public void setTeam_stadium(String team_stadium) {
        this.team_stadium = team_stadium;
    }

    public String getTeam_description() {
        return team_description;
    }

    public void setTeam_description(String team_description) {
        this.team_description = team_description;
    }

    public String getTeam_formed() {
        return team_formed;
    }

    public void setTeam_formed(String team_formed) {
        this.team_formed = team_formed;
    }

    @Nullable
    public String getTeam_badge() {
        if (team_badge != null && !team_badge.isEmpty()) {

            if(!team_badge.toLowerCase().contains("https://")){
                return "https://www.thesportsdb.com/images/media/team/badge/" + team_badge;
            }else{
                return team_badge;
            }

        }
        return null; //Use Picasso to put placeholder for poster
    }

    public void setTeam_badge(String team_badge) {
        this.team_badge = team_badge;
    }

}
