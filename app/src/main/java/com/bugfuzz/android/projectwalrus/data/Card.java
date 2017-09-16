package com.bugfuzz.android.projectwalrus.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.parceler.Parcel;

import java.util.Date;

@DatabaseTable()
@Parcel
public class Card {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String name;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public CardData cardData;

    @DatabaseField
    public Date cardDataAcquired;

    @DatabaseField
    public String details;

    @DatabaseField
    public String notes;


    public Card() {
    }

    public void setCardData(CardData cardData){
        this.cardData = cardData;
        cardDataAcquired = new Date();
    }


}
