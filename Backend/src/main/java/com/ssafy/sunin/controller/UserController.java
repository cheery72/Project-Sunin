package com.ssafy.sunin.controller;

import com.ssafy.sunin.domain.user.User;
import com.ssafy.sunin.user.JwtTokenProvider;
import com.ssafy.sunin.user.UserRequest;
import com.ssafy.sunin.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {



    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private final JwtTokenProvider jwtTokenProvider;
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = null;
        try {
            User loginuser = userService.login(request.getUserId(), request.getUserPassword());
//            System.out.println(loginuser.getUserId());
            if(loginuser!=null){
                String token = jwtTokenProvider.createToken(loginuser.getUserId(), loginuser.getRoles());
                System.out.println(token);
                resultMap.put("access-token", token);
                resultMap.put("message", SUCCESS);
                status =  HttpStatus.ACCEPTED;
            }
            else{
                resultMap.put("message", FAIL);
                status = HttpStatus.ACCEPTED;
            }
        }
        catch (Exception e){
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Map<String,Object>>(resultMap, status);

//    	if(userService.login(request.getUserId(), request.getUser_password()).equals("Success")) {
//    		return new ResponseEntity(HttpStatus.OK);
//    	}
//    	return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
