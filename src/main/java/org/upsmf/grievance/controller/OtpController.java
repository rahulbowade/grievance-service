package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.model.OtpRequest;
import org.upsmf.grievance.model.OtpValidationRequest;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.service.OtpService;
import org.upsmf.grievance.service.TicketService;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate-otp")
    public void generateOtp(@RequestBody OtpRequest otpRequest) {
        otpService.generateAndSendOtp(otpRequest);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        boolean isValid = otpService.validateOtp(otpValidationRequest.getEmail(), otpValidationRequest.getOtp());
        if (isValid) {
            return ResponseEntity.ok("OTP validation successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }


}
