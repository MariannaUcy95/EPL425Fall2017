import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {
	private static final int USERS = 10; // number of  users
	private static double[] RTT;
	
	public static void main(String[] args) throws Exception {
		ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);
		// check arguments
		if (args.length < 2) {
			throw new Exception("Wrong arguments");
		}

		RTT = new double[USERS];

		int port = Integer.parseInt(args[1]);
		String IPAddress = args[0];
		/*try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			throw new Exception("Argument not a number!");
		}*/

		// create concurrent users
		for (int i = 0; i < USERS; i++) {
			try {
				TCP_WORKER_SERVICE.execute(new ClientThread(IPAddress, port, i));
			} catch (UnknownHostException e) {
				throw new Exception("Unknown host!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		TCP_WORKER_SERVICE.shutdown();
		while (!TCP_WORKER_SERVICE.isTerminated()) {
			// wait for the threads to finish
		}

		// calculate total RTT
		double totalRTT = 0;
		for (int i = 0; i < USERS; i++) {
			totalRTT += RTT[i];
		}

		System.out.println("Total Mean RTT: " + totalRTT / USERS);
	}

	/**
	* Set RTT to the array
	*/
	public static void setRTT(int id, double meanRtt) {
		RTT[id] = meanRtt;
	}
}
