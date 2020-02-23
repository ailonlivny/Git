package servlets;

import Lib.*;
import exceptions.FirstCommitException;
import exceptions.NoChangesMadeException;
import notification.Notification;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AcceptPRServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String key = req.getParameter("key");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();

        PRManager prManager = user.getPRManager();
        prManager.SolvePR(key, PRStatus.ACCEPTED);
        PR PRToMerge = prManager.getPullRequestMap().get(key);
        String PRCreator = prManager.getPullRequestMap().get(key).user.getName();
        UserInSystem userPRCreator = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(PRCreator);
        userPRCreator.getPRManager().SolvePR(key,PRStatus.ACCEPTED);
        Notification notification=new Notification(user.getUser().getName(),"PR was Accepted by " + user.getUser().getName(),"PR Accepted");
        userPRCreator.getNotificationsManager().addNewMessage(notification);

        String pathFrom = repositoryManager.settings().repositoryFullPath;
        String pathTo = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\pathToDest")));

        try {
            repositoryManager.getEngine().switchRepository(pathTo);
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        String targetBranch = PRToMerge.targetBranch;
        String targetSHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + targetBranch)));
        targetBranch = targetBranch + "PR";

        try {
            repositoryManager.getEngine().switchRepository(pathFrom);
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }

        String baseBranch = PRToMerge.baseBranch;
        String baseSHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + baseBranch)));
        String[] lines = targetSHA1.split("\n");
        targetSHA1 = lines[0];
        try {
            repositoryManager.getEngine().switchRepository(pathTo);
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        repositoryManager.getEngine().CopyDeltaFromTo(pathTo,pathFrom,baseSHA1,targetSHA1);
        try {
            repositoryManager.getEngine().switchRepository(pathFrom);
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        File f = new File(Settings.getBranchFolderPath() + targetBranch);
        f.createNewFile();
        FileWriter fw1 = new FileWriter ( Settings.getBranchFolderPath() + targetBranch);
        fw1.write(targetSHA1);
        fw1.close();

        try {
            repositoryManager.refreshObj();
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }

        repositoryManager.getEngine().merge(baseBranch,targetBranch,true);
        if(repositoryManager.getHead().branch.equals(baseBranch))
        {
            try {
                repositoryManager.refreshObj();
                Utils.clearCurrentWC();
                repositoryManager.replaceWC();
                repositoryManager.getEngine().commit("Auto Commit for PR Merge");
            } catch (FirstCommitException e) {
                e.printStackTrace();
            } catch (NoChangesMadeException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {

                String activeBranch = repositoryManager.getHead().branch;
                repositoryManager.getEngine().checkoutBranch(baseBranch);
                repositoryManager.refreshObj();
                Utils.clearCurrentWC();
                repositoryManager.replaceWC();
                repositoryManager.getEngine().commit("Auto Commit for PR Merge");
                repositoryManager.getEngine().checkoutBranch(activeBranch);
            } catch ( NoChangesMadeException | FirstCommitException e)
            {
                e.printStackTrace();
            }

        }

        f.delete();
    }
}
