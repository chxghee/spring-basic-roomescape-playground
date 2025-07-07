package roomescape.theme.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum ThemeException implements ExceptionCode {

    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 테마", "해당 id의 테마가 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    ThemeException(HttpStatus httpStatus, String title, String detail) {
        this.httpStatus = httpStatus;
        this.title = title;
        this.detail = detail;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDetail() {
        return detail;
    }
}
