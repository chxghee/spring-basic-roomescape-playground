package roomescape.member.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum MemberException implements ExceptionCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패", "아이디와 비밀번호가 일치하지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "존재하지 않는 role", "존재하지 않는 role값 입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자", "해당 id의 사용자가 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

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
