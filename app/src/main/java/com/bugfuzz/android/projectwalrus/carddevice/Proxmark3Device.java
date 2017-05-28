package com.bugfuzz.android.projectwalrus.carddevice;

import android.hardware.usb.UsbDevice;

import com.bugfuzz.android.projectwalrus.CardData;

@CardDevice.UsbCardDevice({
        @CardDevice.UsbCardDevice.IDs(vendorId = 11565, productId = 20557),
        @CardDevice.UsbCardDevice.IDs(vendorId = 39620, productId = 19343)
})
public class Proxmark3Device extends CardDevice {
    public Proxmark3Device(UsbDevice usbDevice) {
        super(usbDevice);
    }

    public String getName() {
        return "Proxmark3";
    }

    public CardData readCardData() {
        CardData cd = new CardData();
        cd.data = "hi";
        return cd;
    }

    public boolean writeCardData(CardData cardData) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
