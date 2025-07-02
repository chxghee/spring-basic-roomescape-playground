package roomescape.reservation.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum ReservationException implements ExceptionCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 예약 요청", "날짜, 테마, 시간은 필수 입력값 입니다."),
    INVALID_ADMIN_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 예약 요청", "관리자 예약 시 name은 필수 입력값 입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약", "해당 id의 예약이 존재하지 않습니다."),
    DUPLICATE_RESERVATION_REQUEST(HttpStatus.BAD_REQUEST, "중복된 예약 요청", "이미 해당 시간에 예약을 했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

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
