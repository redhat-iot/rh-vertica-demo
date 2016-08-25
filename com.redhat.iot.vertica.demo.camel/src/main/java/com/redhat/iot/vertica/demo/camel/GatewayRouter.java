package com.redhat.iot.vertica.demo.camel;

import org.eclipse.kura.camel.cloud.KuraCloudComponent;
import org.eclipse.kura.camel.router.CamelRouter;

/**
 * HPE Vertica demo route.
 */
public class GatewayRouter extends CamelRouter {

   private static String KURA = "kura-cloud:";
   private static String TOPIC = "vertica-demo/notifications";

   @Override
    public void configure() throws Exception {
        KuraCloudComponent cloudComponent = new KuraCloudComponent();
        cloudComponent.setCamelContext(camelContext);
        camelContext.addComponent("kura-cloud", cloudComponent);
//        WebsocketComponent websocketComponent = new WebsocketComponent();
//        camelContext.addComponent("atmosphere-websocket", websocketComponent);

//      Generate fake data for testing every 5 seconds, send to MQTT topic
//      Comment out for real demo
      from("timer://heartbeat?fixedRate=true&period=5000")
          .setBody(simple("Hello from timer at ${header.firedTime}"))
          .to(KURA + TOPIC);

      // Subscribe to topics on Everyware Cloud
      from(KURA + TOPIC)
         .log("Received Test Message: ${body.body}")
//         .to("atmosphere-websocket:///notification");
         .to("log:TestReceived");


   }
}