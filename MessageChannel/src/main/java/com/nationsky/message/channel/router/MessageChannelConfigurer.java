package com.nationsky.message.channel.router;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.nationsky.message.channel.pojos.SystemEventMessage;
import com.nationsky.message.channel.pojos.SystemEventMessage.MessageEvent;
import com.nationsky.message.channel.pojos.SystemEventMessage.MessageType;

public class MessageChannelConfigurer {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();
        
        // connect to embedded ActiveMQ JMS broker
        ConnectionFactory connectionFactory = 
            new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                
                //backend request route to topic:changes
                from("jms:incomingBackendRequest").marshal().json(JsonLibrary.Jackson).to("jms:changes");
                
                //platform request(device login/logout) route to changes queue
                from("jms:incomingPlatformRequest").marshal().json(JsonLibrary.Jackson).to("jms:changes");
                
                // monitor changes, to do monitor
                from("jms:changes").wireTap("jms:monitor").to("jms:bemail");
                
                from("jms:monitor").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Received XML order: " 
                                + exchange.getIn().getBody()); 
                    }
                });
                
                from("jms:bemail").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Received XML order: " 
                                + exchange.getIn().getBody()); 
                    }
                });
            }
        });

        // start the route and let it do its work
        context.start();
        
        SystemEventMessage message = new SystemEventMessage();
        message.setKeys(new String[]{"key1","key2"});
        message.setType(MessageType.USER);
        message.setEvent(MessageEvent.REMOVE);
        
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("jms:incomingBackendRequest", message);
        
        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }
}
