package roomescape.exception;

public record ErrorResponse(
        String title,
        int status,
        String detail,
        String instance
) {
    public static ErrorResponse of(ExceptionCode code, String instance, String message) {
        return new ErrorResponse(code.getTitle(), code.getHttpStatus().value(), message, instance);
    }
}
