package com.redhat.iot.vertica.demo.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.kura.core.message.protobuf.KuraPayloadProto;

/**
 * HPE Vertica demo route.
 */
public class TestRoute extends RouteBuilder {

   @Override
    public void configure() throws Exception {

//      Generate fake data for testing every 5 seconds, send to MQTT topic
//      Comment out for real demo
//      from("timer://heartbeat?fixedRate=true&period=5000")

      /*
      from("file:/Users/ccustine/development/redhat/rh-vertica-demo/com.redhat.iot.vertica.demo.camel/src/test/resources/?fileName=Vertica_Data_PM_short.csv&noop=true")
            .split().tokenize("\\n").streaming()
            .delay(1000)
            .log("Sending ${header.CamelSplitIndex} of ${header.CamelSplitSize}")
            .unmarshal(new CsvDataFormat()
                  .setIgnoreEmptyLines(true)
                  .setUseMaps(true)
                  .setCommentMarker('#')
                  .setHeader(new String[]{"id", "cycle", "settings1", "settings2", "settings3", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "s12", "s13", "s14", "s15", "s16", "s17", "s18", "s19", "s20", "s21"}))
            .to("log:Send");
*/

      // Subscribe to topics, unmarshal Kura gzipped/protobuf messages
      from("mqtt:consume?host=tcp://172.16.121.81:1883&userName=admin&password=admin&subscribeTopicName=Red-Hat/#/vertica-demo/data")
         .unmarshal().gzip()
         .unmarshal().protobuf("org.eclipse.kura.core.message.protobuf.KuraPayloadProto$KuraPayload")
            .process(new Processor() {
               @Override
               public void process(Exchange exchange) throws Exception {
                  KuraPayloadProto.KuraPayload kuraPayload = (KuraPayloadProto.KuraPayload) exchange.getIn().getBody();
                  for (KuraPayloadProto.KuraPayload.KuraMetric kuraMetric : kuraPayload.getMetricList()) {
                     String name = kuraMetric.getName();
                     String value = kuraMetric.getStringValue();
                     // Do something with the metric name/value here and then send it on
                  }
               }
            })
         .to("log:Received");


   }
}