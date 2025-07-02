package roomescape.waiting.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.auth.AuthenticatedMember;
import roomescape.auth.LoginMember;
import roomescape.waiting.application.WaitingService;
import roomescape.waiting.presentation.request.WaitingRequest;
import roomescape.waiting.presentation.response.WaitingResponse;

import java.net.URI;

@Controller
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(@RequestBody WaitingRequest waitingRequest,
                                                       @AuthenticatedMember LoginMember loginMember) {
        WaitingResponse waiting = waitingService.save(waitingRequest.toCommand(loginMember));
        return ResponseEntity.created(URI.create("/waitings/" + waiting.id())).body(waiting);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> cancelWaiting(@PathVariable("id") Long id,
                                              @AuthenticatedMember LoginMember loginMember) {
        waitingService.delete(loginMember, id);
        return ResponseEntity.noContent().build();
    }


}
