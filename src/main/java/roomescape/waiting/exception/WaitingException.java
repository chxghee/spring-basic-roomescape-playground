package roomescape.waiting.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum WaitingException implements ExceptionCode {

    WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약 대기", "해당 id의 예약 대기가 존재하지 않습니다."),
    DUPLICATE_WAITING_REQUEST(HttpStatus.BAD_REQUEST, "중복된 예약 대기 요청", "이미 해당 시간에 예약 대기를 했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    WaitingException(HttpStatus httpStatus, String title, String detail) {
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
