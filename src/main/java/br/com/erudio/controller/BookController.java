package br.com.erudio.controller;


import br.com.erudio.dto.Exchange;
import br.com.erudio.enviroment.InstanceInformationService;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private InstanceInformationService informationService;

    @Autowired
    private BookRepository repository;

    @GetMapping(value = "/{id}/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book findBookBook(
            @PathVariable("id") Long id,
            @PathVariable("currency") String currency
    ){
        String port = informationService.retriveServerPort();

        Book book = repository.findById(id).orElseThrow();

        HashMap<String, String> parans = new HashMap<>();
        parans.put("amount", book.getPrice().toString());
        parans.put("from", "USD");
        parans.put("to", currency);

        var response = new RestTemplate()
                .getForEntity("http://localhost:8001/exchange-service" +
                        "/{amount}" +
                        "/{from}" +
                        "/{to}", Exchange.class, parans);

        Exchange exchange = response.getBody();

        book.setEnviroment(port);
        book.setPrice(exchange.getConvertedValue());
        book.setCurrency(currency);

        System.out.println(book.getEnviroment());
        System.out.println(book.getCurrency());

        return book;
    }


}
