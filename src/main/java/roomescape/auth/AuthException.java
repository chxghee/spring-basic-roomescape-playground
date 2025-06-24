package roomescape.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum AuthException implements ExceptionCode {

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "access token 만료", "access token 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "access token 형식 오류", "access token이 유효하지 않습니다."),
    UNAUTHENTICATED_REQUEST(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청", "로그인이 필요합니다."),
    FORBIDDEN_ADMIN_ACCESS(HttpStatus.FORBIDDEN, "관리자 권한 없음", "관리자 권한이 필요합니다.")
    ;

    private HttpStatus httpStatus;
    private String title;
    private String detail;

    AuthException(HttpStatus httpStatus, String title, String detail) {
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
