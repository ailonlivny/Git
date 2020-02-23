package servlets;

import Lib.RepositoryManager;
import com.google.gson.Gson;
import exceptions.*;
import notification.Notification;
import notification.NotificationManager;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GetAllNotificationsServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        UserInSystem currentUser= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(request));
        NotificationManager notificationManager= currentUser.getNotificationsManager();
        List<Notification> arrayNotifications = new ArrayList<>(notificationManager.getMessages().values());
        arrayNotifications.removeIf(s -> s.getRead() == true);
        arrayNotifications.forEach(n -> n.setRead(true));
        Gson gson = new Gson();
        String json=gson.toJson(arrayNotifications);
        try(PrintWriter out=response.getWriter())
        {
                out.print(json);
        }
    }
}
