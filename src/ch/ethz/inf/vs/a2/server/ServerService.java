package ch.ethz.inf.vs.a2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

public class ServerService {

	private static ServerSocket serverSocket;
	private final static int nThreads = 8;
	private final static int port = 8081;
	
	
	public static  void start(Context callerContext) {
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		try {
			// Set up the Serversocket 
			serverSocket = new ServerSocket(port);
			// Set a response timeout
			serverSocket.setSoTimeout(3000);
		} catch (Exception e) {
			// Somehow i need to throw this exception
		}
		
		while(true) {
			try {
				Socket localSocket = serverSocket.accept();
				Runnable worker = new ServerThread(callerContext, localSocket);
			} catch (Exception e) {
				// There was an issue here
			}
		}
		
	}
	
}
