package com.example.cornerstore.springcornerstoreapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/order")
public class CornerStoreOrderController {
    private final CornerStoreOrderRepository CornerStoreOrderRepository;

    @Autowired
    private CornerStoreCardRepository CornerStoreCardRepository ;


    class Message{
        private String status;

        public String getStatus(){
            return status;
        }

        public void setStatus(String msg){
            status = msg ;
        }
    }

    private HashMap<String, CornerStoreOrder> orders = new HashMap<>();

    CornerStoreOrderController(CornerStoreOrderRepository repository){ this.CornerStoreOrderRepository = repository ;
    }

    @GetMapping("/orders")
    List<CornerStoreOrder> all() {
        return CornerStoreOrderRepository.findAll() ;
    }

    @DeleteMapping("/orders")
    Message deleteAll(){

        CornerStoreOrderRepository.deleteAllInBatch();
        orders.clear();
        Message msg = new Message();
        msg.setStatus("All Orders Cleared!");
        return msg;
    }

    @PostMapping("/order/register/{regid}")
    @ResponseStatus(HttpStatus.CREATED)
    CornerStoreOrder newOrder(@PathVariable String regid, @RequestBody CornerStoreOrder order){
        System.out.println("Placing Order (Reg ID = " + regid + ") => " + order);
        if (order.getDrink().equals("") || order.getMilk().equals("") || order.getSize().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Order Requested!");
        }
        CornerStoreOrder active = orders.get(regid);
        if (active != null) {
            System.out.println("Active Order (Reg ID = " + regid + "( => " + active);
            if (active.getStatus().equals("Ready for Payment.")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active Order Exists!");
            }
        }
        double price = 0.0;
        switch (order.getDrink()){
            case "Latte":
                switch (order.getSize()){
                    case "Tall":
                        price = 2.95 ;
                        break;
                    case "Grande":
                        price = 3.65 ;
                        break;
                    case "Venti":
                    case "Your Own Cup":
                        price = 3.95 ;
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Size");
                }
                break;
            case "Americano":
                switch (order.getSize()){
                    case "Tall":
                        price = 2.25 ;
                        break;
                    case "Grande":
                        price = 2.65 ;
                        break;
                    case "Venti":
                    case "Your Own Cup":
                        price = 2.95 ;
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Size");
                }
                break;
            case "Mocha":
                switch (order.getSize()){
                    case "Tall":
                        price = 3.45 ;
                        break;
                    case "Grande":
                        price = 4.15 ;
                        break;
                    case "Venti":
                    case "Your Own Cup":
                        price = 4.45 ;
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Size");
                }
                break;
            case "Espresso":
                switch (order.getSize()){
                    case "Short":
                        price = 1.75 ;
                        break;
                    case "Tall":
                        price = 1.95 ;
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Size");
                }
                break;
            case "Cappuccino":
                switch (order.getSize()){
                    case "Tall":
                        price = 2.95 ;
                        break;
                    case "Grande":
                        price = 3.65 ;
                        break;
                    case "Venti":
                        price = 3.95 ;
                        break;
                    case "Your Own Cup":
                        price = 3.95 ;
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Size");
                }
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Drink!") ;
        }
        double tax = 0.0725 ;
        double total = price + (price * tax)  ;
        double scale = Math.pow(10, 2);
        double rounded = Math.round(total * scale) / scale ;
        order.setTotal(rounded);

        order.setStatus("Ready for Payment.");
        CornerStoreOrder new_order = CornerStoreOrderRepository.save(order);
        orders.put(regid, new_order);
        return new_order ;
    }

    @GetMapping("/order/register/{regid}")
    CornerStoreOrder getActiveOrder(@PathVariable String regid, HttpServletResponse response){
        CornerStoreOrder active = orders.get(regid);
        if (active != null) {
            return active ;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order Not Found!") ;
        }
    }

    @DeleteMapping("/order/register/{regid}")
    Message deleteActiveOrder(@PathVariable String regid) {
        CornerStoreOrder active = orders.get(regid);
        if (active != null ){
            orders.remove(regid);
            Message msg = new Message();
            msg.setStatus("Active Order Cleared!");
            return msg;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order Not Found!");
        }
    }

    @PostMapping("/order/register/{regid}/pay/{cardnum}")
    CornerStoreCard processOrder(@PathVariable String regid, @PathVariable String cardnum ){
        System.out.println("Pay for Order: Reg ID = " + regid + " Using Card = " + cardnum );
        CornerStoreOrder active = orders.get(regid);
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order Not Found!");
        }
        if ( cardnum.equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card Number not Provided!");
        }
        if ( active.getStatus().startsWith("Paid with Card")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clear Paid Active Order!");
        }
        CornerStoreCard card = CornerStoreCardRepository.findByCardNumber(cardnum);
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card Not Found!") ;
        }
        if (!card.isActivated()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card Not Activated");
        }
        double price = active.getTotal();
        double balance = card.getBalance();
        if ( (balance - price) < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient Funds on Card!") ;
        }
        double new_balance = balance - price ;
        card.setBalance( new_balance );
        String status = "Paid with Card: " + cardnum + " Balance: $" + new_balance + ".";
        active.setStatus(status);
        CornerStoreCardRepository.save(card);
        CornerStoreOrderRepository.save( active );
        return card;

    }
}
