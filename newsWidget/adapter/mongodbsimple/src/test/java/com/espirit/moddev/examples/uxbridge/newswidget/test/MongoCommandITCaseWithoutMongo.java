package com.espirit.moddev.examples.uxbridge.newswidget.test;

/*
 * //**********************************************************************
 * uxbridge.samples.newswidget.mongodb
 * %%
 * Copyright (C) 2012 e-Spirit AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *********************************************************************//*
 */



import java.io.StringReader; 

import jmockmongo.MockMongo;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

import javax.jms.ConnectionFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class tests the ArticleHandler. It starts an own broker sends messages which will be processed by the ArticleHandler.
 * Afterwards it will query for the particular data records if they were written to the database successfully.
 */
public class MongoCommandITCaseWithoutMongo extends BaseTest {
	
	/** The MongoDB mock */
	private static MockMongo mockMongo;

	/** The broker. */
    private static BrokerService broker;
    
	/** The camel context. */
    private static CamelContext camelContext;
    
    private static String errorMessage;

	/**
	 * Do before.
	 *
	 * @throws Exception the exception
	 */
    @BeforeClass
    public static void doBeforeClass() throws Exception {

        broker = new BrokerService();
        // configure the broker
        broker.addConnector("tcp://localhost:61501");
        broker.start();

        ApplicationContext ctx = new FileSystemXmlApplicationContext(getBasePath("mongodbsimple") + "src/test/resources/applicationContext.xml");

        camelContext = (CamelContext) ctx.getBean("camelContext");
        camelContext.start();

        camelContext.addRoutes(new RouteBuilder() {
			
    		@Override
    		public void configure() throws Exception {
    			
    			from("jms:topic:BUS_IN").process(new Processor() {
    				
    				@Override
    				public void process(Exchange exchange) throws Exception {
    					errorMessage = (String)exchange.getIn().getBody();
    				}
    			});	
    		}
    	});
    }

	/**
	 * Shutdown.
	 *
	 * @throws Exception the exception
	 */
    @AfterClass
    public static void shutdown() throws Exception {
        broker.stop();
        camelContext.stop();
        //db.dropDatabase();

        if (mockMongo != null)
            mockMongo.stop();
    }

    /**
	 * Before test.
	 *
	 * @throws Exception the exception
	 */
    @Before
    public void beforeTest() throws Exception{
		
     }

    /**
     * Creates the CamelContext
     * @return The CamelContext
     */
    @Override
	protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61501");
        camelContext.addComponent("jms", JmsComponent.jmsComponentClientAcknowledge(connectionFactory));

        
        
        return camelContext;
    }

	/**
	 * Test add.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testAdd() throws Exception {
    	template.sendBody("jms:topic:BUS_OUT", getContent("src/test/resources/inbox/add/pressreleasesdetails_128.xml", "mongodbsimple"));
		Thread.sleep(7000);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		StringReader inStream = new StringReader(errorMessage);
		InputSource inSource = new InputSource(inStream);
		Document doc = dBuilder.parse(inSource);
		doc.getDocumentElement().normalize();
		
		NamedNodeMap nMap= doc.getChildNodes().item(0).getAttributes();
			
		assertEquals("response message is wrong", "uxb_entity" , doc.getChildNodes().item(0).getNodeName());
	}

    

	

	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newswidget.test.BaseTest#getContext()
	 */
    @Override
    public CamelContext getContext() {
        return camelContext;
    }
}
