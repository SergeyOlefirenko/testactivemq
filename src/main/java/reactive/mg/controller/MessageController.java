package reactive.mg.controller;

import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactive.mg.form.MessageForm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class MessageController {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public MessageController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("message", new MessageForm());
        return "index";
    }
    @GetMapping("/messages")
    public String showMessages(Model model) throws IOException {
        // Чтение содержимого файла и передача его в модель
        String content = new String(Files.readAllBytes(Paths.get("messages.html")));
        model.addAttribute("content", content);
        return "messages";
    }

    @PostMapping("/")
    public String sendMessage(MessageForm messageForm) {
        String destination = messageForm.getDestination();
        String messageText = messageForm.getMessageText();
        jmsTemplate.send(destination, session -> {
            TextMessage message = session.createTextMessage(messageText);
            return message;
        });
        return "redirect:/?success";
    }
}
