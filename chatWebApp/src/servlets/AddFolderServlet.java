package servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddFolderServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String filePath = request.getParameter("filePath");
        String folderName = request.getParameter("folderName");
        filePath = filePath + "\\" + folderName;
        File f = new File(filePath);
        f.mkdir();
    }
}
