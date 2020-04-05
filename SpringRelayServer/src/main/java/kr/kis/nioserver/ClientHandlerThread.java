package kr.kis.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class ClientHandlerThread extends Thread {

	private Abortable abortable;
	private SocketChannel client;
	
	private String envPath;
	private static int 		socketPort ;
	private static String 	socketIp;
	private static String 	serverType;
	private static int 		socketTimeout;
	private static String 	encodeType;
	private static int 		threadNum;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;
	
	public ClientHandlerThread(Abortable abortable, SocketChannel client) {
		this.abortable = abortable;
		this.client = client;
	}
	
	public ClientHandlerThread(Abortable abortable, SocketChannel client, String envPath) {
		this.abortable = abortable;
		this.client = client;
		
		this.envPath = envPath;
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		util = new ServerInfoUtil(envPath);
		
		log.info("######### ServerThread() :: envPath : " + envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());				
	}
	
	@Override
	public void run() {
		super.run();
		
		Selector selector = null;
		
		Charset cs = Charset.forName(encodeType);
		
		boolean done = false;
		
		try {
			log.info("client :: started");
			
			
			 client.configureBlocking(false);  
             selector = Selector.open();  
               
             client.register(selector, SelectionKey.OP_READ);  
               
             // send welcome message  
             client.write(ByteBuffer.wrap(new String("Welcome").getBytes()));  
               
             ByteBuffer buffer = ByteBuffer.allocate(1024);  
               
             while (!Thread.interrupted() && !abortable.isDone() && !done) {  
                 selector.select(3000);  
                   
                 Iterator<SelectionKey> iter = selector.selectedKeys().iterator();  
                 while (!abortable.isDone() && iter.hasNext() && !done) {  
                     SelectionKey key = iter.next();  
                     if (key.isReadable()) {  
                         int len = client.read(buffer);  
                         if (len < 0) {  
                             done = true;  
                             break;  
                         } else if (len == 0) {  
                             continue;  
                         }  
                         buffer.flip();  
                           
                         CharBuffer cb = cs.decode(buffer);  
                           
                         System.out.printf("From Client : ");  
                         while (cb.hasRemaining()) {  
                             System.out.printf("%c", cb.get());  
                         }  
                         System.out.println();  
                           
                         buffer.compact();  
                     }  
                 }  
             }  
               
         } catch (Exception e) {  
             e.printStackTrace();  
         } finally {  
               
             if (client != null) {  
                 try {  
                     client.socket().close();  
                     client.close();  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
             }  
               
             System.out.println("Client :: bye");  
         }  
	}
}
