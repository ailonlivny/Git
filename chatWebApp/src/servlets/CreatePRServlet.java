package servlets;

import Lib.MainEngine;
import Lib.PR;
import Lib.PRManager;
import Lib.RepositoryManager;
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
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class CreatePRServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        boolean isRemote = false;
        resp.setContentType("application/json;charset=UTF-8");
        String branchName = req.getParameter("branchName");
        String baseBranch = req.getParameter("baseBranch");
        String Msg = req.getParameter("Msg");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        MainEngine engine = repositoryManager.getEngine();

        if(engine.isRepoHasRemote())
        {
            isRemote = true;
            PRManager prManager = user.getPRManager();
            String fullRepoPath = repositoryManager.settings().repositoryFullPath;
            PR pullRequest = new PR(branchName,baseBranch,Msg,user.getUser(),MainEngine.getCurrTime());
            String pathFrom = new String(Files.readAllBytes(Paths.get( fullRepoPath+ "\\.magit\\pathToSrc")));
            String[] line = pathFrom.split("\\\\");
            String userNameToNotify = line[line.length-2];
            try {
                pullRequest.setChanges(repositoryManager.changesListForPr(pathFrom));
                prManager.AddPullRequest(pullRequest,pullRequest.msg);
            } catch (FirstCommitException e) {
                e.printStackTrace();
            }

            UserInSystem userToNotify = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userNameToNotify);
            userToNotify.getPRManager().AddPullRequest(pullRequest,pullRequest.msg);
            Notification notification=new Notification(user.getUser().getName(),user.getUser().getName()+ " sent you a pull request","Pull Request");
            userToNotify.getNotificationsManager().addNewMessage(notification);
        }

        try(PrintWriter out=resp.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(isRemote);
            out.println(json);
        }
    }
}
