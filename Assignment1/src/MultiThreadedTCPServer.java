

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {

	// variables
	private static ArrayList<Double> throughput;
	private static ArrayList<Integer> cpu;
	private static ArrayList<Integer> memory;

	public static void main(String[] args) throws Exception {
		throughput = new ArrayList<>();
		cpu = new ArrayList<>();
		memory = new ArrayList<>();
		// check params
		if (args.length < 2) {
			throw new Exception("Wrong arguments");
		}

		int port, repetitions;
		// get port
		try {
			port = Integer.parseInt(args[0]);
			repetitions = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			throw new Exception("Argument not a number!");
		}

		// create server listening on given port
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server is running!");
		while (true) { // server always on
			ExecutorService executor = Executors.newCachedThreadPool();
			int reps = 0;
			// accept only given requests
			while (reps < repetitions) {
				reps++;
				Socket socket;
				try {
					socket = serverSocket.accept(); // listen for a new request
				} catch (IOException e) {
					throw new RuntimeException("Error accepting connection", e);
				}
				executor.execute(new ServerThread(socket)); // start new thread
			}
			
			executor.shutdown();
			while (!executor.isTerminated()) {
				// wait for the threads to finish
			}

			double totalT = 0;
			int cpuLoad =0, memU =0;
			// calculate throughput, cpu,memory
			for(int i=0;i<throughput.size();i++){
				totalT += throughput.get(i);
				cpuLoad+=cpu.get(i);
				memU+=memory.get(i);
			}
			// show the results
			System.out.println("Total avg throuput: " + totalT / throughput.size());
			System.out.println("Total cpuLoad: " + cpuLoad / cpu.size());
			System.out.println("Total mem Utilization: " + memU / memory.size());
		}

	}

	/**
	* add throughput
	*/
	public static void addThroughput(double meanThroughput) {
		throughput.add(meanThroughput);
	}

	/**
	* add cpu
	*/
	public static void addCpu(int cput) {
		cpu.add(cput);
	}
	
	/**
	* add memory
	*/
	public static void addMemory(int mem) {
		memory.add(mem);
	}
}
