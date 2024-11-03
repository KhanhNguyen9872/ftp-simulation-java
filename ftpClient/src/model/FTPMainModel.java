package model;

import java.util.StringTokenizer;
import java.util.stream.Stream;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FTPMainModel {
	private byte[] eof = ByteBuffer.allocate(4).putInt(0xCAFEBABE).array();
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
			
			this.homeRemotePath = this.getCurrentRemotePath(false);
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
	
	private String getCurrentRemotePath(boolean hideRealPath) throws Exception {
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
		
		if (!hideRealPath) {
			return this.remotePath;
		}
		
		String tmp = this.remotePath.replace(this.homeRemotePath, "/");
		if (!tmp.equals("/")) {
			tmp = tmp.substring(1, tmp.length());
		}
		tmp = tmp.replace("\\", "/");
		
		return tmp;
	}
	
	public String getCurrentRemotePath() throws Exception {
		return this.getCurrentRemotePath(true);
	};
	
	public Map<String, Boolean> getListLocalFile() throws Exception {
		Map<String, Boolean> listFile = new LinkedHashMap<>();
		
		if (!this.homeLocalPath.equals(this.localPath)) {
			listFile.put("..", false);
		}
		
		try (Stream<Path> paths = Files.list(Paths.get(this.localPath)).sorted()) {
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
		int code;
		
		Map<String, Boolean> listFile = new LinkedHashMap<>();
		
		write("NIST");
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 550) {
			throw new Exception(result);
		};
		
		if (code == 125) {
			// ok
			Boolean isFile;
			String name;
			if (!this.homeRemotePath.equals(this.remotePath)) {
				listFile.put("..", false);
			}
			while ((name = readLine()) != null) {
				code = getStatusCode(name);
                if (code == 226) {
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
		
		String result;
		int code;
		
		write("MKD \"" + folderName + "\"");
		
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 257) {
			return true;
		} else if (code == 550) {
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
		
		String result;
		int code;
		write("DELE \"" + name + "\"");
		
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 250) {
			return true;
		} else if (code == 550) {
			return false;
		}
		
		throw new Exception(result);
	}
	
	public boolean deleteFileRemote(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		String result;
		int code;
		
		write("RMD \"" + name + "\"");
		
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 250) {
			return true;
		} else if (code == 550) {
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
		
		String result;
		int code;
		
		write("CWD \"" + name + "\"");
		
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 250) {
			return true;
		} else if (code == 550) {
			return false;
		}
		
		throw new Exception(result);
	}

	public boolean renameRemote(String oldName, String newName) throws Exception {
		if (oldName == null || newName == null || oldName.isEmpty() || newName.isEmpty()) {
			return false;
		}
		
		String result;
		int code;
		
		write("RNFR  \"" + oldName + "\"");
		write("RNTO  \"" + newName + "\"");
		
		result = readLine();
		code = getStatusCode(result);
		
		if (code == 250) {
			return true;
		} else if (code == 550) {
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
            e.printStackTrace();
        }
        return false;
	}
	
	public boolean sendFile(String fileName) throws Exception {
		try {
			write("STOR \"" + fileName + "\"");
			
			String result;
			int code;
			
			result = readLine();
			code = getStatusCode(result);
			
			if (code == 150) {
				sendFileToServer(this.localPath + "/" + fileName);
				result = readLine();
				code = getStatusCode(result);
				
				if (code != 226) {
					return false;
				}
				
			} else if (code == 553) {
				throw new Exception("Cannot send [" + fileName + "] to REMOTE");
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private int getStatusCode(String data) {
		int code;
		StringTokenizer tokenizer = str2token(data);
		try {
			code = Integer.parseInt(tokenizer.nextToken());
		} catch (Exception ex) {
//			ex.printStackTrace();
			return -1;
		}
		return code;
	}
	
	private void sendFileToServer(String filePath) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(filePath);
		
		byte[] buffer = new byte[65536];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            this.send.write(buffer, 0, bytesRead);
            this.send.flush();
        }
        
        this.send.write(eof);
        this.send.flush();
        
        fileInputStream.close();
	}
	
	private void receiveFileToServer(String filePath) throws Exception {		
		FileOutputStream fileOutputStream = new FileOutputStream(filePath, false);
		
		byte[] buffer = new byte[65536];
        int bytesRead;
        
        InputStream inputStream = this.sock.getInputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            if (bytesRead >= 4 && 
                buffer[bytesRead - 4] == eof[0] &&
                buffer[bytesRead - 3] == eof[1] &&
                buffer[bytesRead - 2] == eof[2] &&
                buffer[bytesRead - 1] == eof[3]) {

            	fileOutputStream.write(buffer, 0, bytesRead - 4);
                break;
            } else {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
        
        fileOutputStream.close();
        
        write("226");
	}

	public boolean receiveFile(String fileName) {
		try {
			write("RETR \"" + fileName + "\"");
			
			String result;
			int code;
			
			result = readLine();
			code = getStatusCode(result);
			
			if (code == 150) {
				receiveFileToServer(this.localPath + "/" + fileName);
				result = readLine();
				code = getStatusCode(result);
				
				if (code != 226) {
					return false;
				}
				
			} else if (code == 550) {
				throw new Exception("Cannot receive [" + fileName + "] from REMOTE (File not found)");
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;

	}

	public boolean receiveFolder(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean sendFolder(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getLastModifiedTimeFileRemote(String fileName) throws Exception {
		write("MDTM \"" + fileName + "\"");
		
		String result;
		int code;
		
		result = readLine();
		StringTokenizer tokenizer = new StringTokenizer(result);
		code = Integer.parseInt(tokenizer.nextToken());
		
		if (code == 213) {
			return tokenizer.nextToken();
		} else if (code == 550) {
			throw new Exception("File or folder not found");
		}
		
		throw new Exception(result);
	}

	public String getSizeFileRemote(String fileName) throws Exception {
		write("SIZE \"" + fileName + "\"");
		
		String result;
		int code;
		
		result = readLine();
		StringTokenizer tokenizer = new StringTokenizer(result);
		code = Integer.parseInt(tokenizer.nextToken());
		
		if (code == 213) {
			return tokenizer.nextToken();
		} else if (code == 550) {
			throw new Exception("File or folder not found");
		}
		
		throw new Exception(result);
	}
	
	public String timestampToDateTime(long timestamp) {
		// Convert timestamp to LocalDateTime
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDateTime();

        // Format the LocalDateTime to a readable string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
	}
	
	public String convertBytes(long bytes) {
        if (bytes < 0) {
            return "Invalid size"; // Handle negative sizes if necessary
        }
        
        if (bytes == 0) {
            return "0 Bytes";
        }

        String[] units = {"Bytes", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        
        double size = (double) bytes;
        
        // Keep dividing by 1024 until the size is less than 1024
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        // Format the result to two decimal places
        return String.format("%.2f %s", size, units[unitIndex]);
    }

	public long getSizeFileLocal(String fileName) {
		File file = new File(this.localPath + "/" + fileName);
        
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
        	return -1;
        }
	}

	public long getLastModifiedTimeFileLocal(String fileName) {
		File file = new File(this.localPath + "/" + fileName);
        
        if (file.exists()) {
            return file.lastModified();
        } else {
        	return -1;
        }
	}
}
