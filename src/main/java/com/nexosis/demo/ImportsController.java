package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import com.nexosis.impl.NexosisClientException;
import com.nexosis.model.ImportDetail;
import com.nexosis.model.ImportDetailQuery;
import com.nexosis.model.ImportFromS3Request;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ImportsController {

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"));

    @RequestMapping("/imports")
    public String imports(Model model) throws NexosisClientException {
        List<ImportDetail> importList = client.getImports().list(new ImportDetailQuery()).getItems();
        model.addAttribute("importList", importList);
        return "imports";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    private void importData(Model model, HttpServletResponse response,
        @RequestParam("datasetName") String datasetName, 
        @RequestParam("region") String region,
        @RequestParam("bucket") String bucket,
        @RequestParam("path") String path) throws NexosisClientException, IOException {

        ImportFromS3Request importRequest = new ImportFromS3Request();
        importRequest.setDataSetName(datasetName);
        importRequest.setRegion(region);
        importRequest.setBucket(bucket);
        importRequest.setPath(path);
        client.getImports().importFromS3(importRequest);
        response.sendRedirect("/imports");
    }
}