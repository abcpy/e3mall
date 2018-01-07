package com.e3.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.e3.search.service.searchItemImpl;

public class ItemChangeListener implements MessageListener{
	@Autowired
	private searchItemImpl itemImpl;
	@Override
	public void onMessage(Message message) {
		try{
		TextMessage textMessage=null;
		Long itemId=null;
		//取商品id
		if(message instanceof TextMessage){
			textMessage=(TextMessage) message;
			itemId=Long.parseLong(textMessage.getText());
		  }
		
			itemImpl.addDocument(itemId);
		}
			catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}


