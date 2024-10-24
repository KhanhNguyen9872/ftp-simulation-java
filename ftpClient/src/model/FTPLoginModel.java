package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import lib.FTPConfig;
import java.util.StringTokenizer;

public class FTPLoginModel {
	private Socket socket;
	private OutputStream send;
	private BufferedReader recv;
	
	public FTPLoginModel() {
		
	}
	
	public Socket connect(FTPConfig config) throws Exception {
		String ipAddress = config.getIpAddress();
		int port = Integer.parseInt(config.getPort());
		String username = config.getUsername();
		String password = config.getPassword();
		
        this.socket = new Socket(ipAddress, port);
        this.send = getSend(socket);
        this.recv = getRecv(socket);
        
        this.ready(socket);
        this.auth(socket, username, password);
        
        return socket;
	}
	
	private void ready(Socket socket) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(readLine());
		String code = tokenizer.nextToken();
		
		System.out.println();
		
		if (!code.equals("220")) {
			throw new Exception("FTPServer isn't ready!");
		};
	};
	
	private void auth(Socket socket, String username, String password) throws Exception {
		String code;
		String result;
		StringTokenizer tokenizer;
		
		
		// username
		write("USER " + username);
		result = readLine();
		tokenizer = new StringTokenizer(result);
		code = tokenizer.nextToken();
		
		if (!code.equals("331")) {
			throw new Exception(result);
		};
		
		// password
		write("PASS " + password);
		result = readLine();
		tokenizer = new StringTokenizer(result);
		code = tokenizer.nextToken();
		
		if (!code.equals("230")) {
			throw new Exception(result);
		};
	};
	
	private void write(String cmd) throws Exception {
		send.write(cmd.getBytes());
		send.flush();
		send.write('\n');
		send.flush();
	};
	
	private String readLine() throws Exception {
		return this.recv.readLine();
	};
	
//	private byte readByte() {
//		this.recv.rea
//	}
	
	private OutputStream getSend(Socket sock) throws Exception {
		return sock.getOutputStream();
	};
	
	private BufferedReader getRecv(Socket sock) throws Exception {
		return new BufferedReader(new InputStreamReader(sock.getInputStream()));
	};
}
