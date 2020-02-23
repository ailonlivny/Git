package Lib;

import users.User;

import java.util.ArrayList;
import java.util.List;

public class PR
{
    public String targetBranch;
    public String baseBranch;
    public String msg;
    public User user;
    String date;
    PRStatus status;
    List<Item> changes;

    public List<Item> getChanges() {
        return changes;
    }

    public void setChanges(List<Item> changes)
    {
        this.changes = changes;
    }



    public PR(String targetBranch, String baseBranch, String msg, User user, String date)
    {
        this.targetBranch = targetBranch;
        this.baseBranch = baseBranch;
        this.msg = msg;
        this.user = user;
        this.date = date;
        this.status = PRStatus.OPEN;
        changes = new ArrayList<>();
    }


    public void setStatus(PRStatus status){
        this.status=status;
    }
}
