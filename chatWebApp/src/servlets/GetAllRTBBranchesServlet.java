package servlets;

import Lib.RepositoryManager;
import Lib.Settings;
import com.google.gson.Gson;
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
import java.util.ArrayList;

public class GetAllRTBBranchesServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        ArrayList<String> allBranches = repositoryManager.getEngine().getAllBranchnames();
        ArrayList<String> allRTBBranches = new ArrayList<>();

        for(String branch : allBranches)
        {
            String content = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + branch)));
            if(content.contains("RTB"))
            {
                allRTBBranches.add(branch);
            }
        }
        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(allRTBBranches);
            out.println(json);
        }
    }
}
