package com.fastcampus.projectboardadmin.service;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VisitCounterService {

    private final MeterRegistry meterRegistry; //  Java 애플리케이션의 성능 메트릭을 측정하고 모니터링 툴과 연동하기 위한 라이브러리 https://micrometer.io/

    // localhost:8081/actuator/metrics/http.server.requests
    // -> "tag": "uri", "value": [view page, actuator, rest api 작업 등을 수행하기 위해 정의된 많은 uri 에 대한 list]
    //     uri 리스트 중에서, view pages ("/admin/members", "/management/articles", "/management/article-comments", "/management/userAccounts")
    //     위 네곳 만 filtering 해서 보갰다.
    private static final List<String> viewEndpoints = List.of(
            "/management/articles",
            "/management/article-comments",
            "/management/user-accounts",
            "/admin/members"
    );
    public long visitCount() {

        long sum;

        // 최초 application 을 실행했을 때, 아래 get("http.server.requests") 의 값은 존재하지 않아
        // 이때 error 가 발생한다.
        // 이를 해결하기 위해 MeterNotFoundExpection error catch 가 필요하다.
        // http://localhost:8081/actuator/metrics  - "names" -> http.server.requests
        try {
            sum = meterRegistry.get("http.server.requests")
                    .timers()
                    .stream()
                    .filter(timer -> viewEndpoints.contains(timer.getId().getTag("uri")))
                    .mapToLong(Timer::count)
                    .sum();
        } catch (MeterNotFoundException e) {
            sum = 0L;
        }

        return sum;
    }
}
