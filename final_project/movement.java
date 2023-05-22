package movement;

import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.Keys;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class movement {	
	// Each angles will be calculated here
	  public static int z_angle = 0;
	  public static int x_angle = 0;
			
	// A is connected to Z_motor
	// B is connected to X_motor
	  public static RegulatedMotor Z_Motor = Motor. A;
	  public static RegulatedMotor X_Motor = Motor. B;
	  
	  public static void Rotate_Z() {
		Z_Motor.rotate(z_angle);
		Delay.msDelay(1000);
	  }
		
	  public static void Rotate_X() {
		X_Motor.rotate(x_angle);
		Delay.msDelay(1000);
	  }

	  public static void Return() {
		X_Motor.rotate(-x_angle);
		Delay.msDelay(1000);
		Z_Motor.rotate(-z_angle);
		Delay.msDelay(1000);
	  }

	  public static void main(String[] args) {
		Rotate_Z();
		Rotate_X();
			
		// Shooting function
		
		Return();
	  }
}
