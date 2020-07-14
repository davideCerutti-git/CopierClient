package model;

import java.io.File;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.log4j.Logger;


public class LocalFtpServer {

    private FtpServer server;
    private Logger log;
    
    public LocalFtpServer(Logger _log) {
    	log=_log;
    }

    public void startServer() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("properties//ftpserver.properties"));
        userManagerFactory.setPasswordEncryptor(passwordEncryptor());
        UserManager um = userManagerFactory.createUserManager();
        serverFactory.setUserManager(um);
        server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException ex) {
            log.error(ex);
            ex.printStackTrace();
        }
    }

    private PasswordEncryptor passwordEncryptor() {
        return new PasswordEncryptor() {
            @Override
            public String encrypt(String password) {
                return password;
            }
            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
                return passwordToCheck.equals(storedPassword);
            }
        };
    }

    public void shutdown() {
        server.stop();
    }
}