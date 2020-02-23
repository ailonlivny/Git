package servlets;

import Lib.Commit;
import Lib.RepositoryManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

public class GetFileListFromCommitServlet extends HttpServlet
{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String SHA1 = request.getParameter("SHA1");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager=user.getRepositoryManager();
        try {
            repositoryManager.refreshObj();
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        List<String> Files = new ArrayList<>(repositoryManager.getCommitSet().get(SHA1).rootFolder.createMapFromTree().keySet());
        try(PrintWriter out=response.getWriter())
        {
//            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson gson=new Gson();
            String json= gson.toJson(Files);
            System.out.println(json);
            out.println(json);
        }
    }
}
