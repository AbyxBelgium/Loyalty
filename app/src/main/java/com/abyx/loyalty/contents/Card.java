/*
 * Copyright 2017 Abyx (https://abyx.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abyx.loyalty.contents;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.zxing.BarcodeFormat;

public class Card implements Parcelable{
    private long id;
    private String name;
    private String barcode;
    private String imageLocation;
    private BarcodeFormat format;

    public Card(String name, String barcode, String imageLocation, BarcodeFormat format){
        // A card without specific ID has ID -1
        this.id = -1;
        this.name = name;
        this.barcode = barcode;
        this.imageLocation = imageLocation;
        this.format = format;
    }

    public Card(String name, String barcode, BarcodeFormat format){
        // A card without specific ID has ID -1
        this.id = -1;
        this.name = name;
        this.barcode = barcode;
        this.imageLocation = "";
        this.format = format;
    }

    public Card(Parcel in){
        this.id = in.readLong();
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

    public void setImageURL(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageURL(){
        return imageLocation;
    }

    public BarcodeFormat getFormat(){
        return format;
    }

    public void setFormat(BarcodeFormat format){
        this.format = format;
    }

    public void setDefaultImageLocation(){
        this.imageLocation = "";
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return this.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
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
