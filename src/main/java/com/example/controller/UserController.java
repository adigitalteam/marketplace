package com.example.controller;

import com.example.dto.GetTokenDTO;
import com.example.dto.LoginDTO;
import com.example.dto.MeDTO;
import com.example.exceptions.AppException;
import com.example.security.jwt.JwtUtils;
import com.example.service.AuthUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/v1/oauth/users")
public class UserController {

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/me")
    public MeDTO me(@RequestHeader("Authorization") String Authorization) throws IOException, AppException {
        String tokenValue = Authorization.replace("Bearer", "").trim();
        return jwtUtils.getMe(tokenValue);
    }


    @PostMapping("/login")
    public GetTokenDTO login(@Valid @RequestBody LoginDTO loginDTO) throws AppException, IOException {
        return authUserService.login(loginDTO);
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/logout")
    public GetTokenDTO logout(@RequestHeader("Authorization") String Authorization) throws IOException {
        String tokenValue = Authorization.replace("Bearer", "").trim();
        return authUserService.logout(tokenValue);
    }

    @PostMapping("/example")
    public String example() {
        return "example";
    }


}
