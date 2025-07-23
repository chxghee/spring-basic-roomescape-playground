package roomescape.exception;

import java.util.List;

public class ApplicationException extends RuntimeException {

    private final ExceptionCode code;

    public ApplicationException(ExceptionCode code) {
        super(code.getDetail());
        this.code = code;
    }

    public ApplicationException(ExceptionCode code, List<String> errorFields) {
        super(String.join(", ", errorFields) + code.getDetail());
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }
}
