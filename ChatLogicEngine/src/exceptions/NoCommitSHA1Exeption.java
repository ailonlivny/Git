package exceptions;

public class NoCommitSHA1Exeption extends Exception
{
    public NoCommitSHA1Exeption(String errorMessage)
    {
        super(errorMessage);
    }
}

