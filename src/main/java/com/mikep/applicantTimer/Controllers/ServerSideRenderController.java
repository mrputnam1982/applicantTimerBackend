package com.mikep.applicantTimer.Controllers;

import com.mikep.applicantTimer.Services.MyUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/auth/")
@Log4j2
public class ServerSideRenderController {
    @Autowired
    MyUserDetailsService userDetailsService;

    @GetMapping("verify")
    public String verifyUser(@Param("code") String code) {
        if (userDetailsService.verify(code)) {
            return "verify_success";

        } else return "verify_fail";
    }
}
