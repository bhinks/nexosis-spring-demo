package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import com.nexosis.impl.NexosisClientException;
import com.nexosis.model.DataSetDeleteOptions;
import com.nexosis.model.DataSetSummary;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DatasetsController {

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"));

    @RequestMapping("/datasets")
    public String datasets(Model model) throws NexosisClientException {
        List<DataSetSummary> sets = client.getDataSets().list().getItems();
        model.addAttribute("datasets", sets);
        return "datasets";
    }

    @RequestMapping("/dataset/{datasetName}")
    public String dataset(Model model, @PathVariable("datasetName") String datasetName) throws NexosisClientException {
        model.addAttribute("datasetColumns", client.getDataSets().get(datasetName).getColumns().getsetColumnMetadata());
        model.addAttribute("datasetName", datasetName);
        return "dataset";
    }

    @RequestMapping("/dataset/delete/{datasetName}")
    public void delete(HttpServletResponse response, @PathVariable("datasetName") String datasetName) throws NexosisClientException, IOException {
        client.getDataSets().remove(datasetName, DataSetDeleteOptions.CASCADE_ALL);
        response.sendRedirect("/datasets");
    }



}
