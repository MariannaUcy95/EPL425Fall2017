import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.Date;

public class ClientThread implements Runnable {
	private final int REQUESTS = 300; // number of request per user
	private final Object object = new Object(); // synchronization
	
	private Socket socket;
	private int ID;  //ID of each user

	public ClientThread(String IPAddress, int port, int ID) throws UnknownHostException, IOException {
		socket = new Socket(IPAddress, port); // connect to socket for each user through the port
		this.ID = ID;
	}

	@Override
	public void run() {
		PrintWriter output = null;
		BufferedReader input = null;

		try {
			// get input and output
			output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			long timeSend = 0, timeReceived = 0, RTT=0;
			// send all the requests
			for (int i = 0; i < REQUESTS; i++) {
				timeSend = new Date().getTime();
				
				output.write("HELLO " + socket.getInetAddress().getHostAddress() + " " + socket.getPort() + " " + ID + "\n");
				output.flush();

				String line = input.readLine();
				System.out.println(line);
				timeReceived = new Date().getTime();
				RTT += timeReceived-timeSend; // count RTT
			}
			
			output.write("Stop\n"); // send stop command
			output.flush();
			
			synchronized (object) {
				// save RTT time
				TCPClient.setRTT(ID, (double)RTT/(double)REQUESTS);
				System.out.println("Mean RTT: "+(double)RTT/REQUESTS);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Clean up
			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
