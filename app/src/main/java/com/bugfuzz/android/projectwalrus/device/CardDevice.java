package com.bugfuzz.android.projectwalrus.device;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.bugfuzz.android.projectwalrus.data.CardData;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class CardDevice {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UsbCardDevice {
        @Retention(RetentionPolicy.RUNTIME)
        public @interface IDs {
            int vendorId();
            int productId();
        }

        IDs[] value();
    }

    protected UsbDevice usbDevice;
    protected UsbDeviceConnection usbDeviceConnection;

    public CardDevice(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
        this.usbDevice = usbDevice;
        this.usbDeviceConnection = usbDeviceConnection;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public abstract String getName();

    public abstract CardData readCardData() throws IOException;
    public abstract void writeCardData(CardData cardData) throws IOException;
}
