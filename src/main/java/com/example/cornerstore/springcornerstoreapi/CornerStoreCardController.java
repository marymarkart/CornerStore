package com.example.cornerstore.springcornerstoreapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

@RestController
public class CornerStoreCardController {
    private final CornerStoreCardRepository CornerStoreCardRepository;

    class Message{
        private String status;

        public String getStatus(){
            return status;
        }

        public void setStatus(String msg){
            status = msg ;
        }
    }

    CornerStoreCardController(CornerStoreCardRepository repository){
        this.CornerStoreCardRepository = repository ;
    }

    @PostMapping("/cards")
    CornerStoreCard newCard(){
        CornerStoreCard newCard = new CornerStoreCard() ;

        Random random = new Random();
        int num = random.nextInt(900000000) + 100000000 ;
        int code = random.nextInt(900) + 100 ;

        newCard.setCardNumber(String.valueOf(num));
        newCard.setCardCode(String.valueOf(code));
        newCard.setBalance(20.00);
        newCard.setActivated(false);
        newCard.setStatus("New Card");
        return CornerStoreCardRepository.save(newCard) ;
    }

    @GetMapping("/cards")
    List<CornerStoreCard> all() {
        return CornerStoreCardRepository.findAll() ;
    }

    @GetMapping("/cards/{num}")
    CornerStoreCard getOne(@PathVariable String num, HttpServletResponse response) {
        CornerStoreCard card = CornerStoreCardRepository.findByCardNumber(num) ;
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Found!") ;
        }
        return card;
    }

    @DeleteMapping("/card/{num}")
    void deleteAll(){
        CornerStoreCardRepository.deleteAllInBatch();
    }

    @DeleteMapping("/cards")
    Message deleteAllCards(){

        CornerStoreCardRepository.deleteAllInBatch();
        Message msg = new Message();
        msg.setStatus("All Cards Cleared!");
        return msg;
    }

    @PostMapping("card/activate/{num}/{code}")
    CornerStoreCard activate(@PathVariable String num, @PathVariable String code, HttpServletResponse response){
        CornerStoreCard card = CornerStoreCardRepository.findByCardNumber(num);
        if (card == null ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Found.") ;
        }
        if (card.getCardCode().equals(code)) {
            card.setActivated(true);
            CornerStoreCardRepository.save(card) ;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Valid!") ;
        }
        return card ;
    }

}
