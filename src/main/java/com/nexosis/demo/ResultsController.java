package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ResultsController {

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"), System.getenv("NEXOSIS_BASE_URL"));

    @RequestMapping("/results")
    public String datasets(Model model) {
 
        return "results";
    }
}
