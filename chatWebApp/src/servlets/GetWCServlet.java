package servlets;

import Lib.Item;
import Lib.MainEngine;
import Lib.RepositoryManager;
import Lib.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetWCServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String repoName = request.getParameter("repoName");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager=user.getRepositoryManager();
        try {
            repositoryManager.refreshObj();
        } catch (FirstCommitException e) {
            System.out.println(e.getMessage());
        }
        Map<String, Item> ret = MainEngine.createWC(repositoryManager.settings().getRepoFullPathProperty());
        List<Item> ItemList = new ArrayList<>(ret.values());
        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
            String json= gson.toJson(ItemList);
            out.println(json);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
