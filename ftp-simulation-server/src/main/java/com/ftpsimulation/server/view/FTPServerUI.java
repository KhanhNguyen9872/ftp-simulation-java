package com.ftpsimulation.server.view;

import java.io.IOException;
import java.io.PrintWriter;

import com.ftpsimulation.server.controller.*;

public class FTPServerUI {
    private FTPServerController controller;
    private PrintWriter stdOut;

    public FTPServerUI(FTPServerController controller) {
        this.controller = controller;
        this.stdOut = new PrintWriter(System.out, true);
    }

    public void run() throws IOException {
        try {
            this.controller.execute();
        } catch (Exception ex) {
            this.stdOut.println(ex.getMessage());
        }
    }
}
