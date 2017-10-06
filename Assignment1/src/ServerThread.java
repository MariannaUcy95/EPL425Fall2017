import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class ServerThread implements Runnable {

	private Socket clientSocket;
	private boolean running = true;
	private final Object object = new Object(); // for synchronization
	
	public ServerThread(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		BufferedReader input = null;
		PrintWriter output = null;

		try {
			// get input and output
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			
			long timeReceived = 0, timeSend = 0,total=0;
			int requests = 0,totalCPU=0,totalMem=0;
			while (running) { // do for all requests
				timeSend = new Date().getTime();
				// read incoming stream
				String clientCommand = input.readLine();
				System.out.println(clientCommand);
				
				if (clientCommand == null || clientCommand.equals("Stop")) {
					running = false;
					continue;
				}
				requests++;
				String temp[] = clientCommand.split(" ");

				String userId = temp[3];

				// create random payload
				//int payloadSize = 300*1024+(int)(Math.random()*1701*1024); 

				//byte[] randomBytes = new byte[payloadSize];
				int payloadSize=(int) (Math.random()*2000+300);
				String str = "[" + new Date()+ "]"+ "WELCOME " + this.clientSocket.getInetAddress() + " " + payloadSize + "\n";

				// send payload
				output.write(str);
				output.flush();
				
				// calculate rtt,cpu and memory
				timeReceived = new Date().getTime();
				total += (timeReceived - timeSend);
				totalCPU+=getCPUUtilization();
				totalMem+=getMemoryUtilization();
			}

			synchronized (object) {
				// save calculations
				double div = total / 1000.0;
				MultiThreadedTCPServer.addThroughput((double) requests / div);
				MultiThreadedTCPServer.addCpu(totalCPU/requests);
				MultiThreadedTCPServer.addMemory(totalMem/requests);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// clean up
			try {
				input.close();
				output.close();
				clientSocket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	* get memory utilization 
	*/
	public int getMemoryUtilization(){
		// Get runtime instance
		Runtime runtimeInstance = Runtime.getRuntime();
		// Get total and free memory space in bytes
		long freeMemory = runtimeInstance.freeMemory();
		long totalMemory = runtimeInstance.totalMemory();
		// Calculate used memory space
		long usedMemory = totalMemory - freeMemory;
		// Calculate memory utilization percentage
		double usedMemoryDouble = (double)usedMemory;
		double totalMemoryDouble = (double)totalMemory;
		double utilizationPercentage = (usedMemoryDouble / totalMemoryDouble) * 100;
		int percentageCalculated = (int) Math.floor(utilizationPercentage);
		return percentageCalculated;
	}
	
	/**
	* get cpu utilization
	*/
	public int getCPUUtilization(){
		// System call is executed to get the CPU average load as a
		// percentage represented as a floating point number
		//
		// Unix process to be executed from the system
		Process cpuUtilizationProcess = null;
		String systemCallResult = null;
		try {
			// Process creation - For unix inly
			cpuUtilizationProcess = Runtime.getRuntime().exec("cat /proc/loadavg");
			// Get input stream deriving from the process to read its results
	        BufferedReader stdInput = new BufferedReader(new
	             InputStreamReader(cpuUtilizationProcess.getInputStream()));
	        // Get the result as a string
	        systemCallResult = stdInput.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Get the first double of the result
		String cpuUtilizationString = (systemCallResult.split(" "))[0];
        // Parse the string as a double
        double percentageAsDouble = Double.parseDouble(cpuUtilizationString) * 100;
		// Calculate integer percentage utilization
		int percentageCalculated = (int) Math.floor(percentageAsDouble);
		return percentageCalculated;
	}
	

}
