package servlets;

import Lib.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class AddFileServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String filePath = request.getParameter("filePath");
        String content = request.getParameter("content");
        String fileName = request.getParameter("fileName");
        filePath = filePath + "\\" + fileName;
        File f = new File(filePath + ".txt");
        f.createNewFile();
        FileWriter fw = new FileWriter (f);
        fw.write(content);
        fw.close();
    }
}
