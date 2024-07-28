//package com.tech_hub.TechHub.service.otpservice;
//
//import com.tech_hub.TechHub.configuration.TwilioConfig;
//import com.twilio.http.TwilioRestClient;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TwilioService {
//
//    @Autowired
//    private TwilioConfig twilioConfig;
//    @Autowired
//    TwilioRestClient twilioRestClient;
//
//
//    public void sendSms(String phoneNumber, String messageBody){
//        Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(twilioConfig.getTrialNumber()),
//                messageBody
//        ).create(twilioRestClient);
//    }
//
//
//}
