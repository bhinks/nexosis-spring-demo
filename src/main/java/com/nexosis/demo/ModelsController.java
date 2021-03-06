package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import com.nexosis.impl.NexosisClientException;
import com.nexosis.model.ModelPredictionRequest;
import com.nexosis.model.ModelPredictionResult;
import com.nexosis.model.ModelSummary;
import com.nexosis.model.ModelSummaryQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ModelsController {

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"), System.getenv("NEXOSIS_BASE_URL"));

    @RequestMapping("/models")
    public String models(Model model) throws NexosisClientException {
        List<ModelSummary> modelList = client.getModels().list(new ModelSummaryQuery()).getItems();
        model.addAttribute("modelList", modelList);
        return "models";
    }

    @RequestMapping("/model/{modelId}")
    public String showModel(Model model, @PathVariable("modelId") UUID modelId) throws NexosisClientException {
        ModelSummary nexoModel = client.getModels().get(modelId);
        String datasetName = nexoModel.getDataSourceName();
        model.addAttribute("datasetColumns", nexoModel.getColumns().getsetColumnMetadata());
        model.addAttribute("datasetName", datasetName);
        model.addAttribute("model", nexoModel);
        
        return "model";
    }

    @RequestMapping("/predict")
    public String predict(Model model, 
        @RequestParam("modelId") UUID modelId,
        @RequestParam Map<String,String> params) throws NexosisClientException {

        Map<String, String> features = new HashMap<String, String>();
        List<Map<String,String>> featureCollection = new ArrayList<Map<String,String>>();

        for(Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String keyparts[] = key.split("|");
            if(keyparts.length > 1) {
                features.put(keyparts[1], value);
            }
        }
        featureCollection.add(features);
        ModelPredictionRequest modelPrediction = new ModelPredictionRequest();
        modelPrediction.setData(featureCollection);
        modelPrediction.setModelId(modelId);
        ModelPredictionResult prediction = client.getModels().predict(modelPrediction);
        model.addAttribute("prediction", prediction.getData().get(0));
        return "prediction";
    }
 
}
