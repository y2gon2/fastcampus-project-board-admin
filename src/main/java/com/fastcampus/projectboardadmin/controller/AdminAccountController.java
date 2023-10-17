package com.fastcampus.projectboardadmin.controller;

import com.fastcampus.projectboardadmin.dto.response.AdminAccountResponse;
import com.fastcampus.projectboardadmin.service.AdminAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @RequestMapping("/admin/members")
    public String adminAccount (Model model) {
        return "admin/members";
    }

    // 관리자 정보 view 에서는 JsGrid 를 사용하여
    // Table 상태에서 특정 정보 (ex. email, 권한) 등을 수정할 수 있다.
    // 이를 위해서 다른 view 들과 다른 REST 작업 method 가 필요하다.

    // @ResponseBody (by chatGPT)
    // @ResponseBody 어노테이션은 Spring MVC에서 사용되며, 컨트롤러의 메서드가 반환하는 값을
    // HTTP 응답 본문(body)으로 직접 작성하도록 지시
    //
    // 기본적으로 Spring MVC의 컨트롤러 메서드는 뷰 이름(view name)을 반환합니다.
    // 예를 들어, Thymeleaf, JSP, FreeMarker와 같은 뷰 템플릿을 사용하여 UI를 생성할 때
    // 해당 뷰 이름에 따라 특정 템플릿이 선택되고 렌더링됩니다.
    //
    // 그러나 때로는 클라이언트에게 JSON, XML과 같은 형태의 데이터를 직접 반환해야 할 필요가 있습니다.
    // 이러한 경우에 @ResponseBody를 사용하여 메서드가 반환하는 객체나 값이
    // HTTP 응답 본문으로 직접 변환되도록 할 수 있습니다.
    //
    // Spring에서 @ResponseBody는 내부적으로 HttpMessageConverter를 사용하여 반환된 객체를
    // HTTP 응답 본문으로 변환합니다. 예를 들어, Jackson 라이브러리가 classpath에 포함되어 있으면
    // 반환된 객체는 기본적으로 JSON 형식으로 변환됩니다.
    //
    // 아래 코드에서 getMembers 메서드는 List<AdminAccountResponse>를 반환합니다.
    // @ResponseBody 어노테이션 덕분에 이 리스트는 클라이언트에게 JSON 배열로 변환되어 반환됩니다.
    //
    // @RestController 어노테이션을 사용하여 컨트롤러를 선언하면, 해당 컨트롤러의 모든 메서드에 자동으로
    // @ResponseBody가 적용됩니다. 따라서 이 경우 별도로 @ResponseBody를 메서드에 붙일 필요가 없습니다.
    @ResponseBody
    @GetMapping("/api/admin/members")
    public List<AdminAccountResponse> getMembers() {
        return List.of();
//        return adminAccountService.users().stream()
//                .map(AdminAccountResponse::from)
//                .toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT) // 응답 HttpStatus 를 특정 code 로 지정하기 위해 사용? (NO_CONTENT - 204 (delete 에 주로 사용), OK - 200 (default) CREATED - 200 (insert))
    @ResponseBody
    @DeleteMapping("/api/admin/members/{userId}")
    public void delete(@PathVariable String userId) {
//        adminAccountService.deleteUser(userId);
    }
}
