package roomescape.member.presentation.request;

public record MemberRequest(
        String name,
        String email,
        String password
) {
}
