package de.sadrab.testcode.Starter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sadrab.testcode.Parser.LogParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class StarterConfig {
    public StarterConfig(RestTemplate rest) {
        this.rest = rest;
    }

    private RestTemplate rest;

    @Value("${baseUrl.v1}")
    private String v1Url;

    @Value("${baseUrl.v2}")
    private String v2Url;

    @Bean
    public ApplicationRunner start(LogParser logParser){
        return args -> {
            logParser.parse().forEach(System.out::println);

            List<String> parsedData = logParser.parse();

            List<String> requests = parsedData.stream().filter(item -> item.contains("GET")).collect(Collectors.toList());

            requests.forEach(request -> {
                String requestData = request.split(" /")[1];

                String response1 = sendRequest(v1Url, requestData);
                String response2 = sendRequest(v2Url, requestData);

//                System.out.println(response1);
//                System.out.println(response2);

                String message = (response1.equalsIgnoreCase(response2)) ?
                        (" == With [" + requestData + "] have identical output. ==") :
                        " ## With [" + requestData + "] have different output. ##";

                System.out.println(message);




                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode1 = objectMapper.readTree(response1);
                    JsonNode jsonNode2 = objectMapper.readTree(response2);

                    if (jsonNode1 != null && jsonNode2 != null) {

                        if (jsonNode1.equals(jsonNode2)) {
                            System.out.println("Equle");
                        }

                        Iterator<String> stringIterator = jsonNode1.fieldNames();

                        stringIterator.forEachRemaining(fildName -> {
                            if (jsonNode1.get(fildName).equals(jsonNode2.get(fildName))) {
                                System.out.println("= " + fildName + " in first Equle to second");
                            }
                            else {
                                System.out.println("# " +  fildName + " in first NOT Equle to second");
                            }

                        });

                         //
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            });


        };
    }

    private String sendRequest(String url, String path) {
        String requestUrl1 = url + path;

        try {
            return rest.getForObject(requestUrl1, String.class);
        }catch (HttpClientErrorException e){
            return "";
        }
    }
}
