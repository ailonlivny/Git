package Lib;

import com.google.gson.annotations.Expose;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commit implements CommitRepresentative , Comparable<Commit>
{
    @Expose
    String msg;
    String commitTime;
    String commitSHA1 = "";
    String userLastModified;
    String rootSha1;
    public Folder rootFolder;
    Commit previousCommit;
    Commit secondPreviousCommit;
    public String branchList = "";




    Commit(String msg, Folder rootFolder, Commit previousCommit,Commit secondPreviousCommit,String commitTime,String userLastModified,String commitSHA1)
    {
        this.msg = msg;
        this.rootSha1 = rootFolder.currentSHA1;
        this.rootFolder = rootFolder;
        this.previousCommit = previousCommit;
        this.commitTime = commitTime;
        this.userLastModified = userLastModified;
        this.commitSHA1 = commitSHA1;
        this.secondPreviousCommit = secondPreviousCommit;
    }

    public void printInfo()
    {
        System.out.println(commitSHA1 + "\n" + msg + "\n" + commitTime  + "\n" + userLastModified + "\n");
    }

    @Override
    public String getSha1()
    {
        return commitSHA1;
    }

    @Override
    public String getFirstPrecedingSha1()
    {
        if(previousCommit != null)
        {
            return previousCommit.commitSHA1;
        }
        else
        {
            return "";
        }
    }

    @Override
    public String getSecondPrecedingSha1()
    {
        if(secondPreviousCommit != null)
        {
            return secondPreviousCommit.commitSHA1;
        }
        else
        {
            return "";
        }
    }

    @Override
    public int compareTo(Commit o)
    {
        try
        {
            Date date1 = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss").parse(this.commitTime);
            Date date2 = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss").parse(o.commitTime);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            //Controller.CreateAlertDialog(null,"Error!!",e.getMessage());
        }
        return 0;
    }
}
