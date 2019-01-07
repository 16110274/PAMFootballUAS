
package corp.demo.sportapp.model;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class DataModel {


    @SerializedName("id_food")
    private String mId;
    @SerializedName("name_food")
    private String mNama;
    @SerializedName("price_food")
    private String mPrice;
    @SerializedName("image_food")
    private String mFoto;



    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getNama() {
        return mNama;
    }

    public void setNama(String nama) {
        mNama = nama;
    }

    public String getHarga() { return mPrice;   }

    public void setHarga(String harga) {
        mPrice = harga;
    }

    public String getFoto() {
        return mFoto;
    }

    public void setFoto(String foto) {  mFoto = foto;   }

}
