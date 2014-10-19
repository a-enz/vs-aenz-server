package ch.ethz.inf.vs.a2.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ch.ethz.inf.vs.a2.server.R;

public class ServerThread extends Thread{
	
	// The context of the caller
	private Context cont;
	// The socket
	private Socket socket;
	// The streams
	private InputStream ins;
	private OutputStream outs;
	// The Reader
	private BufferedReader reader;
	
	private static final String lineSeperator = "\r\n";
	
	// The sensorListener & its values
	private boolean doneSensing;
	private float [] sensValues;
	private SensorEventListener sensListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			sensValues = event.values;
			doneSensing =true;
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// Nothing
			
		}
	};
	
	public ServerThread(Context callContext, Socket _socket) {
		this.cont = callContext;
		this.socket = _socket;
		
		// tap the streams
		try {
			ins = socket.getInputStream();
			outs = socket.getOutputStream();
		} catch (IOException e) {
			// IO tapping problem
		}
		reader = new BufferedReader(new InputStreamReader(ins));
		
	}
	
	private void sensHandling(SensorManager sensMan, int type) {
		Sensor sens = sensMan.getDefaultSensor(type);
		sensMan.registerListener(sensListener, sens, SensorManager.SENSOR_DELAY_FASTEST);
		doneSensing = false;
		while (!doneSensing) {
			// Wait for sensor
		}
		sensMan.unregisterListener(sensListener);
		//response = getHtml("SensorHTML");
	}
	
	private String getHTML(String name) {
		BufferedReader br = null;
		String everything = "";
		if (name.equals("Sensor")){
			try{
				br = new BufferedReader(new FileReader("sensor"));
			} catch (Exception e) {
				// File not found
			}
		} else {
			// fail
			return "File in reading sample HTML";
		}
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(lineSeperator);
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    } catch (Exception e) {
	    	// IO error
	    }
	    return everything;
	}
	
	public void run() {
		String query = readFromBuffer();
		String response = " ";
		int type = 0;
		
		// Decrypt the request
		
		// Fetching the location in the command
		String httpPath  = query.substring(query.indexOf(" ") + 1);
		httpPath = httpPath.substring(0, httpPath.indexOf(" "));
		
		if (query.startsWith("GET")) {
			// Calling the sensor manager from the original context
			SensorManager sensMan = (SensorManager) cont.getSystemService(Context.SENSOR_SERVICE);
			
			if (httpPath.equals("sensors/")) {
				// Extracting the sensor names
				String sName = query.substring(13);
				sName = sName.substring(0,sName.indexOf(" "));
				// URL formatting
				sName = sName.replace("%20"," ");
				
				// Looking if it is one of the two sensors
				// Thermometer
				if (sName.equals("Thermometer") || sName.equals("thermometer") || sName.equals(sensMan.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE))) {
					type = Sensor.TYPE_AMBIENT_TEMPERATURE;
					sensHandling(sensMan, type);
				}	else if (sName.equals("Light") || sName.equals(sensMan.getDefaultSensor(Sensor.TYPE_LIGHT))) {
					type = Sensor.TYPE_LIGHT;
					sensHandling(sensMan, type);
				} else {
					// Could not handle the GET query
				}
			}
		}
	}

	
	public String readFromBuffer() {
		String tmp, complete;
		complete = "";
		try {
			tmp = reader.readLine();
			complete = tmp;
			while((tmp = reader.readLine()) != null && tmp.length() != 0) {
				complete = complete + tmp + "\r\n";
			}
		} catch (Exception e) {
			// Error in query loading
		}
		return complete;
	}
		

}
