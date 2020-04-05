package kr.kis.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class ClientThread extends Thread  {
	private Abortable abortable;  
    private String host;  
    private int port;  
    private SocketChannel client;  
      
    /** 
     *  
     * @param abortable 
     * @param host 
     * @param port 
     */  
    public ClientThread(Abortable abortable, String host, int port) {  
        this.abortable = abortable;  
        this.host = host;  
        this.port = port;  
    }  

    /** 
     *  
     * @param text 
     * @throws IOException  
     */  
    public void sayToServer(String text) throws IOException {  
        int len = client.write(ByteBuffer.wrap(text.getBytes()));  
        System.out.printf("[write :: text : %s / len : %d]\n", text, len);  
    }  

    @Override  
    public void run() {  
        super.run();  
          
        boolean done = false;  
        Selector selector = null;  
        Charset cs = Charset.forName("UTF-8");  
          
        try {  
              
            System.out.println("Client :: started");  
              
            client = SocketChannel.open();  
            client.configureBlocking(false);  
            client.connect(new InetSocketAddress(host, port));  
              
            selector = Selector.open();  
            client.register(selector, SelectionKey.OP_READ);  
              
            while (!Thread.interrupted() && !abortable.isDone() && !client.finishConnect()) {  
                Thread.sleep(10);  
            }  
              
            System.out.println("Client :: connected");  
              
            ByteBuffer buffer = ByteBuffer.allocate(1024);  
              
            while (!Thread.interrupted() && !abortable.isDone() && !done) {  
                  
                selector.select(3000);  
                  
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();  
                while (!Thread.interrupted() && !abortable.isDone() && !done && iter.hasNext()) {  
                    SelectionKey key = iter.next();  
                    if (key.isReadable()) {  
                        int len = client.read(buffer);  
                        if (len < 0) {  
                            System.out.println("Client :: server closed");  
                            done = true;  
                            break;  
                        } else if (len == 0) {  
                            continue;  
                        }  
                        buffer.flip();  
                          
                        CharBuffer cb = cs.decode(buffer);  
                          
                        System.out.printf("From Server : ");  
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
              
            System.out.println("Client :: done");  
        }  
    }
}
