package egovframework.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocket {
	public static List<Session> sessionList = Collections.synchronizedList(new ArrayList<>());
	
	
	/**
	 * WebSocket 연결 시 호출
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connect WebSocket -" + session);
		
		sessionList.add(session);
	}

	
	/**
	 * WebSocket 메세지 수신 시 호출
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message) {
		
	}
	
	
	/**
	 * WebSocket 닫힐 때 호출
	 */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Close WebSocket - " + session);
		
		sessionList.remove(session);
	}
	
	
	@OnError
	public void onError(Throwable t) {
		System.out.println("Error " + t);
	}
	
}
