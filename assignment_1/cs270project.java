/* 
 * This project is made for getting information on sensor and moving the ev3 kit
 * 
 * A is connected to the left motor
 * B is connected to the right motor
 * S1 is connected to the distance module
 * S2 is connected to the color module
 * 
 * Functions:
 * main() : motorMoveForward() -> color() -> IRSensor()
 * private void motorMoveForward() : moves ev3 forward for certain time(precise changes must be made)
 * private void color() : reads color and motorRotateRight() when color_id < 9, motorRotateLeft() otherwise
 * public static void IRSensor() : If > 23cm print "Box not ahead" if not, print "Box ahead"
 * motorRotateRight() : rotate right(by rotating up the left motor) for certain time(precise changes must be made)
 * motorRotateLeft() : rotate left(by rotating up the right motor) for certain time(precise changes must be made)
 * 
 * To-do:
 * make a new function test() to test the sensors by printing out the variables on the lcd(would not use in practice)
 * take variables in individual sensors to a global variable to stop time loss from defining the same variable again
 * fix bug where the ev3 tilts to the right when going straight
 * when going straight, make ev3 go exactly one block, or 23cm
 * when rotating, make ev3 rotate precisely 90 degrees
 * when rotating, make ev3 go back and rotate for it to be on spot for the next turn
 * -> be careful for edge case when ev3 is surrounded by two boxes(it might move the box when going back for rotation)
 * find a method to account for false outcomes on the EV3ColorSensor
 */

package cs270project;

import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class cs270project {
	private static EV3IRSensor sensor;
	private static EV3ColorSensor color_sensor;
	EV3 ev3 = (EV3) BrickFinder.getLocal();
	TextLCD lcd = ev3.getTextLCD();
	
	private void color(){
		color_sensor = new EV3ColorSensor(SensorPort. S2);
	
		String str = "";
		int color_id = color_sensor.getColorID();
		
		switch(color_id)
		{
		case Color.BLACK:
			str = "Color.Black = "+String.valueOf(color_id);
			break;
		case Color.BLUE:
			str = "Color.Blue = "+String.valueOf(color_id);
			break;
		case Color.BROWN:
			str = "Color.Brown = "+String.valueOf(color_id);
			break;
		case Color.CYAN:
			str = "Color.Cyan = "+String.valueOf(color_id);
			break;
		case Color.DARK_GRAY:
			str = "Color.Dark_Gray = "+String.valueOf(color_id);
			break;
		case Color.GRAY:
			str = "Color.Gray = "+String.valueOf(color_id);
			break;
		case Color.GREEN:
			str = "Color.Green = "+String.valueOf(color_id);
			break;
		case Color.LIGHT_GRAY:
			str = "Color.Light_Gray = "+String.valueOf(color_id);
			break;
		case Color.MAGENTA:
			str = "Color.Magenta = "+String.valueOf(color_id);
			break;
		case Color.NONE:
			str = "Color.None = "+String.valueOf(color_id);
			break;
		case Color.ORANGE:
			str = "Color.Orange = "+String.valueOf(color_id);
			break;
		case Color.PINK:
			str = "Color.Pink = "+String.valueOf(color_id);
			break;
		case Color.RED:
			str = "Color.Red = "+String.valueOf(color_id);
			break;
		case Color.WHITE:
			str = "Color.White = "+String.valueOf(color_id);
			break;
		case Color.YELLOW:
			str = "Color.Yellow = "+String.valueOf(color_id);
			break;
		}
		lcd.clear();
		lcd.drawString(str, 1, 4);
		Delay.msDelay(10000);
		if(color_id == Color.RED) motorRotateRight();
		else motorRotateLeft();
	}
	
	private boolean IRSensor(){
		sensor = new EV3IRSensor(SensorPort. S1);
		
		SampleProvider distanceMode = sensor.getDistanceMode();
		float value[] = new float[distanceMode.sampleSize()];
		Delay.msDelay(30000);
		distanceMode.fetchSample(value, 0);
		float centimeter = value[0];
		lcd.clear();
		if(centimeter < 23)lcd.drawString("box ahead : "+centimeter, 1, 4);
		else lcd.drawString("box not ahead : "+centimeter, 1, 4);
		Delay.msDelay(30000);
		return centimeter < 1;
	}

	public static void motorMoveForward() {
		RegulatedMotor leftMotor = Motor. A;
		RegulatedMotor rightMotor = Motor. B;
		
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		
		leftMotor.forward();
		rightMotor.forward();
		
		try {
			Thread.sleep(3000);
		}catch(InterruptedException e) {}
		
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public static void motorRotateLeft() {
		RegulatedMotor rightMotor = Motor. B;
		
		rightMotor.setSpeed(400);
		rightMotor.setAcceleration(800);
		
		rightMotor.forward();
		
		try {
			Thread.sleep(3000);
		}catch(InterruptedException e) {}
		
		rightMotor.stop();
	}

	public static void motorRotateRight() {
		RegulatedMotor leftMotor = Motor. A;
		
		leftMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		
		leftMotor.forward();
		
		try {
			Thread.sleep(3000);
		}catch(InterruptedException e) {}
		
		leftMotor.stop();
	}
	
	public static void main(String[] args) {
		cs270project project = new cs270project();
		motorMoveForward();
		project.color();
		project.IRSensor();
	}
}
