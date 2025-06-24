package roomescape.reservation.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum ReservationException implements ExceptionCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 예약 요청", "날짜, 테마, 시간은 필수 입력값 입니다."),
    INVALID_ADMIN_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 예약 요청", "관리자 예약 시 name은 필수 입력값 입니다.")
    ;

    private HttpStatus httpStatus;
    private String title;
    private String detail;

    ReservationException(HttpStatus httpStatus, String title, String detail) {
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
