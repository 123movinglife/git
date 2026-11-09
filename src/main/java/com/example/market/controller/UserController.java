package com.example.market.controller;

import com.example.market.common.Result;
import com.example.market.dto.LoginDTO;
import com.example.market.dto.RegisterDTO;
import com.example.market.entity.User;
import com.example.market.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto){
        userService.register(dto);
        return Result.ok();
    }
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO dto){
        return Result.ok(userService.login(dto));   //dto是token
    }
    //需要登录才能访问,userId 由拦截器塞进request
    @GetMapping("/profile")
    public Result<User> profile(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(userService.getById(userId));
    }
}
