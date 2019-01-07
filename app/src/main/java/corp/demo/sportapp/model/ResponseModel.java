
package corp.demo.sportapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@SuppressWarnings("unused")
public class ResponseModel {


    List<DataModel> result;
    @SerializedName("kode")
    private String mKode;
    @SerializedName("pesan")
    private String mPesan;

    public List<DataModel> getResult() {
        return result;
    }

    public void setResult(List<DataModel> result) {
        this.result = result;
    }
    public String getKode() {
        return mKode;
    }

    public void setKode(String kode) {
        mKode = kode;
    }

    public String getPesan() {
        return mPesan;
    }

    public void setPesan(String pesan) {
        mPesan = pesan;
    }

}
