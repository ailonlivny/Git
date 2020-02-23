package Lib;

public class Blob extends Item
{
    String content;

    Blob (String name,String content,String fullPath, String currentSHA1,String userLastModified,String lastModified)
    {
        this.name = name;
        typeItem = "File";
        this.content = content;
        this.currentSHA1=currentSHA1;
        this.userLastModified = userLastModified;
        this.lastModified = lastModified;
        this.fullPath = fullPath;
    }

    Blob (String name,String fullPath, String currentSHA1,String content)
    {
        this.content = content;
        typeItem = "File";
        this.name = name;
        this.currentSHA1 = currentSHA1;
        this.fullPath = fullPath;
    }

    public void printFileInfo()
    {
      //  System.out.println(this.name + Lib.Settings.delimiter + this.typeItem + Lib.Settings.delimiter + this.getCurrentSHA1() + Lib.Settings.delimiter + this.userLastModified + Lib.Settings.delimiter + lastModified);
    }

    public String getRepoPath()
    {
        return fullPath.substring(Settings.repositoryFullPath.length()+1,fullPath.length());
    }
}

