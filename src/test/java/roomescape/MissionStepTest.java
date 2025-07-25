package roomescape;

import jwt.JwtTokenProvider;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.reservation.presentation.response.MyReservationResponse;
import roomescape.reservation.presentation.response.ReservationResponse;
import roomescape.waiting.presentation.response.WaitingResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class MissionStepTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 일단계() {
        String token = createToken("admin@email.com", "password");

        assertThat(token).isNotBlank();

        ExtractableResponse<Response> checkResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract();

        assertThat(checkResponse.body().jsonPath().getString("name")).isEqualTo("어드민");
    }

    private static String createToken(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        return response.headers().get("Set-Cookie").getValue().split(";")[0].split("=")[1];
    }

    @Test
    void 이단계() {
        String token = createToken("brown@email.com", "password");

        Map<String, String> params1 = new HashMap<>();
        params1.put("date", "2024-03-02");
        params1.put("time", "1");
        params1.put("theme", "1");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertSoftly(soft -> {
            soft.assertThat(response.statusCode()).isEqualTo(201);
            soft.assertThat(response.as(ReservationResponse.class).name()).isEqualTo("브라운");
        });

        Map<String, String> params2 = new HashMap<>();
        params2.put("date", "2024-03-03");
        params2.put("time", "1");
        params2.put("theme", "1");

        ExtractableResponse<Response> adminResponse = RestAssured.given().log().all()
                .body(params2)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertSoftly(soft -> {
            soft.assertThat(response.statusCode()).isEqualTo(201);
            soft.assertThat(adminResponse.as(ReservationResponse.class).name()).isEqualTo("브라운");
        });
    }

    @Test
    void 삼단계() {
        String brownToken = createToken("brown@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", brownToken)
                .get("/admin")
                .then().log().all()
                .statusCode(403);

        String adminToken = createToken("admin@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 육단계() {
        String brownToken = createToken("brown@email.com", "password");

        Map<String, String> params = new HashMap<>();
        params.put("date", "2024-03-01");
        params.put("time", "1");
        params.put("theme", "1");

        // 예약 대기 생성
        WaitingResponse waiting = RestAssured.given().log().all()
                .body(params)
                .cookie("token", brownToken)
                .contentType(ContentType.JSON)
                .post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract().as(WaitingResponse.class);

        // 내 예약 목록 조회
        List<MyReservationResponse> myReservations = RestAssured.given().log().all()
                .body(params)
                .cookie("token", brownToken)
                .contentType(ContentType.JSON)
                .get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", MyReservationResponse.class);

        // 예약 대기 상태 확인
        String status = myReservations.stream()
                .filter(it -> it.id().equals(waiting.id()))
                .filter(it -> !it.status().equals("예약"))
                .findFirst()
                .map(it -> it.status())
                .orElse(null);

        assertThat(status).isEqualTo("1번째 예약대기");
    }

    @Test
    void 칠단계() {
        Component componentAnnotation = JwtTokenProvider.class.getAnnotation(Component.class);
        assertThat(componentAnnotation).isNull();
    }
}
