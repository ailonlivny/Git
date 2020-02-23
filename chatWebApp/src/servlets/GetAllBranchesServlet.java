package servlets;

import Lib.RepositoryManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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


public class GetAllBranchesServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        ArrayList<String> allBranches = repositoryManager.getEngine().getAllBranchnames();
        allBranches.remove(repositoryManager.getHead().branch);
        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
//            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json=gson.toJson(allBranches);
            out.println(json);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
}
