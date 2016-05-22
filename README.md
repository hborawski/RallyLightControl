# RallyLightControl
Rally light control arduino sketch and android app


This was created for Angel Rivera for Spring Semester 2016 for a Material Analysis and Design class.

# How to Run
1. Arduino
  1. Open sketch (.ino file) in the Arduino IDE
  2. Upload to plugged in Arduino board
  3. Attach wires for RxD and TxD on bluetooth HC-05 module to pins 11 and 10 respectively
  4. Attach vertical and horizontal servo wires to pins 9 and 6 respectively
  5. Attach power and ground where applicable.
2. Android
  1. Open project in Android Studio
  2. Plug in compatible device (Android 5.1 or above with bluetooth)
  3. Press the 'play' button (build the app)
  4. Choose the plugged in device
  5. Wait for it to launch
3. Using the app
  1. In system settings on Android, pair with the HC-05 module. The default pin is 1234.
  2. Open the RallyLightControl app
  3. Press the menu button if your device has a physical button, or the triple dot menu button at the top right of the screen
  4. Select "Choose Device" to open the paired device selection
  5. Choose the HC-05 module
  6. Open the menu from step 3
  7. Press the "connect" button to open the bluetooth serial port connection
  8. Press a preset to send the values to the Arduino
  9. Press the Plus button to add a new preset
  10. Long press on a preset in the list to bring up the delete option.
