package com.DATN.Bej.service.identity;

import com.DATN.Bej.dto.request.identityRequest.AuthenticationRequest;
import com.DATN.Bej.dto.request.identityRequest.IntrospectRequest;
import com.DATN.Bej.dto.request.identityRequest.UserCreationRequest;
import com.DATN.Bej.dto.request.identityRequest.UserUpdateRequest;
import com.DATN.Bej.dto.response.UserResponse;
import com.DATN.Bej.dto.response.identity.AuthenticationResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.UserMapper;
import com.DATN.Bej.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest 
public class AuthenticationServiceTest {
    
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserManageService userManageService;

    
    /**
     * Test of authenticate method, of class AuthenticationService.
     * Test case ID: LOGIN_001
     */
    @Test
    @Rollback(true)
    public void testAuthenticate_test1() {
        String sdt="admin";
        String password="admin";
        
        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        AuthenticationResponse result = authenticationService.authenticate(request);
        
        assertNotNull(result.getToken());
    }
    
     /**
     * Test of authenticate method, of class AuthenticationService.
     * Test case ID: LOGIN_002
     */
    @Test
    @Rollback(true)
    public void testAuthenticate_test2() {
        String sdt="admin";
        String password="12345678";

        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        try{
            authenticationService.authenticate(request);
        }catch(AppException e){
            System.out.println("Sai tài khoản mật khẩu");
        }
    }

        /**
     * Test of authenticate method, of class AuthenticationService.
     * Test case ID: LOGIN_003
     */
    @Test
    @Rollback(true)
    public void testAuthenticate_test3() {
        String sdt="0123456789";
        String password="123";

        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        try{
            authenticationService.authenticate(request);
        }catch(AppException e){
            System.out.println("Sai tài khoản mật khẩu");
        }
    }

    /**
     * Test of logout method, of class AuthenticationService.
     * Test case ID: Logout_001
     */
    @Test
    @Rollback(true)
    public void testLogout() throws ParseException, JOSEException{
        String sdt="admin";
        String password="admin";
        
        AuthenticationRequest request = new AuthenticationRequest(sdt, password);
        AuthenticationResponse result = authenticationService.authenticate(request);
        
        String token = result.getToken();
        
        IntrospectRequest logoutRequest = new IntrospectRequest(token);
        String logoutResult = authenticationService.logout(logoutRequest);
        assertNotNull(logoutResult);
    }   
    
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