package roomescape.member.presentation.request;

public record LoginRequest(
        String email,
        String password
) {
}
