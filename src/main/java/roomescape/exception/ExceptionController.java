package roomescape.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("알 수 없는 오류", 500, "관리자에게 문의 하세요", request.getRequestURI()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e, HttpServletRequest request) {
        return ResponseEntity.status(e.getCode().getHttpStatus())
                .body(ErrorResponse.of(e.getCode(), request.getRequestURI(), e.getMessage()));
    }
}
