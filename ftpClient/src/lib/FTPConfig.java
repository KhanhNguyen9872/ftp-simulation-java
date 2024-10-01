package lib;

public class FTPConfig {
	private String ipAddress;
	private String port;
	private String username;
	private String password;
	
	public FTPConfig(String ipAddress, String port, String username, String password) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public String getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
