package com.DATN.Bej.service.identity;

import com.DATN.Bej.dto.request.identityRequest.AuthenticationRequest;
import com.DATN.Bej.dto.request.identityRequest.UserUpdateRequest;
import com.DATN.Bej.dto.response.UserResponse;
import com.DATN.Bej.dto.response.identity.RoleResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.UserMapper;
import com.DATN.Bej.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.AssertFalse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class UserManageServiceTest {
    @Autowired
    UserManageService userManageService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserMapper userMapper;
    
    /**
     * Test of findByRole method, of class UserManageService.
     * Test_case_ID: FIND_BY_ROLE_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testFindByRole_test1() {
        AuthenticationRequest authenRequest = new AuthenticationRequest("admin", "admin");
        authenticationService.authenticate(authenRequest);
        System.out.println("findByRole");
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        
        List<UserResponse> listResult = userManageService.findByRole(roles);
        assertEquals(11, listResult.size());
    }

    /**
     * Test of findByRole method, of class UserManageService.
     * Test_case_ID: FIND_BY_ROLE_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testFindByRole_test2() {
        AuthenticationRequest authenRequest = new AuthenticationRequest("admin", "admin");
        authenticationService.authenticate(authenRequest);
        System.out.println("findByRole");
        List<String> roles = new ArrayList<>();
        roles.add("SHOP_MANAGER");
        
        List<UserResponse> listResult = userManageService.findByRole(roles);
        assertEquals(2, listResult.size());
    }
 
    /**
     * Test of updateUser method, of class UserManageService.
     * Test_case_ID: UPDATE_USER_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateUser_test1() {
        String fullName="Hoàng Hải";
        String password=null;
        String address="Hà Nội";
        String phoneNumber="0967672862";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("SHOP_MANAGER");
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
 
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        
        UserResponse result = userManageService.updateUser(u.getId(), request);
        
        //Lay ra de kiem tra
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u, result);
    }
    
    /**
     * Test of updateUser method, of class UserManageService.
     * Test_case_ID: UPDATE_USER_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateUser_test2() {
        String fullName="Hoàng Hải";
        String password=null;
        String address="Hà Nội";
        String phoneNumber="0967672862";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("SHOP_MANAGER");
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));

        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        
        UserResponse result=null;
        try{
            result = userManageService.updateUser(u.getId(), request);
        } catch (AppException e){
            assertNull(result);
        } finally{
            assertNull(result);
        }
    }
    
    /**
     * Test of updateUser method, of class UserManageService.
     * Test_case_ID: UPDATE_USER_003
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateUser_test3() {
        String fullName="Hoàng Hải";
        String password=null;
        String address="Hà Nội";
        String phoneNumber="0967672862";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("SHOP_MANAGER");

        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        
        UserResponse result=null;
        try{
            result = userManageService.updateUser("123", request);
        } catch (AppException e){
            assertNull(result);
        } finally{
            assertNull(result);
        }
    }
    
    /**
     * Test of updateUser method, of class UserManageService.
     * Test_case_ID: UPDATE_USER_004
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateUser_test4() {
        String fullName="Hoàng Hải";
        String password=null;
        String address="Hà Nội";
        String phoneNumber="0967672862";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("1234");

        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        
        UserResponse result=null;
        try{
            result = userManageService.updateUser("123", request);
        } catch (AppException e){
            assertNull(result);
        } finally{
            assertNull(result);
        }
    }
    
    /**
     * Test of updateUser method, of class UserManageService.
     * Test_case_ID: UPDATE_USER_005
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testUpdateUser_test5() {
        String fullName="Hoàng Hải";
        String password="1234567";
        String address="Hà Nội";
        String phoneNumber="0967672862";
        int year=2000;
        int month=2;
        int day=15;
        var dob = LocalDate.of(year, month, day);
        List<String> roles = new ArrayList<>();
        roles.add("SHOP_MANAGER");
        UserResponse u;
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
 
        UserUpdateRequest request = new UserUpdateRequest(fullName, password, address, dob, phoneNumber, roles);
        
        UserResponse result = userManageService.updateUser(u.getId(), request);
        
        //Lay ra de kiem tra
        u = userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        
        assertEquals(u, result);
    }

    /**
     * Test of searchUserByPhoneNumber method, of class UserManageService.
     * Test_case_ID: SEARCH_BY_PHONE_NUMBER_001
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testSearchUserByPhoneNumber_test1() {
        System.out.println("searchUserByPhoneNumber");
        String phoneNumber = "0967672862";
        UserResponse result = userManageService.searchUserByPhoneNumber(phoneNumber);
        assertNotNull(result);
    }
    
    /**
     * Test of searchUserByPhoneNumber method, of class UserManageService.
     * Test_case_ID: SEARCH_BY_PHONE_NUMBER_002
     */
    @Test
    @Rollback(true)
    @Transactional
    public void testSearchUserByPhoneNumber_test2() {
        System.out.println("searchUserByPhoneNumber");
        String phoneNumber = "0999999999";
        UserResponse result=null;
        try{
            result = userManageService.searchUserByPhoneNumber(phoneNumber); 
        } catch (AppException e){
            assertNull(result);
        }
    }
}


