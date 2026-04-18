/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unitTest;
import junit.framework.TestCase;
import com.DATN.Bej.controller.identity.AuthenticationController;
import com.DATN.Bej.service.FcmDeviceTokenService;
import com.DATN.Bej.service.identity.AuthenticationService;
import com.DATN.Bej.service.identity.UserService;
/**
 *
 * @author dinhh
 */
public class XacThucVaPhanQuyen extends TestCase{
    
    public void test_dangnhapdung (){
        AuthenticationService authenticationService;
        FcmDeviceTokenService fcmDeviceTokenService;
        UserService userService;
        
        AuthenticationController a = new AuthenticationController(authenticationService, fcmDeviceTokenService, userService);
        authenticationService.authenticate(request)
        
    }
    
    public void test_dangnhapsai() {
        
    }
    
    public void test_dangki() {
        
    }
    
    public void test_dangki_trung_email(){
        
    }
    
    public void test_chinhsuathongtin() {
        
    }
    
    public void test_dangxuat (){
        
    }
    
    public void test_admin_chinhsuanhanvien() {
        
    }
    
    public void test_admin_chinhsuanguoidung(){
        
    }
}
