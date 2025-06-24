package roomescape.exception;

public class ApplicationException extends RuntimeException {

    private final ExceptionCode code;

    public ApplicationException(ExceptionCode code) {
        super(code.getTitle());
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }
}
