package kr.kis.nioserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.logging.log4j.core.jmx.Server;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class Client {
	private Abortable abortable = new Abortable();  
    private ClientThread clientThread;  
      
	private static int 		socketPort ;
	private static String 	socketIp;
	private static String 	serverType;
	private static int 		socketTimeout;
	private static String 	encodeType;
	private static int 		threadNum;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;
	public static int threadID = 1;
	String envPath = "";

	
    public Client() {
    	
    }
    
    public Client(String envPath) {
		this.envPath = envPath;
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		util = new ServerInfoUtil(envPath);
		
		log.info("######### TPooledServer() :: envPath : " + envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());    	
    }
    /** 
     *  
     * @param args 
     * @throws Exception 
     */  
    public static void main(String[] args) throws Exception {  
          
        Client client = new Client();  
        client.start("127.0.0.1", 23510);  
          
        Thread.sleep(500);  
  
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
          
        while (true) {  
            String line = reader.readLine();  
              
            if (line.equals("quit"))  
                break;  
              
            try {  
                client.sayToServer(line);  
            } catch (Exception e) {  
                e.printStackTrace();  
                break;  
            }  
              
        }  
          
        client.stop();  
           
        System.out.println("BYE");  
    }  
      
    /** 
     * start client 
     *  
     * @param host 
     * @param port 
     */  
    public void start(String host, int port) {  
          
        abortable.init();  
          
          
        if (clientThread == null || !clientThread.isAlive()) {  
            clientThread = new ClientThread(abortable, host, port);  
//            clientThread = new ClientThread(abortable, host, socketPort);  
            clientThread.start();  
        }  
    }  
      
    /** 
     * stop client 
     */  
    public void stop() {  
          
        abortable.done = true;  
          
        if (clientThread != null && clientThread.isAlive()) {  
            clientThread.interrupt();  
        }  
          
    }  
      
    /** 
     *  
     * @param text 
     * @throws IOException 
     */  
    public void sayToServer(String text) throws IOException {  
        clientThread.sayToServer(text);  
    }  
      
}
