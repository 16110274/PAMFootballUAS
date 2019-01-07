package corp.demo.sportapp;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Upcoming implements Parcelable {

    public static final String LOG_TAG = Upcoming.class.getSimpleName();
    
    @SerializedName("idEvent")
    private Long event_id;
    @SerializedName("strTeam")
    private String event_nama;

    @SerializedName("strLeague")
    private String event_competition;
    
    @SerializedName("strTime")
    private String event_time;


    @SerializedName("dateEvent")
    private String event_date;
    


    public Upcoming(){

    }

    public Upcoming(long event_id,
                    String event_nama,
                    String event_competition,
                    String event_time,
                    String event_date)
    {
        this.event_id = event_id;
        this.event_nama = event_nama;
        this.event_competition = event_competition;
        this.event_time = event_time;
        this.event_date = event_date;
    }

    protected Upcoming(Parcel in){
        event_id = in.readLong();
        event_nama = in.readString();
        event_competition = in.readString();
        event_time = in.readString();
        event_date = in.readString();
    }

    public static final Creator<Upcoming> CREATOR = new Creator<Upcoming>() {
        @Override
        public Upcoming createFromParcel(Parcel in) {
            return new Upcoming(in);
        }

        @Override
        public Upcoming[] newArray(int size) {
            return new Upcoming[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(event_id);
        parcel.writeString(event_nama);
        parcel.writeString(event_competition);
        parcel.writeString(event_time);
        parcel.writeString(event_date);
    }

    //Getter Methods
    public Long getId_team() {
        return event_id;
    }

    public void setEvent_id(Long event_id) {
        this.event_id = event_id;
    }


    public String getEvent_name() {
        return event_nama;
    }

    public void setEvent_name(String event_nama) {
        this.event_nama = event_nama;
    }
    


    public String getEvent_competition() {
        return event_competition;
    }

    public void setEvent_competition(String event_competition) {
        this.event_competition = event_competition;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }


    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_date) {
        this.event_time = event_time;
    }



}
