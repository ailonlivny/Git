package users;

public class User
{
    private boolean isLogIn;
    private String name;

    public User(String name)
    {
        this.isLogIn = true;
        this.name = name;
    }

    public boolean isLogIn()
    {
        return isLogIn;
    }

    public void setLogIn(boolean logIn)
    {
        isLogIn = logIn;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
