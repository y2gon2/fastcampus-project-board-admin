package com.fastcampus.projectboardadmin.controller.advice;


import com.fastcampus.projectboardadmin.service.VisitCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


// 모든 view Endpoint 에 해당 attribute 를 공통으로 내려줌  (전역 변수처럼 사용???)
@RequiredArgsConstructor
@ControllerAdvice
public class VisitCounterControllerAdvice {

    private final VisitCounterService visitCounterService;


    @ModelAttribute("visitCount")
    public Long visitCount() {
        return visitCounterService.visitCount();
    }
}
