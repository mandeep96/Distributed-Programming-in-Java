package edu.coursera.distributed;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

            Socket s1 = socket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s1.getInputStream())); 
            PrintWriter out = new PrintWriter(s1.getOutputStream());
            
            String textFromClient = in.readLine();
            boolean getRequest = false;
            String[] split = textFromClient.split(" ");
             
            getRequest = "GET".equals(split[0]) ? true : false;
            String fileContents = getFileContents(fs, split[1]);
                        
            if(getRequest && fileContents!=null){
            	out.write("HTTP/1.0 200 OK\r\n");
            	out.write("\r\n");
            	out.write("\r\n");
            	out.write(fileContents+"\r\n");
            	out.flush();
            }else{
            	out.write("HTTP/1.0 404 Not Found\\r\\n");
            	out.write("Server: FileServer\\r\\n");
            	out.write("\\r\\n");
            	out.flush();
            }
            
            out.close();
            in.close(); 
        }     
        
    }
    
      
	private String getFileContents(final PCDPFilesystem fs, String path) {
		  return fs.readFile(new PCDPPath(path)); 
	}
       

}
