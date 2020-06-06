package serverNet;

import static chatbean.TypeValue.*;
import chatbean.*;
import secure.*;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;

public class ServerNetManager {
	private final ServerSocket listenSocket;
	private final ConcurrentHashMap<Integer,Socket> messageSockets = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer,Socket> requestSockets = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Socket,Integer> bindedIDs = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Socket,LBLOutputStream> outStreams = new ConcurrentHashMap<>();
	private final Executor executor= Executors.newCachedThreadPool();
	public Parser parser;
	public LogOutput log = System.out::println;

	/**
	 * Constructor with port appointing
	 * @param port Appointed port of new server
	 * @throws IOException when failing to create socket with appointed port
	 */
	public ServerNetManager(int port) throws IOException{
		listenSocket = new ServerSocket(port);
	}
	public void start() throws Exception{
		log.write("Server launched");
		while(true) {
			Socket client = listenSocket.accept();
			log.write("New connection: "+client.getInetAddress()+":"+client.getPort());
			try {
				SafeStreamPair ssp = new SafeStreamPair(client);
				log.write("Secure stream ready: " + client.getInetAddress() + ":" + client.getPort());
				outStreams.put(client, ssp.getOutputStream());
				executor.execute(new ClientReader(client, ssp.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public int getPort() {
		return listenSocket.getLocalPort();
	}
	private class ClientReader implements Runnable {
		final private LBLInputStream inputStream;
		final Socket socket;
		ClientReader(Socket socket,LBLInputStream inputStream) {
			this.socket=socket;
			this.inputStream=inputStream;
		}
		public void run() {
			while(true)
			{
				try{
					ChatBean bean=(ChatBean)inputStream.readObject();
					log.write("Receive "+bean.type+" from "+socket.getInetAddress()+":"+socket.getPort());
					switch (bean.type) {
						case REQ_LOGIN:
						case REQ_BIND:
						case REQ_FORGET_PASSWORD:
						case REQ_CHECK_CAPTCHA:
						case REQ_GET_INFO:
							break;
						default:
							bean.ID = bindedIDs.getOrDefault(socket, -12306);
					}
					parser.parse(bean,socket);
				} catch (IOException e) {
					discard(socket);
					//socket closed, stop reading
					return;
				}
			}
		}
	}
	/**
	 * send an ChatBean to assigned client
	 * @param client client socket to send to
	 * @param bean the ChatBean to be sent
	 * @return whether successfully sent
	 */
	public boolean send(Socket client,ChatBean bean)
	{
		LBLOutputStream outputStream=outStreams.get(client);
		if(outputStream==null)
			return false;
		try{
			outputStream.writeObject(bean);
			log.write("Send "+bean.type+" to "+client.getInetAddress()+":"+client.getPort());
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * send an ChatBean to a client with assigned ID
	 * @param id client id to send to
	 * @param bean the ChatBean to be sent
	 * @return whether successfully sent
	 */
	public boolean send(int id,ChatBean bean)
	{
		Socket client=messageSockets.get(id);
		if(client==null) {
			//bad id
			return false;
		}
		return send(client,bean);
	}
	/**
	 * shutdown a connection
	 * @param socket socket to close
	 */
	public void discard(Socket socket) {
		if (bindedIDs.containsKey(socket)) {
			int id = bindedIDs.get(socket);
			bindedIDs.remove(socket);
			if(requestSockets.get(id)==socket) {
				requestSockets.remove(id);
				log.write(id+" cancels authority of "+socket.getInetAddress()+":"+socket.getPort());
			}
			if(messageSockets.get(id)==socket) {
				messageSockets.remove(id);
				log.write(id+" cancels bind with "+socket.getInetAddress()+":"+socket.getPort());
			}
		}
		if (outStreams.containsKey(socket)) {
			outStreams.remove(socket);
			log.write("Close connection with " + socket.getInetAddress() + ":" + socket.getPort());
		}
	}
	public void authorize(int id,Socket socket) {
		log.write(id+" authorizes "+socket.getInetAddress()+":"+socket.getPort());
		if(requestSockets.containsKey(id)) {
			Socket oldSocket=requestSockets.get(id);
			requestSockets.remove(id);
			bindedIDs.remove(oldSocket);
		}
		requestSockets.put(id,socket);
		bindedIDs.put(socket,id);
	}
	public void bind(int id,Socket socket) {
		log.write(id+" binds "+socket.getInetAddress()+":"+socket.getPort());
		if(messageSockets.containsKey(id)) {
			Socket oldSocket=messageSockets.get(id);
			send(oldSocket,new ChatBean(RECV_OFFLINE));
			messageSockets.remove(id);
			bindedIDs.remove(oldSocket);
		}
		messageSockets.put(id,socket);
		bindedIDs.put(socket,id);
	}
	public boolean isOnline(int id) {
		return messageSockets.containsKey(id);
	}
}


