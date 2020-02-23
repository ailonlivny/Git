package users;

import Lib.PRManager;
import Lib.Repository;
import Lib.RepositoryManager;
import Lib.Settings;
import constants.Constants;
import notification.NotificationManager;

import java.io.File;
import java.util.*;

public class UserInSystem
{

    private User user;
    private RepositoryManager repositoryManager;
    private Map<String,Repository> repositories;
    private Map<String,Object> repositoriesLocks;
    private NotificationManager notificationManager;
    private PRManager PRManager;
    private boolean logedIn;

    public UserInSystem(User user)
    {
        this.user=user;
        repositories=new HashMap<>();
        repositoryManager=new RepositoryManager();
        Settings.setUser(user.getName());
        repositoriesLocks=new HashMap<>();
        notificationManager=new NotificationManager();
        createDirectoryForUser();
        logedIn=false;
        PRManager = new PRManager();
    }


    public Lib.PRManager getPRManager()
    {
        return PRManager;
    }

    private void createDirectoryForUser()
    {
        File directory = new File(Constants.ALL_USERS_FOLDER+user.getName());
        if(!directory.mkdirs())
        {
            System.out.println("failed to make directory for: " +user.getName());
        }
    }

    public Map<String,Repository> getRepositories()
    {
        return Collections.unmodifiableMap(repositories);
    }

    public RepositoryManager getRepositoryManager()
    {
        return repositoryManager;
    }
    public User getUser()
    {
        return user;
    }

    public boolean isLogedIn()
    {
        return logedIn;
    }

    public void setLogedIn(boolean logedIn)
    {
        this.logedIn = logedIn;
    }

    public void addRepository(Repository repository)
    {
        repositories.put(repository.getName(),repository);
        final Object repositoryLock=new Object();
        repositoriesLocks.put(repository.getName(),repositoryLock);
    }

    public NotificationManager getNotificationsManager() {
        return notificationManager;
    }
}