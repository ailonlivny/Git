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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteBranchServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        boolean isHeadBranch = false;
        resp.setContentType("application/json;charset=UTF-8");
        String branchName = req.getParameter("branchName");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        MainEngine engine = repositoryManager.getEngine();
        String pathTo = Settings.repositoryFullPath;
        if(repositoryManager.getHead().branch.equals(branchName))
        {
            isHeadBranch = true;
        }
        else
        {
            File f = new File(Settings.getBranchFolderPath() + branchName);
            f.delete();

            if(engine.isRepoHasRemote())
            {
                File f2 = new File(Settings.getBranchFolderPath() + branchName + "RB");
                f2.delete();

                String fullRepoPath = repositoryManager.settings().repositoryFullPath;
                String pathFrom = new String(Files.readAllBytes(Paths.get( fullRepoPath+ "\\.magit\\pathToSrc")));
                String[] line = pathFrom.split("\\\\");
                String userNameToNotify = line[line.length-2];
                try {
                    engine.switchRepository(pathFrom);
                    File f3 = new File(Settings.getBranchFolderPath() + branchName);
                    f3.delete();
                    engine.switchRepository(pathTo);
                } catch (FirstCommitException e) {
                    e.printStackTrace();
                }
                UserInSystem userToNotify = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userNameToNotify);
                Notification notification=new Notification(user.getUser().getName(),user.getUser().getName()+ " deleted the branch:" + branchName,"Remote branch deleted");
                userToNotify.getNotificationsManager().addNewMessage(notification);
            }
        }

    }

}
