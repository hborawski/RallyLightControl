#include <Servo.h>
#include <SoftwareSerial.h>
// Define constants
#define RxD 11
#define TxD 10
#define HOR_RATIO 3.33
#define VER_RATIO 3.45
// Declare objects
Servo verticalservo;
Servo horizontalservo;
SoftwareSerial bluetoothSerial(RxD,TxD);

// Declare variables
char pitch;
char yaw;
char sig;

void setup() {
  // Attach servo objects to pins 9 and 6 (PWM pins)
  verticalservo.attach(9);
  horizontalservo.attach(6);
  // Set servos to 'zero' positions
  horizontalservo.write(90);
  verticalservo.write(0);
  // Start bluetooth serial port
  bluetoothSerial.begin(9600);
  bluetoothSerial.println("Bluetooth On");
}

void loop() {
  // Check if data is available to read
  if (bluetoothSerial.available() > 0) {
    // How many bytes are left to read after reading the signal byte
    int count = bluetoothSerial.available() -1;
    // Read the signal
    sig = bluetoothSerial.read();
    // Android app sends 'm' for move and then 2 integers
    if (sig == 'm' && count == 2) {
      // Read pitch and yaw values
      pitch = bluetoothSerial.read();
      yaw = bluetoothSerial.read();
      // Write out to servo while adjusting for gear ratios
      verticalservo.write(30 + pitch * VER_RATIO);
      horizontalservo.write(yaw * HOR_RATIO);
    }
  }
  // Pause for 100ms
  delay(100);
}


