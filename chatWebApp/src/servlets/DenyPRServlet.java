package servlets;

import Lib.*;
import com.google.gson.Gson;
import exceptions.FirstCommitException;
import notification.Notification;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DenyPRServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String denyMsg = req.getParameter("denyMsg");
        String key = req.getParameter("key");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
//        RepositoryManager repositoryManager = user.getRepositoryManager();

        PRManager prManager = user.getPRManager();
        prManager.SolvePR(key,PRStatus.DENIED);
        String PRCreator = prManager.getPullRequestMap().get(key).user.getName();
        UserInSystem userPRCreator = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(PRCreator);
        userPRCreator.getPRManager().SolvePR(key,PRStatus.DENIED);
        Notification notification=new Notification(user.getUser().getName(),denyMsg,"Denied Pull Request");
        userPRCreator.getNotificationsManager().addNewMessage(notification);

    }

}
