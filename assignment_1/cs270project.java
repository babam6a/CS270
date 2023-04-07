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
 * private void motorMoveForward() : moves ev3 forward for certain time(changes to parameters must be made)
 * private void color() : reads color and motorRotateRight() when color_id < 9, motorRotateLeft() otherwise
 * public static void IRSensor() : If > 23cm print "Box not ahead" if not, print "Box ahead"
 * motorMoveBackward() : ev3 moves back before each rotation for precise positioning(changes to parameters must be made)
 * motorRotateRight() : rotate right(by rotating up the left motor) for certain time(changes to parameters must be made)
 * motorRotateLeft() : rotate left(by rotating up the right motor) for certain time(changes to parameters must be made)
 * test() : function to print out strings on the lcd pannel for testing
 * 
 * when rotating, make ev3 goes back and rotates for it to be on spot for the next motorMoveForward() move
 * -> be careful for edge case when ev3 is surrounded by two boxes(it might move the box when going back for rotation)
 * 
 * To-do:
 * fix bug where the ev3 tilts a little when going straight
 * when going straight, make ev3 go exactly one block, or 23cm
 * when rotating, make ev3 rotate precisely 90 degrees
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
	private final boolean testing = true; // change this value to toggle on/off the test function
	private static EV3IRSensor sensor = new EV3IRSensor(SensorPort. S1);
	private static EV3ColorSensor color_sensor = new EV3ColorSensor(SensorPort. S2);
	EV3 ev3 = (EV3) BrickFinder.getLocal();
	TextLCD lcd = ev3.getTextLCD();
	
	private void test(int testNum, float result){
		lcd.clear();
		String str = "";
		
		/* test for function color() */
		if(testNum == 1) {
			int color_id = (int) result;
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
			lcd.drawString(str, 1, 4);
		}
		
		/* test for function IRSensor() */
		if(testNum == 2){
			str = "distance : " + result;
			lcd.drawString(str, 1, 4);
		}
		
		if(testNum == 3) {
			str = "turn" + result;
			lcd.drawString(str, 2, 4);
		}
	}
	
	private void color(){
		int color_id = color_sensor.getColorID();
		if(testing) test(1, color_id);
	}
	
	private boolean IRSensor(){
		
		SampleProvider distanceMode = sensor.getDistanceMode();
		float value[] = new float[distanceMode.sampleSize()];
		distanceMode.fetchSample(value, 0);
		float centimeter = value[0];
		
		if(testing) test(2, centimeter); // test number for IRSensor is 2
		Delay.msDelay(3000);
		return centimeter < 1;
	}

	public static void motorMoveForward() {
		RegulatedMotor leftMotor = Motor. A;
		RegulatedMotor rightMotor = Motor. B;
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		leftMotor.rotate(627);
		rightMotor.rotate(627);
		leftMotor.endSynchronization();

		Delay.msDelay(3000);
	}
	
	public static void motorRotateLeft() {
		RegulatedMotor leftMotor = Motor. A;
		RegulatedMotor rightMotor = Motor. B;
		
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		leftMotor.rotate(-350);
		rightMotor.rotate(350);
		leftMotor.rotate(180);
	}

	public static void motorRotateRight() {
		RegulatedMotor leftMotor = Motor. A;
		RegulatedMotor rightMotor = Motor. B;
		
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		rightMotor.rotate(-350);
		leftMotor.rotate(350);
		rightMotor.rotate(175);
	}
	
	public static void main(String[] args) {
		cs270project project = new cs270project();
		motorMoveForward();
		Delay.msDelay(5000);
		project.IRSensor();
		Delay.msDelay(10000);
	}
}
