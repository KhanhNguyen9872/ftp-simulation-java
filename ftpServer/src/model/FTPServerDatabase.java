package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FTPServerDatabase {
    private Connection connection = null;
    private String ipAddress;
    private int port;
    private String database;
    private String username;
    private String password;

    public FTPServerDatabase(String ipAddress, int port, String database, String username, String password) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    private void connect() {
        if (this.connection == null) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&useSSL=false", username, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
    
    private void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            this.connection = null;
        }
    }

    public boolean checkUsername(String username) {
        connect();

        String sql = "SELECT username FROM account WHERE (username = ?);";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if (username.equals(resultSet.getString("username"))) {
                    return true;
                }
            }

        } catch (Exception e) {
        	e.printStackTrace();
            // TODO: handle exception
        }

        close();
        return false;
    }

    public boolean checkPassword(String username, String password) {
        connect();

        String sql = "SELECT password FROM account WHERE (username = ?);";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if (password.equals(resultSet.getString("password"))) {
                    return true;
                }
            }

        } catch (Exception e) {
        	e.printStackTrace();
            // TODO: handle exception
        }

        close();
        return false;
    }

    public int getPortServer() {
        connect();

        String sql = "SELECT port FROM settings WHERE (id = ?);";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("port");
            }

        } catch (Exception e) {
        	e.printStackTrace();
            // TODO: handle exception
        }

        close();
        return -1;
    }

    public String getPathServer() {
        connect();

        String sql = "SELECT path FROM settings WHERE (id = ?);";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("path");
            }

        } catch (Exception e) {
        	e.printStackTrace();
            // TODO: handle exception
        }

        close();
        return null;
    }

	public void saveSettings(String path, int port2) {
		connect();

        String sql = "UPDATE settings SET port = ?, path = ? WHERE (id = 1);";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setInt(1, port2);
            preparedStatement.setString(2, path);

            preparedStatement.execute();

        } catch (Exception e) {
        	e.printStackTrace();
            // TODO: handle exception
        }

        close();
	}
}
