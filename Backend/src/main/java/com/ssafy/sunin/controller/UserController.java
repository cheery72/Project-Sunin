package com.ssafy.sunin.controller;

import com.ssafy.sunin.common.ApiResponse;
import com.ssafy.sunin.domain.user.User;
import com.ssafy.sunin.dto.ImageVO;
import com.ssafy.sunin.dto.UserProfile;
import com.ssafy.sunin.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.spi.FileTypeDetector;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
//@RequestMapping("user")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value="user 반환")
    @GetMapping
    public ApiResponse getUser() {

        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUser(principal.getUsername());
        return ApiResponse.success("user", user);
    }

    @ApiOperation(value = "유저 프로필 조회")
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long id){
        log.info("getUserProfile");
        if(ObjectUtils.isEmpty(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @ApiOperation(value = "유저 프로필 사진 수정")
    @PutMapping("/image")
    public ResponseEntity<UserProfile> updateUserImage(@Valid ImageVO imageVO){
        log.info("updateUserImage");
        if(ObjectUtils.isEmpty(imageVO)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.updateUserImage(imageVO));
    }

    @ApiOperation(value = "유저 프로필 사진 삭제")
    @DeleteMapping("image/{id}")
    public ResponseEntity<UserProfile> deleteUserImage(@PathVariable Long id){
        log.info("deleteUserImage");
        if(ObjectUtils.isEmpty(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.delteUserImage(id));
    }

//    @PostMapping("/signup")
//    @ApiOperation(value="회원가입", notes="가입성공 여부에 따라 http상태로 반환해서 알려줌")
//    public ResponseEntity<String> signup(@RequestBody UserRequest request) {
//        if(userService.signup(request).equals("Success")) {
//            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
//        }
//        return new ResponseEntity<>("회원가입 실패", HttpStatus.OK);
//    }
//
//    @PostMapping("/login")
//    @ApiOperation(value="로그인", notes = "이메일과 비밀번호로 로그인을 시도합니다.")
//    public ResponseEntity<User> login(@RequestBody UserRequest request) {
//            User loginuser = userService.login(request.getUserId(), request.getUser_password());
//
//            if(loginuser != null) return new ResponseEntity<>(loginuser, HttpStatus.OK);
//            return new ResponseEntity<>(null, HttpStatus.OK);
//    }
//
//    @GetMapping("/logout")
//    public Map<String, Object> logout(HttpSession session){
//        session.invalidate();
//        Map<String, Object> resultMap = new HashMap<>();
//
//        resultMap.put("status", true);
//        resultMap.put("msg", "로그아웃 성공");
//        return resultMap;
//
//    }
//
//    @DeleteMapping("/delete/{userId}")
//    @ApiOperation(value="회원탈퇴")
//    public ResponseEntity deleteUser(@PathVariable String userId) {
//        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
//    }
//
//    @GetMapping("/searchAll")
//    @ApiOperation(value="모든 회원 정보 조회")
//    public ResponseEntity<List<User>> listuser() throws Exception{
//        return new ResponseEntity<>(userService.listUser(), HttpStatus.OK);
//    }
//
//    @GetMapping("/{userId}")
//    @ApiOperation(value="회원정보를 가져온다")
//    public ResponseEntity<User> detailUser(@PathVariable String userId) {
//        return new ResponseEntity<>(userService.detailUser(userId), HttpStatus.OK);
//    }
//
//    @PutMapping
//    @ApiOperation(value="회원정보 수정")
//    public ResponseEntity updateUser(@RequestBody UserRequest request) {
//        if(userService.updateUser(request).equals("Success")) {
//            return new ResponseEntity(HttpStatus.OK);
//        }
//        return new ResponseEntity(HttpStatus.BAD_REQUEST);
//    }
}
