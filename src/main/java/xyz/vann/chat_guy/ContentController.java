package xyz.vann.chat_guy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ContentController {

    @GetMapping("/chat")
    public String chat(){
        return "chat";
    }
}