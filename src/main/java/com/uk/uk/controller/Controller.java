package com.uk.uk.controller;

import com.uk.uk.entity.AuthEntity;
import com.uk.uk.entity.DashboardGridDataDAO;
import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.entity.TempUser;
import com.uk.uk.implementation.*;
import com.uk.uk.repository.AuthRepository;
import com.uk.uk.repository.TempUserRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uk.uk.repository.PricingInsightsRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
public class Controller {
    @Autowired
    private PricingInsightsRepo ProductInsightsRepo;
    @Autowired
    private AsdaImpl AsdaImpl;
    @Autowired
    private MorrisonsImpl MorrisonsImpl;
    @Autowired
    private SainsburysImpl SainsburysImpl;
    @Autowired
    private TescoImpl TescoImpl;
    @Autowired
    private ProductMasterDataImpl ProductMasterDataImpl;
    @Autowired
    private PricingInsightsImpl PricingInsightsImpl;
    @Autowired
    private WaitRoseImpl WaitRoseImpl;
    @Autowired
    private AmazonImpl AmazonImpl;
    @Autowired
    private OcadoImpl OcadoImpl;
    @Autowired
    private CoOpImpl CoOpImpl;
    @Autowired
    private AmazonTempImpl AmazonTempImpl;

    @Autowired
    EmailService mailService;

    @Autowired
    TempUserRepo TempRepo;

    @Autowired
    AuthRepository authRepository;

    @GetMapping("/insertPricingInsights")
    public Boolean insertPricingInsights() throws IOException, InterruptedException {
        MorrisonsImpl.getProductDetails();
        SainsburysImpl.getProductDetails();
        TescoImpl.getProductDetails();
        WaitRoseImpl.getProductDetails();
//        AmazonImpl.getProductDetails();
        AmazonTempImpl.getProductDetails();
        OcadoImpl.getProductDetails();
        CoOpImpl.getProductDetails();
        AsdaImpl.getProductDetails();
        return true;
    }

    @PostMapping("/insertProductMasterData")
    public Boolean insertProductMasterData(@RequestBody List<ProductMasterDataDAO> ProductMasterDataDAOList) throws IOException, InterruptedException {
        ProductMasterDataImpl.insertProductMasterData(ProductMasterDataDAOList);
        return true;
    }

    @GetMapping("/getGridData")
    public List<DashboardGridDataDAO> getUser() {
        List<DashboardGridDataDAO> gridDataList = new ArrayList<>();
        gridDataList = PricingInsightsImpl.getGridData();
        return gridDataList;
    }

    @GetMapping("/getProductMasterByTag")
    public List<ProductMasterDataDAO> getProductMasterByTag(@RequestParam("tag") Integer tag) {
        List<ProductMasterDataDAO> productMasterDataDAOList = new ArrayList<>();
        productMasterDataDAOList = PricingInsightsImpl.getProductMasterByTag(tag);
        return productMasterDataDAOList;
    }

    @PostMapping("/editProductMasterByTag")
    public Boolean editProductMasterByTag(@RequestBody List<ProductMasterDataDAO> ProductMasterDataDAOList) throws InterruptedException {
        Boolean updateStatus = false;
        updateStatus = ProductMasterDataImpl.updateProductMasterByTag(ProductMasterDataDAOList);
        return updateStatus;
    }

    @GetMapping("/hideProductByTag")
    public Boolean hideProductByTag(@RequestParam("tag") Integer tag) {
        ProductMasterDataImpl.hideProductByTag(tag);
        PricingInsightsImpl.deletePricingInsightsByTag(tag);
        return true;
    }

    @GetMapping("/test")
    public Boolean test() throws IOException, InterruptedException {
//        AmazonTempImpl.getProductDetails();
        SainsburysImpl.test();
        return true;
    }


    @GetMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String password) throws MessagingException {
        //mailService.sendSimpleEmail(to, subject, text)
        String otp = generateOTP(6);
        String subject = "Your OTP Code";
        String text = "Hello " + to + ", " + "Your One-Time Password (OTP) code is: ["+ otp +"] "
                + "Please do not share this OTP with anyone for security reasons. " + "Thanks.";
        Boolean alreadyExist = PersonValidation(to);
        if(alreadyExist){
            return new ResponseEntity<>("already_exist", HttpStatus.OK);
        }
        else{
            mailService.sendSimpleEmail(to, subject, text);
        }
        List <TempUser> user = TempRepo.findByUserEmail(to);
        if(user.isEmpty()){
            TempUser user1 = new TempUser();
            user1.setOtp(otp);
            user1.setUserEmail(to);
            user1.setPassword(password);
            TempRepo.save(user1);
        }
        else {
            List<TempUser> user2 = TempRepo.findByUserEmail(to);
            user2.get(0).setOtp(otp);
            user2.get(0).setPassword(password);
            TempRepo.save(user2.get(0));
        }
        return  new ResponseEntity<>("success",HttpStatus.OK);
    }
    public static String generateOTP(int length) {
        if (length < 1 || length > 9) {
            throw new IllegalArgumentException("OTP length must be between 1 and 9 digits.");
        }
        // Generate a random OTP with the specified length
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 1; i <= length; i++) {
            int digit = random.nextInt(10); // Generates a random digit between 0 and 9
            otp.append(digit);
        }

        return otp.toString();
    }
    @GetMapping("/Verification")
    public boolean Otpverification(@RequestParam String to, @RequestParam String otp ){
        System.out.println("Hello");
        Iterable<TempUser> user = TempRepo.findByUserEmail(to);
        var ref = new Object() {
            boolean isvalid = false;
        };
        user.forEach(x -> {
            if(x.getUserEmail().equals(to) && x.getOtp().equals(otp))
            {
                ref.isvalid = true;
                AuthEntity insertUser = new AuthEntity();
                insertUser.setUsername(x.getUsername());
                insertUser.setUserEmail(x.getUserEmail());
                insertUser.setPassword(x.getPassword());
                authRepository.save(insertUser);
                TempRepo.deleteById(x.getId());
            }
        });
        return ref.isvalid;
    }
    @GetMapping("/Personvalidation")
    public boolean PersonValidation(@RequestParam String to){
        System.out.println("Hello");
        List<AuthEntity> user = (List<AuthEntity>) authRepository.findAll();
        return user.stream().anyMatch(x-> x.getUserEmail().equals(to));
    }
    @GetMapping("/Allowuserlogin")
    public String Allowuserlogin(@RequestParam String to, @RequestParam String password){
        List<AuthEntity> user = (List<AuthEntity>) authRepository.findAll();
        if(user == null || user.size() == 0){
            return "no_user";
        }
        return user.stream().anyMatch(x-> x.getUserEmail().equals(to) && x.getPassword().equals(password)) ? "success" : "";
    }

    @GetMapping("/Updatepassword")
    public String Updatepassword(@RequestParam String name, @RequestParam String password) {
        AuthEntity user = authRepository.findByUserEmail(name);
        user.setPassword(password);
        user.setTempOtp(null);
        authRepository.save(user);
        return "success";
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String to) throws MessagingException {
        //mailService.sendSimpleEmail(to, subject, text)
        String otp = generateOTP(6);
        String subject = "Your OTP Code";
        String text = "Hello " + to + ", " + "Your One-Time Password (OTP) code is: ["+ otp +"] "
                + "Please do not share this OTP with anyone for security reasons. " + "Thanks.";
        mailService.sendSimpleEmail(to, subject, text);
        AuthEntity user = authRepository.findByUserEmail(to);
        user.setTempOtp(otp);
        authRepository.save(user);
        return  new ResponseEntity<>("success",HttpStatus.OK);
    }

    @GetMapping("/reVerification")
    public boolean ReOtpverification(@RequestParam String to, @RequestParam String otp ){
        System.out.println("Hello");
        AuthEntity user = authRepository.findByUserEmail(to);
        boolean valid = false;
        if(user.getUserEmail().equals(to) && user.getTempOtp().equals(otp))
        {
            valid = true;
        }
        return valid;
    }
}
