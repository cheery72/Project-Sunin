package com.ssafy.sunin.controller;

import com.ssafy.sunin.domain.user.User;
import com.ssafy.sunin.user.UserRequest;
import com.ssafy.sunin.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ApiOperation(value="가입성공 여부에 따라 http상태로 반환해서 알려줌")
    public ResponseEntity signup(@RequestBody UserRequest request) {
        if(userService.signup(request).equals("Success")) {
            return new ResponseEntity(HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    @ApiOperation(value="로그인 성고여부 반환")
    public ResponseEntity login(@RequestBody UserRequest request) {
//    	log.info("userId = {}, password = {}", request.getUser_email(), request.getUser_password());
        if(userService.login(request.getUserId(), request.getUser_password()).equals("Success")) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{userId}")
    @ApiOperation(value="회원탈퇴")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }

    @GetMapping("/searchAll")
    @ApiOperation(value="모든 회원 정보 조회")
    public ResponseEntity<List<User>> listuser() throws Exception{
        return new ResponseEntity<>(userService.listUser(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @ApiOperation(value="회원정보를 가져온다")
    public ResponseEntity<User> detailUser(@PathVariable String userId) {
        return new ResponseEntity<>(userService.detailUser(userId), HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value="회원정보 수정")
    public ResponseEntity updateUser(@RequestBody UserRequest request) {
        if(userService.updateUser(request).equals("Success")) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
