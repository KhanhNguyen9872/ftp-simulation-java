package model;

import java.util.StringTokenizer;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FTPMainModel {
	private OutputStream send;
	private BufferedReader recv;
	private Socket sock;
	
	public FTPMainModel() {
		
	};
	
	public void setSocket(Socket sock) {
		this.sock = sock;
		try {
			this.send = this.getSend();
			this.recv = this.getRecv();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	};
	
	public String getCurrentPath() throws Exception {
		write("PWD");
		
		String str = readLine();
		StringTokenizer tokenizer = new StringTokenizer(str);
		String code = tokenizer.nextToken();
		
		if (code.equals("257")) {
			String path = tokenizer.nextToken();
			return path;
		} else {
			throw new Exception(str);
		}
	};
	
	public List<String> getListFile() throws Exception {
		String result;
		String code;
		List<String> listFile = new ArrayList<>();
		
		write("NIST");
		result = readLine();
		StringTokenizer tokenizer = new StringTokenizer(result);
		code = tokenizer.nextToken();
		
		if (code.equals("550")) {
			throw new Exception(result);
		};
		
		if (code.equals("125")) {
			// ok
			String name;
			while ((name = readLine()) != null) {
				tokenizer = new StringTokenizer(name);
                if (tokenizer.nextToken().equals("226")) {
                	break;
                };
                
                listFile.add(name);
            };
		} else {
			throw new Exception("Unknown status code!");
		};
		
		return listFile;
	};
	
	private void write(String cmd) throws Exception {
		send.write(cmd.getBytes());
		send.flush();
		send.write('\n');
		send.flush();
	};
	
	private String readLine() throws Exception {
		return recv.readLine();
	};
	
	private OutputStream getSend() throws Exception {
		return this.sock.getOutputStream();
	};
	
	private BufferedReader getRecv() throws Exception {
		return new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
	};
}
