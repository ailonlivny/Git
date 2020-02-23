package Lib;

abstract public class Item
{
    String currentSHA1;
    String userLastModified;
    String lastModified;
    String fullPath;
    String typeItem;
    String name;
    public String contentWC = null;
    String status;
    String relativePath = "";

    public void setStatus(String status)
    {
        this.status = status;
    }


    public String getCurrentSHA1()
    {
        return currentSHA1;
    }


}