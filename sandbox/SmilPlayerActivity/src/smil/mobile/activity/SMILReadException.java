package smil.mobile.activity;

public class SMILReadException extends Exception
{
    private static final long serialVersionUID = 1L;


    public SMILReadException(String message)
    {
        super(message);
    }


    public SMILReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
