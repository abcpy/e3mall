package com.e3.test;

import java.io.IOException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javassist.compiler.ast.NewExpr;

public class SpringAcTest {
	@Test
	public void testQueueProducer(){
//		第一步：初始化一个spring容器
		ApplicationContext applicationContext =new ClassPathXmlApplicationContext("classpath:spring/applicationContext-ac.xml");
//		第二步：从容器中获得JMSTemplate对象。
		JmsTemplate jmsTemplate =applicationContext.getBean(JmsTemplate.class);
//		第三步：从容器中获得一个Destination对象
		Destination destination=(Destination) applicationContext.getBean("queueDestination");
//		第四步：使用JMSTemplate对象发送消息，需要知道Destination
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage=session.createTextMessage("spring activemq test");
				return textMessage;
			}
		});
	}
	
	@Test
	public void testQueueConsumer() throws IOException{
		ApplicationContext applicationContext =new ClassPathXmlApplicationContext("classpath:spring/applicationContext-ac.xml");
		//等待
		System.in.read();

		
	}

}
