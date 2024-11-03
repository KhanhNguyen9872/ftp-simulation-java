package com.ftpsimulation.server;

import com.ftpsimulation.server.controller.FTPServerController;
import com.ftpsimulation.server.model.FTPServerDatabase;
import com.ftpsimulation.server.model.FTPServerModel;
import com.ftpsimulation.server.view.FTPServerUI;

/**
 * Hello world!
 *
 */
public class FTPServerApp 
{
    public static void main( String[] args ) throws Exception
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        FTPServerModel model = new FTPServerModel();
        FTPServerController controller = new FTPServerController(model);
        FTPServerUI ui = new FTPServerUI(controller);
        
        ui.run();
    }
}
