package ev3Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class ev3Client {
				
	public static void z_rotate_robot(float z_angle) {
		RegulatedMotor Z_Motor = Motor.B;
		int z = (int) (z_angle*8.953*2.8);

		Z_Motor.setSpeed(100);
        	Z_Motor.rotate(-z);
		Delay.msDelay(1000);
	}

	public static void x_rotate_robot(float x_angle){
		RegulatedMotor X_Motor = Motor.C;
		int x = (int) (x_angle*2.2);
		if (x_angle < 0) x = x ;
		else x = x;

		X_Motor.setSpeed(30);
       		X_Motor.rotate(x);
		Delay.msDelay(1000);
	}
		
	public static void shoot_robot(float z_angle, float x_angle, float velocity) {	
		RegulatedMotor S_Motor = Motor.D;
		RegulatedMotor L_Motor = Motor.A;
		
//		int z = (int) (z_angle*8.953);
		// int x = (int) x_angle*3.22 + 1.5 ;
		int drag = 1700;

		x_rotate_robot(-x_angle);

		// pull the bow
		S_Motor.setSpeed(800);
		S_Motor.rotate(drag);

		// fix location
		L_Motor.setSpeed(200);
		L_Motor.rotate(50);

		// release the bow
		S_Motor.setSpeed(800);
		S_Motor.rotate(-drag);

		// shoot!
		L_Motor.setSpeed(200);
		L_Motor.rotate(-50);

		// return to original position
		x_rotate_robot(x_angle);
	}
	
	public static void main(String args[]) throws IOException, InterruptedException, NotBoundException{
		
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		
		String serverAddress = "10.0.1.3";
		int serverPort = 8040;
		
		Socket socket = null;
		DataOutputStream streamOut = null;
		DataInputStream streamIn = null;
		try {
			lcd.clear();
			lcd.drawString("Waiting..", 1, 1);
			
			socket = new Socket(serverAddress, serverPort);
			lcd.clear();
			lcd.drawString("Connected", 1, 1);
			
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			streamOut = new DataOutputStream(socket.getOutputStream());
		} catch(UnknownHostException uhe) {
			lcd.drawString("Host unknown: "+uhe.getMessage(), 1, 1);
		}

		Thread.sleep(1000);
		String recvM = "";
		while(keys.getButtons() != Keys.ID_ESCAPE) {
			try {
				streamOut.writeBytes("Client ready\n");
				streamOut.flush();
				
				recvM = streamIn.readUTF();
				
				String[] datas = recvM.split(" ");
				boolean finish = false;
				for (String elem : datas) {
					if (elem.equals("Impossible") || elem.equals("Nothing")) { // impossible target, finish program
						finish = true;
					} 
					else {
						String[] data = elem.split("/");
						Float z_angle = Float.parseFloat(data[0]);
						Float x_angle = Float.parseFloat(data[1]);
						Float velocity = Float.parseFloat(data[2]);
						
						lcd.clear();
						System.out.println(data[0] + data[1] + data[2]);
						z_rotate_robot(z_angle);
						
						while (true) { // adjust position by checking the position
							streamOut.writeBytes("Position check\n");
							streamOut.flush();
							recvM = streamIn.readUTF();

							if (recvM.equals("Yes")) { // position fixed and ready to shoot
								break;
							} 
							else if (recvM.equals("Error")) { // error occured
								lcd.clear();
								lcd.drawString("Error", 1, 4);
								finish = true;
								break;
							} 
							else if (recvM.equals("Turn too much")) { // turn too much
								z_rotate_robot(-(z_angle / 2)); // re-rotate half of the angle
								z_angle = z_angle / 2;
							} 
							else { // adjust position
								Float adjust_angle = Float.parseFloat(recvM);
								z_angle = z_angle + adjust_angle;

								z_rotate_robot(adjust_angle);
							}
							Thread.sleep(500);
						}

						if (finish) {
							break; // error occured during adjust position, finish program
						}
						shoot_robot(z_angle, x_angle, velocity);
						z_rotate_robot(-z_angle);
					}
				}
				
				if (finish) {
					lcd.drawString(recvM, 1, 4);
					streamOut.writeBytes("Client finished\n");
					streamOut.flush();
					break;
				}

				Thread.sleep(500);
			} catch(IOException ioe) {
				lcd.clear();
				lcd.drawString("Error: "+ioe.getMessage(), 1, 4);
				break;
			}
		}
		
		if (socket != null) socket.close();
		if (streamOut != null) streamOut.close();
		if (streamIn != null) streamIn.close();
	}
}
