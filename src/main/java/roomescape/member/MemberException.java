package roomescape.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum MemberException implements ExceptionCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패", "아이디와 비밀번호가 일치하지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "존재하지 않는 role", "존재하지 않는 role값 입니다.")
    ;

    private HttpStatus httpStatus;
    private String title;
    private String detail;

    MemberException(HttpStatus httpStatus, String title, String detail) {
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
