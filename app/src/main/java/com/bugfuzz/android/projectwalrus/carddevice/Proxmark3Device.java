package com.bugfuzz.android.projectwalrus.carddevice;

import com.bugfuzz.android.projectwalrus.CardData;

@CardDevice.UsbDevice(vendorId = 11565, productId = 20557)
public class Proxmark3 implements CardDevice {
    public CardData readCardData() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean writeCardData(CardData cardData) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
