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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FTPMainModel {
	private String homeLocalPath;
	private String homeRemotePath;
	private String localPath = null;
	private String remotePath = null;
	private OutputStream send;
	private BufferedReader recv;
	private Socket sock;
	
	public FTPMainModel(String homeLocalPath) {
		this.homeLocalPath = homeLocalPath;
	};
	
	public void setSocket(Socket sock) {
		this.sock = sock;
		try {
			this.send = this.getSend();
			this.recv = this.getRecv();
			
			this.homeRemotePath = this.getCurrentRemotePath();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	};
	
	public void setLocalPath(String path) {
		this.localPath = path;
		this.homeLocalPath = path;
	}
	
	public String getCurrentLocalPath() {
		if (localPath == null) {
			this.localPath = homeLocalPath;
		};
		return this.localPath;
	}
	
	public String getCurrentRemotePath() throws Exception {
		write("PWD");
		
		String str = readLine();
		StringTokenizer tokenizer = str2token(str);
		String code = tokenizer.nextToken();
		
		if (code.equals("257")) {
			String path = tokenizer.nextToken();
			if (path == null || path.isEmpty()) {
				throw new Exception("Path is null or empty!");
			}
			this.remotePath = path.substring(1, path.length() - 1);
		} else {
			throw new Exception(str);
		}

		return this.remotePath;
	};
	
	public Map<String, Boolean> getListLocalFile() throws Exception {
		Map<String, Boolean> listFile = new LinkedHashMap<>();
		
		if (!this.homeLocalPath.equals(this.localPath)) {
			listFile.put("..", false);
		}
		
		try (Stream<Path> paths = Files.list(Paths.get(this.localPath))) {
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
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("550")) {
			throw new Exception(result);
		};
		
		if (code.equals("125")) {
			// ok
			Boolean isFile;
			String name;
			if (!this.homeRemotePath.equals(this.remotePath)) {
				listFile.put("..", false);
			}
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
	
	private StringTokenizer str2token(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str);
		return tokenizer;
	}

	public boolean mkdirLocal(String folderName) {
		if (folderName == null || folderName.isEmpty()) {
			return false;
		}
		
		File directory = new File(this.localPath + "/" + folderName);
		
		if (directory.mkdir()) {
			return true;
		}
		return false;
	}
	
	public boolean mkdirRemote(String folderName) throws Exception {
		if (folderName == null || folderName.isEmpty()) {
			return false;
		}
		
		String result, code;
		write("MKD \"" + folderName + "\"");
		
		result = readLine();
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("257")) {
			return true;
		} else if (code.equals("550")) {
			return false;
		}
		
		throw new Exception(result);
	}
	
	public boolean deleteFolderLocal(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		File file = new File(this.localPath + "/" + name);
		
		if (file.delete()) {
			return true;
		}
		
		return false;
	}
	
	public boolean deleteFileLocal(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		File file = new File(this.localPath + "/" + name);
		
		if (file.delete()) {
			return true;
		}
		
		return false;
	}

	public boolean deleteFolderRemote(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		String result, code;
		write("DELE \"" + name + "\"");
		
		result = readLine();
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("250")) {
			return true;
		} else if (code.equals("550")) {
			return false;
		}
		
		throw new Exception(result);
	}
	
	public boolean deleteFileRemote(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		String result, code;
		write("RMD \"" + name + "\"");
		
		result = readLine();
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("250")) {
			return true;
		} else if (code.equals("550")) {
			return false;
		}
		
		throw new Exception(result);
	}
	
	public boolean chdirLocal(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		Path path = Paths.get(this.localPath + "/" + name);
        Path realPath;

        try {
            realPath = path.normalize().toRealPath();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String newPath = realPath.toString();
        
        File f = new File(newPath);
        
        if (f.exists() && f.isDirectory() && (newPath.length() >= this.homeLocalPath.length())) {
        	this.localPath = newPath;
        	return true;
        }
		
		return false;
	}

	public boolean chdirRemote(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		String result, code;
		
		write("CWD \"" + name + "\"");
		
		result = readLine();
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("250")) {
			return true;
		} else if (code.equals("550")) {
			return false;
		}
		
		throw new Exception(result);
	}

	public boolean renameRemote(String oldName, String newName) throws Exception {
		if (oldName == null || newName == null || oldName.isEmpty() || newName.isEmpty()) {
			return false;
		}
		
		String result, code;
		
		write("RNFR  \"" + oldName + "\"");
		write("RNTO  \"" + newName + "\"");
		
		result = readLine();
		StringTokenizer tokenizer = str2token(result);
		code = tokenizer.nextToken();
		
		if (code.equals("250")) {
			return true;
		} else if (code.equals("550")) {
			return false;
		}
		
		throw new Exception(result);
	}

	public boolean renameLocal(String oldName, String newName) {
		if (oldName == null || newName == null || oldName.isEmpty() || newName.isEmpty()) {
			return false;
		}
		
		Path oldPath = Paths.get(this.localPath + "/" + oldName);
        Path newPath = Paths.get(this.localPath + "/" + newName);

        try {
            // Rename the file/folder
            Files.move(oldPath, newPath);
            return true;
        } catch (IOException e) {
            
        }
        return false;
	}
}
