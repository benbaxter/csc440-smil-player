package smil.mobile.activity;

public class SMILWriteException extends Exception
{
    private static final long serialVersionUID = 1L;


    public SMILWriteException(String message)
    {
        super(message);
    }


    public SMILWriteException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
