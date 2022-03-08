package com.example.springstarbucks;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

@RestController
public class StarbucksCardController {
    private final StarbucksCardRepository starbucksCardRepository;

    class Message{
        private String status;

        public String getStatus(){
            return status;
        }

        public void setStatus(String msg){
            status = msg ;
        }
    }

    StarbucksCardController(StarbucksCardRepository repository){
        this.starbucksCardRepository = repository ;
    }

    @PostMapping("/cards")
    StarbucksCard newCard(){
        StarbucksCard newCard = new StarbucksCard() ;

        Random random = new Random();
        int num = random.nextInt(900000000) + 100000000 ;
        int code = random.nextInt(900) + 100 ;

        newCard.setCardNumber(String.valueOf(num));
        newCard.setCardCode(String.valueOf(code));
        newCard.setBalance(20.00);
        newCard.setActivated(false);
        newCard.setStatus("New Card");
        return starbucksCardRepository.save(newCard) ;
    }

    @GetMapping("/cards")
    List<StarbucksCard> all() {
        return starbucksCardRepository.findAll() ;
    }

    @GetMapping("/cards/{num}")
    StarbucksCard getOne(@PathVariable String num, HttpServletResponse response) {
        StarbucksCard card = starbucksCardRepository.findByCardNumber(num) ;
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Found!") ;
        }
        return card;
    }

    @DeleteMapping("/card/{num}")
    void deleteAll(){
        starbucksCardRepository.deleteAllInBatch();
    }

    @DeleteMapping("/cards")
    Message deleteAllCards(){

        starbucksCardRepository.deleteAllInBatch();
        Message msg = new Message();
        msg.setStatus("All Cards Cleared!");
        return msg;
    }

    @PostMapping("card/activate/{num}/{code}")
    StarbucksCard activate(@PathVariable String num, @PathVariable String code, HttpServletResponse response){
        StarbucksCard card = starbucksCardRepository.findByCardNumber(num);
        if (card == null ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Found.") ;
        }
        if (card.getCardCode().equals(code)) {
            card.setActivated(true);
            starbucksCardRepository.save(card) ;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error. Card Not Valid!") ;
        }
        return card ;
    }

}
