package Lib;

import com.google.gson.annotations.Expose;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Head
{
    @Expose
    public String branch = "";
    @Expose
    public Commit activeCommit;
    String commitSHA1 = "";
    StringProperty branchProperty = new SimpleStringProperty(this,"branchProperty","");

    public String getBranchProperty()
    {
        return branchProperty.get();
    }

    public StringProperty branchPropertyProperty()
    {
        return branchProperty;
    }

    public void setBranchProperty(String branchProperty)
    {
        this.branchProperty.set(branchProperty);
    }

    public Head(String branch, Commit activeCommit,String commitSHA1)
    {
        this.branch = branch;
        this.activeCommit = activeCommit;
        this.commitSHA1 = commitSHA1;
        setBranchProperty(branch);
    }

    public Head(String branch)
    {
        this.branch = branch;
        setBranchProperty(branch);
    }

    public void setInfo(String branch, Commit commit, String currentCommit)
    {
        this.branch = branch;
        this.activeCommit = commit;
        this.commitSHA1 = currentCommit;
        setBranchProperty(branch);
    }
}
