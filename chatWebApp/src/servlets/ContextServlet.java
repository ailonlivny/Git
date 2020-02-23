package servlets;
import Lib.Settings;
import Lib.Utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ContextServlet implements ServletContextListener
{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        File f = new File("c:\\magit-ex3");
        f.mkdir();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        Utils.clearAllFiles("c:\\magit-ex3");
        File f = new File("c:\\magit-ex3");
        f.delete();
    }
}
