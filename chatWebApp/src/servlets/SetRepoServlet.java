package servlets;

import Lib.RepositoryManager;
import constants.Constants;
import exceptions.FirstCommitException;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class SetRepoServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String repoName = request.getParameter("repoName");
        String userName = SessionUtils.getUserName(request);
        String repoPath = Constants.ALL_USERS_FOLDER + userName + "\\" + repoName;
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        repositoryManager.settings().setNewRepository(repoPath);
//        try {
//            repositoryManager.refreshObj();
//        } catch (FirstCommitException e) {
//            e.printStackTrace();
//        }

//        System.out.println(repoPath);
    }

}
