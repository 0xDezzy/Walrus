package com.bugfuzz.android.projectwalrus.device.chameleonmini;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.bugfuzz.android.projectwalrus.data.CardData;
import com.bugfuzz.android.projectwalrus.device.CardDevice;
import com.bugfuzz.android.projectwalrus.device.LineBasedUsbSerialCardDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;

@CardDevice.UsbCardDevice({@CardDevice.UsbCardDevice.IDs(vendorId = 5840, productId = 1202)})
public class ChameleonMiniDevice extends LineBasedUsbSerialCardDevice {

    public ChameleonMiniDevice(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
        super(usbDevice, usbDeviceConnection, "\r\n", "ISO-8859-1");

        usbSerialDevice.syncOpen();

        usbSerialDevice.setBaudRate(115200);

        usbSerialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
        usbSerialDevice.setParity(UsbSerialInterface.PARITY_NONE);
        usbSerialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);

        usbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
    }

    @Override
    public String getName() {
        return "Chameleon Mini";
    }

    @Override
    public CardData readCardData() throws IOException {
        writeLine("Config=ISO14443A_READER");
        String line = readLine();
        if (line == null)
            throw new IOException("Couldn't read Config result");
        if (!line.equals("100:OK"))
            throw new IOException("Unexpected response to Config command");

        writeLine("IDENTIFY");
        line = readLine();
        if (line == null)
            throw new IOException("Couldn't read IDENTIFY result");
        switch (line) {
            case "101:OK WITH TEXT":
                break;

            case "203:TIMEOUT":
                throw new IOException("Timed out reading card data");

            default:
                throw new IOException("Unexpected response to IDENTIFY command");
        }

        // Create string result to store response from chameleon mini
        String result = "";
        for (int i = 0; i < 4; i++) {
            result += readLine() + "\n";
        }

        /*String[] result_line = result.split("\n");

        // Create new cardData object and set type and result
        CardData cd = new CardData();
        cd.type = CardData.Type.MIFARE;
        cd.data = result_line[2];
        return cd;*/
        return null;
    }

    @Override
    public void writeCardData(CardData cardData) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
