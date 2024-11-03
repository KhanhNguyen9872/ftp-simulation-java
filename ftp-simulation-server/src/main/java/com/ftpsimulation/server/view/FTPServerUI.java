package com.ftpsimulation.server.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.ftpsimulation.server.controller.*;

public class FTPServerUI {
    private FTPServerController controller;
    private PrintWriter stdOut;
    private BufferedReader stdIn;

    public FTPServerUI(FTPServerController controller) {
        this.controller = controller;
        this.stdOut = new PrintWriter(System.out, true);
        this.stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() throws IOException {
        do {
            try {
                this.controller.execute();
                break;
            } catch (Exception ex) {
                this.stdOut.println(ex.getMessage());
            }
        } while (true);
    }
}
