package com.ftpsimulation.server.model;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Arrays;

class FTPClientHandler implements Runnable {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private byte[] eof = ByteBuffer.allocate(4).putInt(0xCAFEBABE).array();
    private FTPServerDatabase ftpServerDatabase;
    private int isLoggedIn = -1;
    private String Rnfr = null;
    private Socket clientSocket;
    private String username;
    private String rootPath;
    private String curPath;
    private BufferedReader in;
    private PrintWriter out;
    private String ipPortClient;
    private boolean isClosed = false;

    public FTPClientHandler(Socket clientSocket, String rootPath, FTPServerDatabase ftpServerDatabase) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.rootPath = rootPath;
        this.ipPortClient = clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        this.curPath = this.rootPath;
        this.ftpServerDatabase = ftpServerDatabase;
    }

    @Override
    public void run() {
        try {
            sendResponse("220 FTP Server Ready");

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[" + ipPortClient + "] (" + getCurrentDateTime() + ") Received: '" + line + "'");
                handleCommand(line);
                if (isClosed) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCommand(String command) {
        StringTokenizer tokenizer = new StringTokenizer(command);
        String cmd = tokenizer.nextToken().toUpperCase();
        String args = "";

        String[] tmp = command.split(" ");
        for (int i = 1; i < tmp.length; i++) {
            args = args + tmp[i] + " ";
        };

        args = args.trim();

        switch (cmd) {
            case "USER":
                handleUser(tokenizer);
                return;
            case "PASS":
                handlePass(tokenizer);
                return;
        }

        if (this.isLoggedIn != 1) {
            sendResponse("530 Not logged in.");
            return;
        }

        switch (cmd) {
            case "MKD":
                handleMkd(tokenizer);
                break;
            case "DELE":
                handleDele(tokenizer);
                break;
            case "RMD":
                handleRmd(tokenizer);
                break;
            case "RNFR":
                handleRnfr(tokenizer);
                break;
            case "RNTO":
                handleRnto(tokenizer);
                break;
            case "LIST":
            case "NIST":
                handleList(args);
                break;
            case "RETR":
                handleRetr(tokenizer);
                break;
            case "STOR":
                handleStor(tokenizer);
                break;
            case "CWD":
                handleCwd(tokenizer);
                break;
            case "PWD":
            case "XPWD":
                handlePwd();
                break;
            case "SIZE":
                handleSize(tokenizer);
                break;
            case "MDTM":
                handleMdtm(tokenizer);
                break;
            case "QUIT":
                handleQuit();
                break;
            default:
                sendResponse("502 Command not implemented");
        }
    }

    private String getCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();

        return currentDateTime.format(formatter);
	}

    private void handleSize(StringTokenizer tokenizer) {
        String fileName = tokenizer.nextToken();

        if (fileName == null || fileName.isEmpty()) {
            sendResponse("500 Syntax error.");
            return;
        }

        fileName = getName(fileName);

        File file = new File(this.curPath + "/" + fileName);
        
        if (file.exists() && file.isFile()) {
            long fileSizeInBytes = file.length();
            sendResponse("213 " + fileSizeInBytes);
        } else {
            sendResponse("550 No such file or directory.");
        }
    }

    private void handleMdtm(StringTokenizer tokenizer) {
        String fileName = tokenizer.nextToken();

        if (fileName == null || fileName.isEmpty()) {
            sendResponse("500 Syntax error.");
            return;
        }

        fileName = getName(fileName);

        File file = new File(this.curPath + "/" + fileName);
        
        if (file.exists()) {
            long lastModifiedMillis = file.lastModified();
            sendResponse("213 " + lastModifiedMillis);
        } else {
            sendResponse("550 No such file or directory.");
        }
    }

    private Map<String, Boolean> nlstHelper(String args) {
        // Construct the name of the directory to list.
        String filename = this.curPath;
        if (args != null) {
          filename = filename + "\\" + args;
        }
    
        // Now get a File object, and see if the name we got exists and is a
        // directory.
        File f = new File(filename);
        Map<String, Boolean> listFile = new LinkedHashMap<>(); 
    
        if (f.exists()) {
            if (f.isDirectory()) {
                List<String> arrayList = Arrays.asList(f.list());
                Collections.sort(arrayList, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        // Compare the first character of each string
                        return Character.compare(s1.charAt(0), s2.charAt(0));
                    }
                });

                for (String string: arrayList) {
                    File currentFile = new File(f, string);
                    listFile.put(string, currentFile.isFile());
                }
            } 

            if (f.isFile()) {
                listFile.put(f.getName(), true);
            }
        } else {
            listFile = null;
        }

        return listFile;
    }

    private void handleRnfr(StringTokenizer tokenizer) {
        String fromName = tokenizer.nextToken();
        if (fromName == null || fromName.isEmpty()) {
            return;
        }

        this.Rnfr = getName(fromName);
    }

    private void handleRnto(StringTokenizer tokenizer) {
        String toName = tokenizer.nextToken();

        if (toName == null || toName.isEmpty() || this.Rnfr == null) {
            sendResponse("501 Syntax error in parameters or arguments.");
            return;
        }

        toName = getName(toName);

        // Specify the old file/folder path and the new name
        Path oldPath = Paths.get(this.curPath + "/" + this.Rnfr);
        Path newPath = Paths.get(this.curPath + "/" + toName);

        try {
            // Rename the file/folder
            Files.move(oldPath, newPath);
            sendResponse("250 Requested file action okay, completed.");
        } catch (IOException e) {
            sendResponse("550 Requested action not taken. File unavailable");
        }
    }

    private String getName(String name) {
        return name.substring(1, name.length() - 1);
    }

    private void handleRmd(StringTokenizer tokenizer) {
        String folderName = tokenizer.nextToken();

        if (folderName == null || folderName.isEmpty()) {
            sendResponse("550 Empty name");
        }

        folderName = folderName.substring(1, folderName.length() - 1);

        File directory = new File(this.curPath + "/" + folderName);

        if (directory.isDirectory() && deleteDirectory(directory)) {
            sendResponse("250 Directory removed successfully.");
        } else {
            sendResponse("550 Directory not empty or permission denied.");
        }
    }

    private void handleDele(StringTokenizer tokenizer) {
        String fileName = tokenizer.nextToken();

        if (fileName == null || fileName.isEmpty()) {
            sendResponse("550 Error");
        }

        fileName = fileName.substring(1, fileName.length() - 1);

        File file = new File(this.curPath + "/" + fileName);

        if (file.isFile() && file.delete()) {
            sendResponse("250 File deleted successfully.");
        } else {
            sendResponse("550 File not found or permission denied.");
        }

    }

    private boolean deleteDirectory(File dir) {
        // Check if the directory exists
        if (dir.exists()) {
            // List all files and directories
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Recursively delete files and directories
                    if (file.isDirectory()) {
                        deleteDirectory(file); // Recurse into subdirectory
                    }
                    // Delete file or empty directory
                    file.delete();
                }
            }
        }
        // Finally, delete the directory itself
        return dir.delete();
    }

    private void handleMkd(StringTokenizer tokenizer) {
        String folderName = tokenizer.nextToken();

        if (folderName == null || folderName.isEmpty()) {
            sendResponse("550 Empty name");
        }

        folderName = folderName.substring(1, folderName.length() - 1);

        File directory = new File(this.curPath + "/" + folderName);

        if (directory.mkdir()) {
            sendResponse("257 \"" + folderName + "\"" + " created");
        } else {
            sendResponse("550 Permission denied");
        }
    }

    private void handleCwd(StringTokenizer tokenizer) {
        String filename = tokenizer.nextToken();

        if (filename == null || filename.isEmpty()) {
            sendResponse("500 Syntax error, command unrecognized.");
            return;
        }

        
        Path path = Paths.get(this.curPath + "/" + getName(filename));
        Path realPath;

        try {
            realPath = path.normalize().toRealPath();
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse("500 Syntax error, command unrecognized.");
            return;
        }

        String newPath = realPath.toString();
    
        File f = new File(newPath);
    
        if (f.exists() && f.isDirectory() && (newPath.length() >= this.rootPath.length())) {
            this.Rnfr = null;
            this.curPath = newPath;
            sendResponse("250 Requested file action okay, completed.");
        } else {
            sendResponse("550 Requested action not taken. File unavailable.");
        }
    }

    private void handleQuit() {
        sendResponse("221 Closing connection");
        try {
            this.clientSocket.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        this.isClosed = true;
    }
    
    private void handlePwd() {
        sendResponse("257 \"" + this.curPath + "\"");
    }

    private void handleUser(StringTokenizer tokenizer) {
        String username = tokenizer.nextToken();
        if (this.ftpServerDatabase.checkUsername(username)) {
            this.username = username;
            this.isLoggedIn = 0;
            sendResponse("331 Username OK, need password");
        } else {
            this.isLoggedIn = -1;
            sendResponse("530 Invalid username");
        };
    }

    private void handlePass(StringTokenizer tokenizer) {
        String password = tokenizer.nextToken();
        if (this.ftpServerDatabase.checkPassword(this.username, password)) {
            if (this.isLoggedIn != 0) {
                sendResponse("530 Missing username");
                return;
            }
            sendResponse("230 User logged in, proceed");
            this.isLoggedIn = 1;
        } else {
            sendResponse("530 Invalid password");
        }
    }

    private void handleList(String args) {
        Map<String, Boolean> dirContent = nlstHelper(args);

        if (dirContent == null) {
            sendResponse("550 File does not exist.");
        } else {
            sendResponse("125 Opening ASCII mode data connection for file list.");

            for (Map.Entry<String, Boolean> entry : dirContent.entrySet()) {
                String filename = entry.getKey(); // Get the filename
                Boolean isFile = entry.getValue();

                sendResponse((isFile ? "f" : "d") + '"' + filename + '"');
            }

            sendResponse("226 Transfer complete.");
        }

    }

    private void handleRetr(StringTokenizer tokenizer) {
        String fileName = tokenizer.nextToken();

        if (fileName == null || fileName.isEmpty()) {
            sendResponse("500 Syntax error.");
            return;
        }

        fileName = getName(fileName);
		
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(this.curPath + "/" + fileName);
            sendResponse("150 File status okay.");
        } catch (Exception e) {
            sendResponse("550 File unavailable.");
            return;
        }
		
		byte[] buffer = new byte[65536];
        int bytesRead;

        OutputStream outputStream = null;
        
        try {
            outputStream = this.clientSocket.getOutputStream();

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
            
            outputStream.write(eof);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            fileInputStream.close();
        } catch (Exception e) {

        }

        String line;
        try {
            line = in.readLine();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        sendResponse("226 Closing data connection.");
    }

    private void handleStor(StringTokenizer tokenizer) {
        String fileName = tokenizer.nextToken();

        if (fileName == null || fileName.isEmpty()) {
            sendResponse("500 Syntax error.");
            return;
        }

        fileName = getName(fileName);
        String pathFile = this.curPath + "/" + fileName;

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pathFile, false);
            sendResponse("150 File status okay.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse("553 Requested action not taken.");
            try {
                fileOutputStream.close();
            } catch (Exception ex) {
                // TODO: handle exception
            }
            
            return;
        }

        byte[] buffer = new byte[65536];
        int bytesRead;

        InputStream inputStream;
        try {
            inputStream = this.clientSocket.getInputStream();
            
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
        } catch (Exception e) {
            e.printStackTrace();
            
        }        

        try {
            fileOutputStream.close();
        } catch (Exception e) {
            
        }

        sendResponse("226 Closing data connection.");
    }

    private void sendResponse(String response) {
        out.println(response);
        System.out.println("[" + ipPortClient + "] (" + getCurrentDateTime() + ") Sent: " + response);
    }
}