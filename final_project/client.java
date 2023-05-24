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
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class ev3Client {
	public static void move_robot(float z_angle, float x_angle, float velocity) {
		RegulatedMotor Z_Motor = Motor.A;
		RegulatedMotor X_Motor = Motor.B;
		int z = (int) z_angle;
		int x = (int) x_angle;
		Z_Motor.rotate(z);
		Delay.msDelay(1000);
		X_Motor.rotate(x);
		Delay.msDelay(1000);
		
		Z_Motor.ratate(-z);
		Delay.msDelay(1000);
		X_Motor.rotate(-x);
		Delay.msDelay(1000);
		return;
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
					} else {
						String[] data = elem.split("/");
						lcd.clear();
						System.out.println(data[0] + data[1] + data[2]);
						move_robot(Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
					}
				}
				
				if (finish) {
					lcd.drawString(recvM, 1, 4);
					streamOut.writeBytes("Client finished\n");
					streamOut.flush();
					break;
				}
				Thread.sleep(1000);
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
