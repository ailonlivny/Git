package exceptions;

public class BranchNotExistException extends Exception
{
    public BranchNotExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
