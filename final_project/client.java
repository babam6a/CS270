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


public class ev3Client {
	public static void move_robot(float z_angle, float x_angle, float velocity) {
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
		
		String sendM = "";
		String recvM = "";
		while(keys.getButtons() != Keys.ID_ESCAPE) {
			try {
				sendM = "Client ready";
				streamOut.writeUTF(sendM);
				streamOut.flush();
				
				recvM = streamIn.readUTF();
				if (recvM.equals("Impossible target")) { // impossible target, finish program
					lcd.drawString(recvM, 1, 4);
					sendM = "Client finished";
					streamOut.writeUTF(sendM);
					streamOut.flush();
					break;
				} else {
					String[] data = recvM.split(" ");
					move_robot(Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
				}
				Thread.sleep(1000);
			} catch(IOException ioe) {
				lcd.drawString("Sending error: "+ioe.getMessage(), 1, 4);
			}
		}
		
		if (socket != null) socket.close();
		if (streamOut != null) streamOut.close();
		if (streamIn != null) streamIn.close();
	}
}
