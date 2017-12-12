package com.nexosis.demo;

import com.nexosis.impl.NexosisClient;
import com.nexosis.impl.NexosisClientException;
import com.nexosis.model.ModelPredictionResult;
import com.nexosis.model.ModelSummary;
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

    private NexosisClient client = new NexosisClient(System.getenv("NEXOSIS_API_KEY"));

    @RequestMapping("/models")
    public String models(Model model) throws NexosisClientException {
        List<ModelSummary> modelList = client.getModels().list().getItems();
        model.addAttribute("modelList", modelList);
        return "models";
    }

    @RequestMapping("/model/{modelId}")
    public String showModel(Model model, @PathVariable("modelId") UUID modelId) throws NexosisClientException {
        ModelSummary nexoModel = getModelSummary(modelId);
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

        ModelSummary nexoModel = getModelSummary(modelId);
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
        
        ModelPredictionResult predictionRequest = client.getModels().predict(nexoModel.getModelId(), featureCollection);
        model.addAttribute("prediction", predictionRequest.getData().get(0));
        return "prediction";
    }

    private ModelSummary getModelSummary(UUID modelId) throws IllegalArgumentException, NexosisClientException {
        List<ModelSummary> modelList = client.getModels().list().getItems();
        ModelSummary model = new ModelSummary();
        for (ModelSummary mod : modelList) {
            if(mod.getModelId().compareTo(modelId) == 0) {
                model = mod;
            }
        }
        return model;
    }
 
}
