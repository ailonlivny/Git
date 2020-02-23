package Lib;

abstract public class Item
{
    String currentSHA1;
    String userLastModified;
    String lastModified;
    String fullPath;
    String typeItem;
    String name;

    public String getCurrentSHA1()
    {
        return currentSHA1;
    }
}