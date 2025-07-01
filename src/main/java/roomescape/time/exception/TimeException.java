package roomescape.time.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum TimeException implements ExceptionCode {

    TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 타임", "해당 id의 타임이 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    TimeException(HttpStatus httpStatus, String title, String detail) {
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
