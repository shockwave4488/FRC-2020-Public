

void setup()
{
  Serial.begin(9600);
  // Initialize MCP2515 running at 16MHz with a baudrate of 500kb/s and the masks and filters disabled.

}

int data[2] = {};

void loop()
{
  data[0] = analogRead(A0);
  data[1] = digitalRead(4);
  String message = "&";
  message += String(data[0]);
  message += ":";
  message += String(data[1]);
  Serial.print(message);
  // send data per 100ms
}
