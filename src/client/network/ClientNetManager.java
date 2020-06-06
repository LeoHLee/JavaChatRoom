package network;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;

import View.Alert;
import View.ClientParser;
import View.Control;
import database.AccountInfo;
import javafx.concurrent.Task;

import chatbean.*;
import static chatbean.TypeValue.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import message.OneMessage;
import secure.*;
import chatbean.Parser;

public class ClientNetManager {
	private final String serverIP;
	private final int serverPort;
	private Socket server;
	private int myID;
	private String myPassword;
	private LBLInputStream bgInput;
	private LBLInputStream requestInput;
	private LBLOutputStream requestOutput;
	private boolean backgroundReading = false;
	private boolean requestSocketOpen = false;
	public ClientParser parser = new ClientParser();
	/**
	 * Constructor of this class
	 * @param IP InetAddress of server to connect
	 * @param port Port number of server to connect
	 */
	public ClientNetManager(String IP, int port) {
		serverIP=IP;
		serverPort=port;
	}
	/**
	 * Start reading thread for messages or offline_command
	 */
	private void startReceiver() {
		if(backgroundReading) return;
		backgroundReading=true;
		Task<ChatBean> reader=new Task<>() {
			/**
			 * read until lossing connection
			 * @return null when connection is lost
			 */
			@Override
			protected ChatBean call() {
				try {
					while (true)
						updateValue((ChatBean) bgInput.readObject());
				} catch (IOException e) {
					getOffline();
					return null;
				}
			}
		};
		reader.valueProperty().addListener((observableValue, old, newBean) -> parser.parse(newBean,server));
		new Thread(reader).start();
	}
	/**
	 * Get offline without notifying server
	 */
	public void getOffline() {
		try{
			backgroundReading = false;
			server.close();
		} catch(IOException ignored) {}
	}

	/**
	 * @param obj Object to be send
	 * @return whether successfully sent
	 */
	public boolean send(Object obj){
		try {
			requestOutput.writeObject(obj);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	/**
	 * @param bean request ChatBean to send
	 * @throws Exception threw during sending, encrypting, or parsing
	 * @return server's reply ChatBean to sent request
	 */
	public ChatBean request(ChatBean bean) throws Exception {
		if(!requestSocketOpen) {
			Socket socket=new Socket(serverIP,serverPort);
			SafeStreamPair ssp=new SafeStreamPair(socket);
			requestSocketOpen=true;
			requestOutput=ssp.getOutputStream();
			requestInput=ssp.getInputStream();
		}
		if(bean.type==REQ_LOGIN) {
			Socket tmpSocket=new Socket(serverIP,serverPort);
			SafeStreamPair bg=new SafeStreamPair(tmpSocket);
			ChatBean bindRequest=new ChatBean(REQ_BIND);
			bindRequest.ID=bean.ID;
			bindRequest.password=bean.password;
			bg.getOutputStream().writeObject(bindRequest);
			ChatBean bindResult=(ChatBean)bg.getInputStream().readObject();
			if(bindResult.type==REPLY_OK) {
				server=tmpSocket;
				bgInput=bg.getInputStream();
				myID=bean.ID;
				myPassword=bean.password;
				startReceiver();
			}
			else return bindResult;
		}
		requestOutput.writeObject(bean);
		//Block here until receiving bean
		return (ChatBean)requestInput.readObject();
	}
	/**
	 * @param filePath path of file to send
	 * @param recvID ID of receiver
	 */

	public void sendFile(String filePath,int recvID) {
		((Label) Control.progress.search("hint")).setText("正在传输...");
		Control.progress.show();
		Task<Boolean> fileTask=new Task<Boolean>() {
			@Override
			protected Boolean call() throws IOException {
				Socket socket = null;
				File file=new File(filePath);
				LBLOutputStream outputStream;
				FileInputStream fis = null;
				try {
					ServerSocket serverSocket=new ServerSocket(0);
					ChatBean bean=new ChatBean(REQ_FILE);
					bean.fileName=file.getName();
					bean.fileSize=file.length();
					bean.IPAddress= InetAddress.getLocalHost().getHostAddress().toString();
					bean.portNo=(serverSocket.getLocalPort());
					bean.ID=myID;
					bean.friendID=recvID;
					ChatBean state = request(bean);
					if(!(state.type == REPLY_OK)) {
						serverSocket.close();
						return false;
					}
					//Wait 20s for receiver connection
					serverSocket.setSoTimeout(20000);
					socket=serverSocket.accept();
					serverSocket.close();
					if(socket==null)
						return false;
					//Block for key exchange
					outputStream=new SafeStreamPair(socket).getOutputStream();
					fis = new FileInputStream(file);
					byte[] buffer=new byte[1002];
					int n;
					long sum=0,max=fis.available();
					while((n=fis.read(buffer))!=-1) {
						if(this.isCancelled())
						{
							fis.close();
							socket.close();
							return false;
						}
						outputStream.writeArray(Arrays.copyOfRange(buffer,0,n));
						sum+=n;
						updateProgress(sum,max);
					}
					fis.close();
					socket.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					if(fis != null)
						fis.close();
					if(socket != null)
						socket.close();
					return false;
				}
			}
		};
		((Button) Control.progress.search("cancel")).setOnAction(event -> {
			fileTask.cancel();
		});
		/*This will run in Java FX thread*/
		fileTask.progressProperty().addListener((observableValue, oldValue, newValue) -> {
			/*TODO: react to progress change*/
			if(!fileTask.isCancelled())
				((ProgressBar) Control.progress.search("bar")).setProgress(newValue.doubleValue());
		});
		fileTask.setOnSucceeded(event -> {
			boolean tf = (Boolean) fileTask.getValue();
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
			if(tf)
			{
				try
				{
					Alert alert = new Alert();
					alert.exec(null, "发送成功！");
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Control.chat.alert1.exec(null, "传输失败，请重试");
			}
		});
		fileTask.setOnCancelled(event -> {
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
		});
		fileTask.setOnFailed(event -> {
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
			Control.chat.alert1.exec(Control.chat, "发送失败，请重试");
		});
		new Thread(fileTask).start();
	}
	/**
	 * @param savePath path to save the file to receive
	 * @param fileSize size of the file to receive
	 * @param senderIP IP Address of sender ServerSocket
	 * @param senderPort Port of sender ServerSocket
	 */
	public void saveFile(String savePath,long fileSize,String senderIP,int senderPort) {
		((Label) Control.progress.search("hint")).setText("正在接收...");
		Control.progress.show();
		Task<Boolean> fileTask= new Task<Boolean>() {
			@Override
			protected Boolean call() throws IOException {
				Socket socket = null;
				File file=new File(savePath);
				LBLInputStream inputStream = null;
				FileOutputStream fos = null;
				try {
					socket=new Socket(senderIP,senderPort);
					inputStream=new SafeStreamPair(socket).getInputStream();
					fos = new FileOutputStream(file);
					byte[] buffer;
					long sum=0;
					while(sum < fileSize) {
						if(this.isCancelled())
						{
							fos.close();
							socket.close();
							file.delete();
							return false;
						}
						buffer=inputStream.readArray();
						fos.write(buffer);
						sum = sum + buffer.length;
						updateProgress(sum,fileSize);
					}
					fos.close();
					socket.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					if(fos != null)
						fos.close();
					if(socket != null)
						socket.close();
					file.delete();
					return false;
				}
			}
		};
		((Button) Control.progress.search("cancel")).setOnAction(event -> {
			fileTask.cancel();
		});
		/*This will run in Java FX thread*/
		fileTask.progressProperty().addListener((observableValue, oldValue, newValue) -> {
			/*TODO: react to progress change*/
			if(!fileTask.isCancelled())
				((ProgressBar) Control.progress.search("bar")).setProgress(newValue.doubleValue());
		});
		fileTask.setOnSucceeded(event -> {
			boolean tf = (Boolean) fileTask.getValue();
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
			if(tf)
			{
				try
				{
					Alert alert = new Alert();
					alert.exec(null, "接收成功！");
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Control.chat.alert1.exec(null, "接收失败，请重试");
			}
		});
		fileTask.setOnCancelled(event -> {
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
		});
		fileTask.setOnFailed(event -> {
			((ProgressBar) Control.progress.search("bar")).setProgress(0);
			Control.progress.close();
			Control.chat.alert1.exec(Control.chat, "接收失败，请重试");
		});
		new Thread(fileTask).start();
	}

	public static void main(String[] args) throws Exception {
		ClientNetManager network=new ClientNetManager("127.0.0.1", 13060);
//		ChatBean bean=new ChatBean(REQ_REGISTER);
//		bean.accountInfo=new AccountInfo();
//		bean.accountInfo.NickName="Leo_h";
//		bean.accountInfo.Birthday="19991104";
//		bean.accountInfo.Sex="1";
//		bean.accountInfo.Mail="542806551@qq.com";
//		bean.password="123456#$";
//		ChatBean rep=network.request(bean);
//		System.out.println(rep.type+" "+rep.ID);
		ChatBean bean=new ChatBean(REQ_LOGIN);
		bean.ID=123463;
		bean.password="123456#$";
		ChatBean rep=network.request(bean);
		System.out.println(rep.type+" "+rep.accountInfo.Birthday);

	}
}


