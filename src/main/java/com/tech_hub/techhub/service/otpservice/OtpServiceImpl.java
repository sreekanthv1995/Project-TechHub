//package com.tech_hub.TechHub.service.otpservice;
//
//
//import com.tech_hub.TechHub.entity.OtpEntity;
//import com.tech_hub.TechHub.entity.UserEntity;
//import com.tech_hub.TechHub.repository.OtpRepository;
//import com.tech_hub.TechHub.service.user.UserServiceImpl;
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.rest.api.v2010.account.MessageCreator;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//
//@Service
//public class OtpServiceImpl {
//
//    @Autowired
//    OtpRepository otpRepository;
//    @Autowired
//    UserServiceImpl userService;
//
//    public void saveOtp(OtpEntity otpEntity){
//        otpRepository.save(otpEntity);
//    }
//    public OtpEntity find(UserEntity user){
//
//        return otpRepository.findByUserEntity(user);
//    }
//    public void delete(OtpEntity otpEntity){
//        otpRepository.delete(otpEntity);
//    }
//
//    public boolean verifyPhoneNumber(String otp,Long phoneNumber){
//
//        UserEntity user = userService.findByPhoneNumber(phoneNumber);
//
//        if (user.isVerified()){
//            return true;
//        }
//        OtpEntity savedOtp = this.find(user);
//        if (savedOtp.otpRequired()){
//            if (otp.equals(savedOtp.getOtp())){
//                user.setVerified(true);
//
//                userService.save(user);
//                this.delete(savedOtp);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void sentOtp(Long phoneNumber){
//
//        UserEntity user = userService.findByPhoneNumber(phoneNumber);
//        OtpEntity savedOtp = this.find(user);
//
//        if (savedOtp != null){
//            if (savedOtp.otpRequired()){
//                return;
//            }else {
//                otpRepository.delete(savedOtp);
//            }
//        }
//
//        String otp = generateRandomOtp();
//        OtpEntity generatedOtp = new OtpEntity();
//        generatedOtp.setUserEntity(user);
//        generatedOtp = otpRepository.save(generatedOtp);
//
//        final String account_sid="AC6987b29c295ea4227f94c55a5dac6da4";
//        final String auth_token="9aab5b793c5b1b623feedd86b4bf09d0";
//        Twilio.init(account_sid,auth_token);
//        PhoneNumber from = new PhoneNumber("+13158963903");
//        PhoneNumber to = new PhoneNumber("+917736331483");
//
//        String message = "Your OTP is ##"+generatedOtp.getOtp();
//
//        MessageCreator messageCreator = new MessageCreator(to,from,message);
//        Message message1 = messageCreator.create();
//        System.out.println("Twilio Message SID: " );
//
//    }
//
//    private String generateRandomOtp() {
//        int otpLength = 6;
//        String allowedChars = "0123456789";
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//
//        for (int i = 0; i < otpLength; i++) {
//            int index = random.nextInt(allowedChars.length());
//            otp.append(allowedChars.charAt(index));
//        }
//        return otp.toString();
//    }
//
//
//}
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
//////    public OtpEntity generateOtp(UserEntity user) {
//////        String otpCode = generateRandomOtp();
//////        LocalDateTime expirationTime = java.time.LocalDateTime.now().plusMinutes(5);
//////        OtpEntity otpEntity = new OtpEntity();
//////        otpEntity.setOtp(otpCode);
//////        otpEntity.setExpirationTime(expirationTime);
//////        otpEntity.setUserEntity(user);
//////
//////        return otpRepository.save(otpEntity);
//////    }
//////
//////    @Override
//////    public boolean verifyOtp(Long phoneNumber, String otpCode) {
//////        return false;
//////    }
//////
//////
//////    @Override
//////    public Optional<OtpEntity> findByUser(UserEntity user) {
//////        return otpRepository.findByUserEntityAndOtp(user);
//////    }
//////
//////    private String generateRandomOtp() {
//////        int otpLength = 6;
//////        String allowedChars = "0123456789";
//////        Random random = new Random();
//////        StringBuilder otp = new StringBuilder();
//////
//////        for (int i = 0; i < otpLength; i++) {
//////            int index = random.nextInt(allowedChars.length());
//////            otp.append(allowedChars.charAt(index));
//////        }
//////        return otp.toString();
//////    }
////
//////    @Override
//////    public OtpEntity findByPhoneNumber(Long phoneNumber) {
//////        return otpRepository.findByPhoneNumber(phoneNumber);
//////    }
//////    @Override
//////    public boolean verifyOtp(Long phoneNumber, String otpCode) {
////////        Optional<OtpEntity> otpEntityOptional = otpRepository.findByUserEntityAndOtp(user,otpCode);
//////        Optional<UserEntity> user = userService.findByPhoneNumber(phoneNumber);
//////
//////
//////
//////
//////        if (otpEntityOptional.isPresent()) {
//////            OtpEntity otpEntity = otpEntityOptional.get();
//////            if (otpEntity.getOtp().equals(otpCode) && !otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
//////                return true;
//////            }
//////        }
//////        return false;
//////    }
////
//////    public boolean sendOtpAndGenerate(UserEntity user) {
//////
//////        String otpCode = generateRandomOtp();
//////        boolean otpSent = sendOtpViaTwilio(user.getPhoneNumber(),otpCode);
//////        if (otpSent) {
//////            // Save OTP details in the database
//////            saveOtpDetails(user, otpCode);
//////            return true;
//////        } else {
//////            return false;
//////        }
//////
//////    }
////
//////    private void saveOtpDetails(UserEntity user, String otpCode) {
//////        OtpEntity otpEntity = new OtpEntity();
//////        otpEntity.setUserEntity(user);
//////        otpEntity.setOtp(otpCode);
//////        // Set expiration time as needed
//////        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5)); // For example, OTP is valid for 5 minutes
//////        otpRepository.save(otpEntity);
//////    }
////
//////    private boolean sendOtpViaTwilio(String phoneNumber, String otpCode) {
//////        try {
//////            String messageContent = "Your OTP is: " + otpCode;
//////            twilioService.sendSms(phoneNumber, messageContent);
//////            return true;
//////        } catch (Exception e) {
//////            e.printStackTrace();
//////            return false;
//////        }
//////    }
////
////}
////
////
//
//
//
//
//
//
////    @Autowired
////    private TwilioConfig twilioConfig;
////    Map<String, String> otpMap = new HashMap<>();
////
////
////    public OtpResponseDto sendSMS(OtpRequest otpRequest) {
////        OtpResponseDto otpResponseDto = null;
////        try {
////            PhoneNumber to = new PhoneNumber(otpRequest.getPhoneNumber());//to
////            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber()); // from
////            String otp = generateOTP();
////            String otpMessage = "Dear Customer , Your OTP is  " + otp + " for sending sms through Spring boot application. Thank You.";
////            Message message = Message
////                    .creator(to, from,
////                            otpMessage)
////                    .create();
////            otpMap.put(otpRequest.getUsername(), otp);
////            otpResponseDto = new OtpResponseDto(OtpStatus.DELIVERED, otpMessage);
////        } catch (Exception e) {
////            e.printStackTrace();
////            otpResponseDto = new OtpResponseDto(OtpStatus.FAILED, e.getMessage());
////        }
////        return otpResponseDto;
////    }
////
////    public String validateOtp(OtpValidationRequest otpValidationRequest) {
////        Set<String> keys = otpMap.keySet();
////        String username = null;
////        for(String key : keys)
////            username = key;
////        if (otpValidationRequest.getUsername().equals(username)) {
////            otpMap.remove(username,otpValidationRequest.getOtpNumber());
////            return "OTP is valid!";
////        } else {
////            return "OTP is invalid!";
////        }
////    }
////
////    private String generateOTP() {
////        return new DecimalFormat("000000")
////                .format(new Random().nextInt(999999));
////    }
