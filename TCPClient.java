package com.tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

import javax.jws.soap.SOAPBinding;

public class TCPClient {

	public static void main(String args[]) {
		int port=new Integer (args[1]);  
		for(int i=0;i<301;i++){
			try {

				String message, response;
				
				Socket socket = new Socket(args[0], port);

				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				//message = reader.readLine() + System.lineSeparator();

				output.writeBytes("HELLO "+ args[0]+", "+args[1]+" USER ID:"+(i+1) + System.lineSeparator());
				response = server.readLine();
				
				System.out.println(response);
				System.out.println("Message "+(i+1));				
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}

