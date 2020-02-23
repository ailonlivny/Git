package exceptions;

public class NoRepositoryExeption extends Exception
{
    public NoRepositoryExeption(String errorMessage)
    {
        super(errorMessage);
    }
}
