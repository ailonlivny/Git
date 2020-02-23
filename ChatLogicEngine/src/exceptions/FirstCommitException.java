package exceptions;

public class FirstCommitException extends Exception
{
    public FirstCommitException()
    {
        super("There are no commits done in the system");
    }
}
