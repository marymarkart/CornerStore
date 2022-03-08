package com.example.springpayments;

import com.example.springcybersource.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/")
public class PaymentsController {

    private static boolean DEBUG = true ;

    @Value("${cybersource.apihost}") private String apiHost ;
    @Value("${cybersource.merchantkeyid}") private String merchantKeyId ;
    @Value("${cybersource.merchantsecretkey}") private String merchantsecretKey ;
    @Value("${cybersource.merchantid}") private String merchantId ;

    @Autowired
    private PaymentsCommandRepository paymentsCommandRepository;

    private CyberSourceAPI api = new CyberSourceAPI() ;

    private static Map<String,String> months = new HashMap<>();
    static {
        months.put("January", "01");
        months.put("February", "02");
        months.put("March", "03");
        months.put("April", "04");
        months.put("May", "05");
        months.put("June", "06");
        months.put("July", "07");
        months.put("August", "08");
        months.put("September", "09");
        months.put("October", "10");
        months.put("November", "11");
        months.put("December", "12");
    }

    private static Map<String,String> states = new HashMap<>();
    static {
        states.put("AL", "Alabama");
        states.put("AK", "Alaska");
        states.put("AZ", "Arizona");
        states.put("AR", "Arkansas");
        states.put("CA", "California");
        states.put("CO", "Colorado");
        states.put("CT", "Connecticut");
        states.put("DE", "Delaware");
        states.put("DC", "District of Columbia");
        states.put("FL", "Florida");
        states.put("GA", "Georgia");
        states.put("HI", "Hawaii");
        states.put("ID", "Idaho");
        states.put("IL", "Illinois");
        states.put("IN", "Indiana");
        states.put("IA", "Iowa");
        states.put("KS", "Kansas");
        states.put("KY", "Kentucky");
        states.put("LA", "Louisiana");
        states.put("ME", "Maine");
        states.put("MD", "Maryland");
        states.put("MA", "Massachusetts");
        states.put("MI", "Michigan");
        states.put("MN", "Minnesota");
        states.put("MS", "Mississippi");
        states.put("MO", "Missouri");
        states.put("MT", "Montana");
        states.put("NE", "Nebraska");
        states.put("NV", "Nevada");
        states.put("NH", "New Hampshire");
        states.put("NJ", "New Jersey");
        states.put("NM", "New Mexico");
        states.put("NY", "New York");
        states.put("NC", "North Carolina");
        states.put("ND", "North Dakota");
        states.put("OH", "Ohio");
        states.put("OK", "Oklahoma");
        states.put("OR", "Oregon");
        states.put("PA", "Pennsylvania");
        states.put("RI", "Rhode Island");
        states.put("SC", "South Carolina");
        states.put("SD", "South Dakota");
        states.put("TN", "Tennessee");
        states.put("TX", "Texas");
        states.put("UT", "Utah");
        states.put("VT", "Vermont");
        states.put("VA", "Virginia");
        states.put("WA", "Washington");
        states.put("WV", "West Virginia");
        states.put("WI", "Wisconsin");
        states.put("WY", "Wyoming");
    }

    @Getter
    @Setter
    class Message{
        private String msg;
        public Message(String m){
            msg = m;
        }
    }

    class ErrorMessages {
        private ArrayList<Message> messages = new ArrayList<Message>();
        public void add(String msg) {messages.add(new Message(msg));}

        public ArrayList<Message> getMessages() {
            return messages;
        }
        public void print(){
            for (Message m : messages){
                System.out.println(m.msg);
            }
        }
    }

    @GetMapping
    public String getAction( @ModelAttribute("command") PaymentsCommand command, 
                            Model model) {

        return "creditcards" ;

    }

    @PostMapping
    public String postAction(@Valid @ModelAttribute("command") PaymentsCommand command,  
                            @RequestParam(value="action", required=true) String action,
                            Errors errors, Model model, HttpServletRequest request) {
    
        log.info( "Action: " + action ) ;
        log.info( "Command: " + command ) ;

        CyberSourceAPI.setHost( apiHost );
        CyberSourceAPI.setKey( merchantKeyId );
        CyberSourceAPI.setSecret( merchantsecretKey );
        CyberSourceAPI.setMerchant( merchantId );

        CyberSourceAPI.debugConfig();

        ErrorMessages msgs = new ErrorMessages();

        boolean hasErrors = false ;
        if (command.firstname().equals("")) {hasErrors = true; msgs.add("First Name Required");}
        if (command.lastname().equals("")) {hasErrors = true; msgs.add("Last Name Required");}
        if (command.getAddress().equals("")) {hasErrors = true; msgs.add("Address Required");}
        if (command.getCity().equals("")) {hasErrors = true; msgs.add("City Required");}
        if (command.getState().equals("")) {hasErrors = true; msgs.add("State Required");}
        if (command.getZip().equals("")) {hasErrors = true; msgs.add("Zip Required");}
        if (command.getPhone().equals("")) {hasErrors = true; msgs.add("Phone Required");}
        if (command.getCardnum().equals("")) {hasErrors = true; msgs.add("Credit Card Number Required");}
        if (command.getCardexpmon().equals("")) {hasErrors = true; msgs.add("Credit Card Expiration Month Required");}
        if (command.getCardexpyear().equals("")) {hasErrors = true; msgs.add("Credit Card Expiration Year Required");}
        if (command.getCardcvv().equals("")) {hasErrors = true; msgs.add("Credit Card CVV Required");}
        if (command.getEmail().equals("")) {hasErrors = true; msgs.add("Email Address Required");}


        if (!command.getZip().matches("\\d{5}")) { hasErrors = true; msgs.add("Invalid Zip Code"); }
        if (!command.getPhone().matches("[(]\\d{3}[)] \\d{3}-\\d{4}")) { hasErrors = true; msgs.add("Invalid Phone Number. You entered: " + command.getPhone()); }
        if (!command.getCardnum().matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) { hasErrors = true; msgs.add("Invalid Card Number. You entered: " + command.getCardnum()); }
        if (!command.getCardexpyear().matches("\\d{4}")) { hasErrors = true; msgs.add("Invalid Card Expiration Year. You entered: " + command.getCardexpyear()); }
        if (!command.getCardcvv().matches("\\d{3}")) { hasErrors = true; msgs.add("Invalid Card CVV. You entered: " + command.getCardcvv()); }

        if ( months.get(command.getCardexpmon()) == null) { hasErrors = true; msgs.add("Invalid Card Expiration Month. You entered: " + command.getCardexpmon()); }

        if ( states.get(command.getState()) == null) { hasErrors = true; msgs.add("Invalid State. You entered: " + command.getState()); }

        if (hasErrors) {
            msgs.print();
            model.addAttribute("messages", msgs.getMessages());
            return "creditcards";
        }

        int min = 1239871;
        int max = 9999999;
        int random_int = (int) Math.floor(Math.random()*(max-min+1)+min) ;
        String order_num = String.valueOf(random_int) ;
        AuthRequest auth = new AuthRequest() ;
        auth.reference = order_num ;
        auth.billToFirstName = command.getFirstname(); ;
        auth.billToLastName = command.getLastname(); ;
        auth.billToAddress = command.getAddress() ;
        auth.billToCity = command.getCity() ;
        auth.billToState = command.getState() ;
        auth.billToZipCode = command.getZip() ;
        auth.billToPhone = command.getPhone() ;
        auth.billToEmail = command.getEmail() ;
        auth.transactionAmount = "30.00" ;
        auth.transactionCurrency = "USD" ;
        auth.cardNumnber = command.getCardnum() ;
        auth.cardExpMonth = months.get(command.getCardexpmon()) ;
        auth.cardExpYear = command.getCardexpyear() ;
        auth.cardCVV = command.getCardcvv() ;
        auth.cardType = CyberSourceAPI.getCardType(auth.cardNumnber) ;
        if (auth.cardType.equals("ERROR")){
            System.out.println("Unsupported Card Type");
            model.addAttribute("message", "Unsupported Card Type");
            return "creditcards";
        }

        boolean authValid = true ;
        AuthResponse authResponse = new AuthResponse() ;
        System.out.println("\n\nAuth Request: " + auth.toJson() ) ;
        authResponse = api.authorize(auth) ;
        System.out.println("\n\nAuth Response: " + authResponse.toJson() ) ;
        if ( !authResponse.status.equals("AUTHORIZED") ) {
            authValid = false ;
            System.out.println(authResponse.message);
            model.addAttribute( "message", authResponse.message) ;
            return "creditcards";
        }

        boolean captureValid = true ;
        CaptureRequest capture = new CaptureRequest() ;
        CaptureResponse captureResponse = new CaptureResponse() ;
        if ( authValid ) {
            capture.reference = order_num;
            capture.paymentId = authResponse.id ;
            capture.transactionAmount = "30.00" ;
            capture.transactionCurrency = "USD" ;
            System.out.println("\n\nCapture Request: " + capture.toJson() ) ;
            captureResponse = api.capture(capture) ;
            System.out.println("\n\nCapture Response: " + captureResponse.toJson() ) ;
            if ( !captureResponse.status.equals("PENDING") ) {
                captureValid = false ;
                System.out.println( captureResponse.message );
                model.addAttribute("message", captureResponse.message);
                return "creditcards";
            }

        }

        if (authValid && captureValid){
            command.setOrderNumber( order_num);
            command.setTransactionAmount("30.00");
            command.setTransactionCurrency("USD");
            command.setAuthId(authResponse.id);
            command.setAuthStatus(authResponse.status);
            command.setCaptureId(captureResponse.id);
            command.setCaptureStatus(captureResponse.status);

            paymentsCommandRepository.save(command);

            System.out.println("Thank you for your payment! Your order number is " + order_num);
            model.addAttribute("message", "Thank you for your payment! Your order number is: "+ order_num);
            return "creditcards";
        }

    return "creditcards";
    /* Render View */
    }
}