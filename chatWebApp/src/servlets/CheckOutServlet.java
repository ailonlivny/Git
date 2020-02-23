package servlets;

import Lib.RepositoryManager;
import com.google.gson.Gson;
import exceptions.FirstCommitException;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class CheckOutServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String isOpenChanges = null;
        response.setContentType("application/json;charset=UTF-8");
        String branchName = request.getParameter("branchName");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        try {
            if(repositoryManager.WorkingCopyStatus().equals(""))
            {
                repositoryManager.getEngine().checkoutBranch(branchName);
            }
            else
            {
                isOpenChanges = "has changes";
            }
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        try(PrintWriter out=response.getWriter())
        {
            Gson gson = new Gson();
            String json=gson.toJson(isOpenChanges);
            out.println(json);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
}
