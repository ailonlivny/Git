package servlets;

import Lib.RepositoryManager;
import exceptions.FirstCommitException;
import exceptions.NoChangesMadeException;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MakeCommitServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(request);
        String commitMsg = request.getParameter("commitMsg");
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        try {
            repositoryManager.getEngine().commit(commitMsg);
        } catch (FirstCommitException e) {
            e.printStackTrace();
        } catch (NoChangesMadeException e) {
            e.printStackTrace();
        }
    }
}
