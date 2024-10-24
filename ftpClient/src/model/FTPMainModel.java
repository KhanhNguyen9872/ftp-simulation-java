package model;

import java.util.StringTokenizer;
import java.util.stream.Stream;

import javafx.scene.control.Label;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
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
			if (path == null || path.isEmpty()) {
				throw new Exception("Path is null or empty!");
			}
			return path.substring(1, path.length() - 1);
		} else {
			throw new Exception(str);
		}
	};
	
	public Map<String, Boolean> getListLocalFile(String localPath) throws Exception {
		Map<String, Boolean> listFile = new LinkedHashMap<>();
		
		try (Stream<Path> paths = Files.list(Paths.get(localPath))) {
            paths.forEach(path -> {
            	String fileName = path.getFileName().toString();
            	Boolean isFile = Files.isRegularFile(path);
            	listFile.put(fileName, isFile);
            });
        } catch (IOException e) {
        	e.printStackTrace();
        };
        
        return listFile;
	}
	
	public Map<String, Boolean> getListRemoteFile() throws Exception {
		String result;
		String code;
		Map<String, Boolean> listFile = new LinkedHashMap<>();
		
		write("NIST");
		result = readLine();
		StringTokenizer tokenizer = new StringTokenizer(result);
		code = tokenizer.nextToken();
		
		if (code.equals("550")) {
			throw new Exception(result);
		};
		
		if (code.equals("125")) {
			// ok
			Boolean isFile;
			String name;
			while ((name = readLine()) != null) {
				tokenizer = new StringTokenizer(name);
                if (tokenizer.nextToken().equals("226")) {
                	break;
                };
                
                isFile = (name.charAt(0) == 'f' ? true : false);
                name = name.substring(2, name.length() - 1);
                listFile.put(name, isFile);
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
	}

	public void mkdirLocal(String folderName) {
		
	};
}
