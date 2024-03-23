package com.rhoopoe.myfashiontrunk.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;

@Service
@Slf4j
public class DetectionService {

    @Value("${app.min-detection-confidence}")
    private float minConfidence;

    @Value("${app.max-detected-labels}")
    private int maxDetectedLabels;

    public DetectLabelsResult getImageLabels(byte[] imageBytes) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        if (minConfidence < 0 || minConfidence > 100 || maxDetectedLabels < 1) {
            throw new IllegalArgumentException(
                    "min-detection-confidence must be between 0 and 100, max-detected-labels must be positive"
            );
        }
        log.info(
                "Building label detection request with min-detection-confidence={} and max-detected-labels={}",
                minConfidence, maxDetectedLabels
        );
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withBytes(ByteBuffer.wrap(imageBytes)))
                .withMaxLabels(maxDetectedLabels)
                .withMinConfidence(minConfidence);
        DetectLabelsResult result = rekognitionClient.detectLabels(request);
        log.info("Returning detected image labels {}", result.getLabels().stream().map(Label::getName).toList());
        return result;
    }
}
