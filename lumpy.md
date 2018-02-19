---
layout: page
title: Lumpy
permalink: /lumpy/
---

# Adding Bluetooth support to the [Bishop Fox RFID Tastic Thief](https://www.bishopfox.com/resources/tools/rfid-hacking/attack-tools/)

Lumpy is based on the previous work done by Fran Brown and the awesome team who worked on the Tastic RFID Thief project over at Bishop Fox. Lumpy improves on [Bishop Fox's Tastic RFID Thief](https://www.bishopfox.com/resources/tools/rfid-hacking/attack-tools/) by adding bluetooth support for the minimal cost of $3.47. This enables wireless communication between the long range card reader and our Walrus app, allowing cloned cards to be sent to your Walrus wallet via a bluetooth connection.

![lumpy]({{ "/assets/lumpy_hc06.png" }})

---
## Equipment Needed
You will need a wireless serial Bluetooth RF transceiver. The HC06 is a cheap solution that does the trick. I picked up one from [ebay](https://www.ebay.com/itm/Wireless-Serial-4-Pin-Bluetooth-RF-Transceiver-Module-HC-06-RS232-With-backplane-/200924726178) for $3.47 USD:

![HC06]({{ "/assets/hc06.png" }})

## Change the Baud rate of HC06
First change the operating board rate of the HC06. This will vary on the equipment you have, and there are a few instructions and guides on how to change the default settings of the HC06 using AT commands. Commands vary on the board so here are instructions used for both [HC05](http://www.instructables.com/id/AT-command-mode-of-HC-05-Bluetooth-module/) and [HC06](http://www.instructables.com/id/How-to-Change-the-Name-of-HC-06-Bluetooth-Module/) modules. I found that HC05 commands worked when changing the baud rate of my HC06 board -  ¯\\\_(ツ)\_/¯. So don't be afraid to experiment...

The operating baud rate of the HC06 module needs to be set to `57600` to correctly receive data from the Tastic RFID Thief Arduino board.

The baud rate was found in the source code for the Arduino board used in the Bishop fox Tastic RFID Thief project still available for download [here](http://www.bishopfox.com/download/814/). The relevant section of code is shown below:

```csharp
// Set up function from Tastic_RFID_Adrudion
void setup()
{
  pinMode(13, OUTPUT);  // LED
  pinMode(2, INPUT);     // DATA0 (INT0)
  pinMode(3, INPUT);     // DATA1 (INT1)
  
  Serial.begin(57600);   // This is the baud rate we need to configure the HC06 bluetooth module to 
  Serial.println("RFID Readers");
  ...
```

## Design Overview and Changes
The PCB (originally designed by Bishop Fox) was modified and bluetooth support added, enabling cloned access card information to be sent directly to the Walrus Android app. The PCB should still be able to fit into the same commercial RFID readers as before, and cloned badges are still written to the CARDS.txt file on the external microSD card.  

![Walrus-and-lumpy-overview]({{ "/assets/walrus_overview.png" }})

## Breadboard Layout
To get the cloned card read from the Tastic RFID Thief Arduino microcontroller, simply connect the D1/TX pin from the Arduino microcontroller to the RX of the HC06 Bluetooth module. The breadboard layout below should give an idea of how the HC06 module should be connected:

![Tastic-modifications-breadboard-layout]({{ "/assets/Tastic-Custom_RFID_Stealer_PCB_du_2018.png" }})
