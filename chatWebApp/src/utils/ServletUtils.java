package utils;

import Lib.RepositoryManager;
import users.UserInSystem;
import users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils
{
	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final Object userManagerLock = new Object();

	public static UserManager getUserManaqer(ServletContext servletContext)
	{
		synchronized (userManagerLock)
		{
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null)
			{
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static RepositoryManager getRepositoryManager(ServletContext servletContext, HttpServletRequest req)
	{
		UserInSystem currentUser = ServletUtils.getUserManaqer(servletContext).getUsers().get(SessionUtils.getUserName(req));
		return currentUser.getRepositoryManager();
	}
}
