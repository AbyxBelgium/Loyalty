package com.abyx.loyalty.contents;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.zxing.BarcodeFormat;

public class Card implements Parcelable{
    private String name;
    private String barcode;
    private String imageLocation;
    private BarcodeFormat format;
    private String defaultURL = "https://cdn4.iconfinder.com/data/icons/devine_icons/Black/PNG/Folder%20and%20Places/Stack.png";

    public Card(String name, String barcode, String imageLocation, BarcodeFormat format){
        this.name = name;
        this.barcode = barcode;
        this.imageLocation = imageLocation;
        this.format = format;
    }

    public Card(String name, String barcode, BarcodeFormat format){
        this.name = name;
        this.barcode = barcode;
        //Default image is used when nothing is set
        this.imageLocation = defaultURL;
        this.format = format;
    }

    public Card(Parcel in){
        this.name = in.readString();
        this.barcode = in.readString();
        this.imageLocation = in.readString();
        this.format = BarcodeFormat.valueOf(in.readString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImageLocation() {
        return String.valueOf(imageLocation.hashCode());
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageURL(){
        return imageLocation;
    }

    public String getSaveRepresentation(){
        return name + "\t" + barcode + "\t" + imageLocation + "\t" + format.toString();
    }

    public BarcodeFormat getFormat(){
        return format;
    }

    public void setFormat(BarcodeFormat format){
        this.format = format;
    }

    public void setDefaultImageLocation(){
        this.imageLocation = defaultURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(barcode);
        parcel.writeString(imageLocation);
        parcel.writeString(format.toString());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return !(!barcode.equals(card.barcode) || !name.equals(card.name)) &&
                imageLocation.equals(card.imageLocation);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + barcode.hashCode();
        result = 31 * result + imageLocation.hashCode();
        return result;
    }
}
