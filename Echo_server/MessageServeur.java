import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress; 
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageServeur {
	private static final int PORT = 10001;
	private static final int THREAD_CNT = 100;
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_CNT);
	public static void main(String[] args) {

		try {
			
			System.out.println("Start server :)");
			ServerSocket serverSocket = new ServerSocket(PORT);

			while(true){
				
				Socket socket = serverSocket.accept();
				try{
					threadPool.execute(new ConnectionWrap(socket));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ConnectionWrap implements Runnable{

	private Socket socket = null;
	private Boolean messageReceived= false;
	public ConnectionWrap(Socket socket) {
		this.socket = socket;
	}

	public void waitProcessor(int msecond){
		try{
			Thread.sleep(msecond);
		}catch(InterruptedException e){
		}
	}
	Runtime runtime = Runtime.getRuntime();
	@Override
	public void run(){

		try {
			
			

            InetAddress  inetaddr = socket.getInetAddress();
            InputStream in = socket.getInputStream();
			
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String question = null;
			
			String date =new String(new Date().toString().getBytes());
			
			String adr=inetaddr.getHostAddress();
			
			String dir="clientDB";
			
			StringBuilder identifient = new StringBuilder();
			identifient.append("");
			identifient.append(adr.hashCode());
			
			StringBuilder path1 = new StringBuilder();
			path1.append(dir+"\\");
			path1.append(adr.hashCode());
			StringBuilder path2 = new StringBuilder();
			path2.append(dir+"/");
			path2.append(adr.hashCode());
			
			
			
            System.out.println(date);
            System.out.println("client : "+inetaddr.getHostAddress());
			while(((question = br.readLine()) != null)){
	        System.out.println("Message received : " + question);
			break;		
    		}
			
			
			File clientdir = new File(path2.toString());
			clientdir.mkdirs();
			
			PrintWriter out = new PrintWriter(path2.toString()+"/question.txt");
			out.println(question+"\n");
			out.close();
    
			PrintWriter batfile = new PrintWriter(path2.toString()+"/killJava.bat");
			batfile.println("timeout 4 & taskkill /IM "+identifient.toString()+".exe /F");
			batfile.close();
			
			PrintWriter batfile2 = new PrintWriter(path2.toString()+"/genAnswer.bat");
			batfile2.println("copy exe\\javatmp.exe "+path1.toString()+"\\"+identifient.toString()+".exe");
			batfile2.println("copy exe\\parsing.exe "+path1.toString()+"\\parsing.exe");
			batfile2.println(path1.toString()+"\\killJava.bat | "+path1.toString()+"\\"+identifient.toString()+".exe -cp out/production/Ab Main bot=super action=chat trace=false 1> "+path1.toString()+"\\reponsetmp.txt 0 < "+path1.toString()+"\\question.txt");
			batfile2.println(path1.toString()+"\\parsing.exe "+ path2.toString()+" & exit");
			batfile2.close();
    
			
			Process p1 = runtime.exec("cmd /c start "+path1.toString()+"\\genAnswer.bat");   
			
			waitProcessor(5500);
			
			OutputStream stream = socket.getOutputStream();
			Files f;
			Path path =Paths.get(path2.toString()+"/reponse.txt");
			byte[] bytes =Files.readAllBytes(path);
			
			stream.write(bytes);
			String reponse =new String(bytes);
			System.out.println("return message : "+reponse);		
			PrintWriter log = new PrintWriter(new BufferedWriter(new FileWriter("logfile.txt", true)));
			
			log.println(date);
			log.println(adr);
			log.println("Q : "+question);
			log.println("A : "+reponse);
			log.close();
			

			System.out.println("Waiting client...");	
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}