package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import com.nexosis.impl.NexosisClientException;
import com.nexosis.model.ModelSessionDetail;
import com.nexosis.model.PredictionDomain;
import com.nexosis.model.SessionResponse;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SessionsController {

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"));

    @RequestMapping("/sessions")
    public String sessions(Model model) throws NexosisClientException {
        List<SessionResponse> sessionList = client.getSessions().list().getItems();
        model.addAttribute("sessionList", sessionList);
        return "sessions";
    }

    @RequestMapping(value = "/newModel", method = RequestMethod.POST)
    public void newModel(HttpServletResponse response, 
        @RequestParam("datasetName") String datasetName, 
        @RequestParam("target") String target,
        @RequestParam("predictionType") String predictionType) throws NexosisClientException, IOException {

        ModelSessionDetail model = new ModelSessionDetail();
        model.setDataSourceName(datasetName);
        model.setTargetColumn(target);
        model.setPredictionDomain(predictionType.equals("classification") ? PredictionDomain.CLASSIFICATION : PredictionDomain.REGRESSION);
        client.getSessions().trainModel(model).getSessionId();

        response.sendRedirect("/sessions");
    }

 
}


