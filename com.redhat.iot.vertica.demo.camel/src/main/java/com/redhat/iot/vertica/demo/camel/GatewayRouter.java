package com.redhat.iot.vertica.demo.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.eclipse.kura.camel.cloud.KuraCloudComponent;
import org.eclipse.kura.camel.router.CamelRouter;
import org.eclipse.kura.message.KuraPayload;

import java.util.List;
import java.util.Map;

/**
 * HPE Vertica demo route.
 */
public class GatewayRouter extends CamelRouter {

   private static String KURA = "kura-cloud:";
   private static String TOPIC = "vertica-demo/data";

   @Override
    public void configure() throws Exception {
        KuraCloudComponent cloudComponent = new KuraCloudComponent();
        cloudComponent.setCamelContext(camelContext);
        camelContext.addComponent("kura-cloud", cloudComponent);


      from("file:/media/sf_data/?fileName=Vertica_Data_PM.csv&delete=true") //Poll for file and delete when finished
            .split().tokenize("\\n").streaming() //Process each line of the file separately, and stream to keep memory usage down
            .delay(1000) //Delay 1 second between processing lines
            .log("Sending ${header.CamelSplitIndex} of ${header.CamelSplitSize}")
            .unmarshal(new CsvDataFormat() //Unmarshal from the csv formatted line and convert to a map using the specified strings as keys
                  .setIgnoreEmptyLines(true)
                  .setUseMaps(true)
                  .setCommentMarker('#')
                  .setHeader(new String[]{"id", "cycle", "settings1", "settings2", "settings3", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "s12", "s13", "s14", "s15", "s16", "s17", "s18", "s19", "s20", "s21"}))
            .process(new Processor() {
               @Override
               public void process(Exchange exchange) throws Exception {
                  KuraPayload payload = new KuraPayload();
                  List<Map> metrics = (List<Map>) exchange.getIn().getBody();
                  Map<String, String> map =  metrics.get(0); //Each line of the file produces a map of name/value pairs, but we only get one line at a time due to the splitter above
                  for (Map.Entry<String, String> entry : map.entrySet()) {
                     payload.addMetric(entry.getKey(), entry.getValue());
                  }

                  exchange.getIn().setBody(payload);
               }
            })
            .log("Sending CSV record")
            .to(KURA + TOPIC);


      // Subscribe to topics on Everyware Cloud
/*
      from(KURA + TOPIC)
         .log("Received Test Message: ${body.body}")
         .to("log:TestReceived");
*/


   }
}