package com.ftpsimulation.server.controller;

import com.ftpsimulation.server.model.FTPServerModel;

public class FTPServerController {
    private FTPServerModel model;

    public FTPServerController(FTPServerModel model) {
        this.model = model;
    }

    public void execute() throws Exception {
        this.model.start();
    }
}
