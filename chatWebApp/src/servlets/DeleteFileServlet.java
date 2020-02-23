package servlets;

import Lib.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


public class DeleteFileServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String filePath = request.getParameter("filePath");
        File f = new File(filePath);
        if(f.isDirectory())
        {
            Utils.clearAllFiles(filePath);
        }
        f.delete();
    }
}
