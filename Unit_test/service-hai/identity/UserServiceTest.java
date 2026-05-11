package com.DATN.Bej.service.identity;

import com.DATN.Bej.dto.request.identityRequest.AuthenticationRequest;
import com.DATN.Bej.dto.request.identityRequest.UserCreationRequest;
import com.DATN.Bej.dto.request.identityRequest.UserUpdateRequest;
import com.DATN.Bej.dto.response.UserResponse;
import com.DATN.Bej.dto.response.identity.AuthenticationResponse;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.UserMapper;
import com.DATN.Bej.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuthenticationService authenticationService;
    
    /**
     * Test of createUser method, of class UserService. Test_case_ID:
     * Register_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testCreateUser_test1() {
        String phoneNumber="0987656789";
        String password="123456";
        String fullName="Hoang Hai";
        String email="hoanghai100@gmail.com";
        UserCreationRequest request = new UserCreationRequest(phoneNumber, password, fullName, email);
        UserResponse result = userService.createUser(request);
        
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findById(result.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u.getEmail(), email);
        assertEquals(u.getFullName(), fullName);
        assertEquals(u.getPhoneNumber(), phoneNumber);
    }

    /**
     * Test of createUser method, of class UserService. Test_case_ID:
     * Register_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testCreateUser_test2() {
        String phoneNumber="0967672862";
        String password="123456";
        String fullName="Hoàng Hải";
        String email="hoanghai100@gmail.com";
        int numberUser=userRepository.findAll().size();
        try{
            UserCreationRequest request = new UserCreationRequest(phoneNumber, password, fullName, email);
            userService.createUser(request);   
        } catch(AppException e){
            assertEquals(numberUser, userRepository.findAll().size());
            System.out.println("Đăng kí trùng lặp số điện thoại thất bại");
        }
    }

    /**
     * Test of createUser method, of class UserService. Test_case_ID:
     * Register_003
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testCreateUser_test3() {
        String phoneNumber="0987656789";
        String password="123456";
        String fullName="Hoàng Hải";
        String email="hoanghaidinh1124@gmail.com";
        int numberUser=userRepository.findAll().size();
        try{
            UserCreationRequest request = new UserCreationRequest(phoneNumber, password, fullName, email);
            userService.createUser(request);   
        } catch(AppException e){
            assertEquals(numberUser, userRepository.findAll().size());
            System.out.println("Đăng kí trùng lặp email thất bại");
        }
    }

    /**
     * Test of createUser method, of class UserService. Test_case_ID:
     * Register_004
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testCreateUser_test4() {
        String phoneNumber="0987656789";
        String password="123456";
        String fullName="Hoàng Hải";
        String email="hoanghaidinh1124";
        int numberUser=userRepository.findAll().size();
        try{
            UserCreationRequest request = new UserCreationRequest(phoneNumber, password, fullName, email);
            userService.createUser(request);   
        } catch(Exception e){
            System.out.println("Đăng kí trùng lặp email thất bại");
        } finally {
            assertEquals(numberUser, userRepository.findAll().size());
            System.out.println("Đăng kí trùng lặp email thất bại");
        }
    }

    /**
     * Test of getMyInfo method, of class UserService. Test_case_ID:
     * GET_MY_INFO_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testGetMyInfo_test1() {
        String sdt="0967672862";
        String password="123456";
        
        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        authenticationService.authenticate(request);
        
        UserResponse result = userService.getMyInfo();
        System.out.println(result.getFullName());
        System.out.println(result.getId());
    }

    /**
     * Test of getMyInfo method, of class UserService. Test_case_ID:
     * GET_MY_INFO_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testGetMyInfo_test2() {
        String sdt="0987656789";
        String password="123456";
        
        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        authenticationService.authenticate(request);
        
        UserResponse result = userService.getMyInfo();
        System.out.println(result.getFullName());
        System.out.println(result.getId());
    }

    /**
     * Test of updateMyInfo method, of class UserService. Test_case_ID:
     * UPDATE_MY_INFO_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateMyInfo_test1() {
        String fullName="Hoàng Hải";
        String password="012345";
        String address="Hà Nội";
        String phoneNumber="0967672862";
        String email="hoanghaidinh1124@gmail.com";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        userService.updateMyInfo(request);
        
        // Lay ra de kiem tra
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u.getEmail(), email);
        assertEquals(u.getFullName(), fullName);
        assertEquals(u.getPhoneNumber(), phoneNumber);
        assertEquals(u.getDob(), dob);
        assertEquals(u.getAddress(), address);
        assertEquals(u.getRoles(), roles);
        
        // Kiem tra mat khau
        AuthenticationRequest authenRequest = new AuthenticationRequest(phoneNumber, password);
        assertNotNull(authenticationService.authenticate(authenRequest).getToken());
    }

    /**
     * Test of updateMyInfo method, of class UserService. Test_case_ID:
     * UPDATE_MY_INFO_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateMyInfo_test2() {
        String fullName="Hoàng Hải";
        String password=null;
        String address="Hà Nội";
        String phoneNumber="0967672862";
        String email="hoanghaidinh1124@gmail.com";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        userService.updateMyInfo(request);
        
        // Lay ra de kiem tra
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u.getEmail(), email);
        assertEquals(u.getFullName(), fullName);
        assertEquals(u.getPhoneNumber(), phoneNumber);
        assertEquals(u.getDob(), dob);
        assertEquals(u.getAddress(), address);
        assertEquals(u.getRoles(), roles);
        
        // Kiem tra mat khau
        AuthenticationRequest authenRequest = new AuthenticationRequest(phoneNumber, password);
        assertNotNull(authenticationService.authenticate(authenRequest).getToken());
    }

    /**
     * Test of updateMyInfo method, of class UserService. Test_case_ID:
     * UPDATE_MY_INFO_003
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateMyInfo_test3() {
        String fullName="Hoàng Hải";
        String password="012345";
        String address="Hà Nội";
        String phoneNumber="0999999999";
        String email="abc123@gmail.com";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        userService.updateMyInfo(request);
        
        // Lay ra de kiem tra
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u.getEmail(), email);
        assertEquals(u.getFullName(), fullName);
        assertEquals(u.getPhoneNumber(), phoneNumber);
        assertEquals(u.getDob(), dob);
        assertEquals(u.getAddress(), address);
        assertEquals(u.getRoles(), roles);
        
        // Kiem tra mat khau
        AuthenticationRequest authenRequest = new AuthenticationRequest(phoneNumber, password);
        assertNotNull(authenticationService.authenticate(authenRequest).getToken());
    }

    /**
     * Test of updateMyInfo method, of class UserService. Test_case_ID:
     * UPDATE_MY_INFO_004
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateMyInfo_test4() {
        String fullName="Hoàng Hải";
        String password="012345";
        String address="Hà Nội";
        String phoneNumber="0999999999";
        String email="dttd@gmail.com";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        userService.updateMyInfo(request);
        
        // Lay ra de kiem tra
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u.getEmail(), email);
        assertEquals(u.getFullName(), fullName);
        assertEquals(u.getPhoneNumber(), phoneNumber);
        assertEquals(u.getDob(), dob);
        assertEquals(u.getAddress(), address);
        assertEquals(u.getRoles(), roles);
        
        // Kiem tra mat khau
        AuthenticationRequest authenRequest = new AuthenticationRequest(phoneNumber, password);
        assertNotNull(authenticationService.authenticate(authenRequest).getToken());
    }
}
