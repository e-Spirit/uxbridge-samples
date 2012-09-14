package com.espirit.moddev.examples.uxbridge.newsdrilldown.test;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBEntity;

public class ExceptionTest extends BaseTest {
	
	/** The ApplicationContext. */
	private static ApplicationContext ctx;
	
	/** The broker. */
	private static BrokerService broker;
	
	/** The camel context. */
	private static CamelContext camelContext;
	
	/** The emf. */
	private static EntityManagerFactory emf;


	/**
	 * Do before.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void doBefore() throws Exception {
		broker = new BrokerService();
		// configure the broker
		broker.addConnector("tcp://localhost:61500");
		broker.start();

		ctx = new FileSystemXmlApplicationContext(getBasePath("hibernate") + "src/test/resources/applicationContext.xml");

		camelContext = (CamelContext) ctx.getBean("camelContext");

		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:topic:BUS_OUT").
						convertBodyTo(UXBEntity.class).to("mock:routeResponse").to("stream:out");
			}
		});

		camelContext.start();

		emf = (EntityManagerFactory) ctx.getBean("entityManagerFactory");
	}

	/**
	 * Shutdown.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void shutdown() throws Exception {
		camelContext.stop();
		broker.stop();
		emf.close();

		((FileSystemXmlApplicationContext)ctx).close();
	}


	/* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createCamelContext()
	 */
	protected CamelContext createCamelContext() throws Exception {
		CamelContext camelContext = super.createCamelContext();

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61500");
		camelContext.addComponent("jms", JmsComponent.jmsComponentClientAcknowledge(connectionFactory));

		return camelContext;
	}

	@Test
	public void testAdd() throws Exception {

		EntityManager em = emf.createEntityManager();


		String[] ids = new String[]{"1","132"};

		// insert all items
		for (String id : ids) {
			// load content
			String content = getContent("src/test/resources/inbox/exceptiontest/pressreleases_" + id + ".xml", "hibernate");
			// send content to jms broker
			template.sendBody("jms:topic:BUS_OUT", content);

			// wait
			Thread.sleep(TimeOuts.LONG);
		}

		em.close();
	}
	
	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newsdrilldown.test.BaseTest#getContext()
	 */
	@Override
	public CamelContext getContext() {
		return camelContext;
	}

}
