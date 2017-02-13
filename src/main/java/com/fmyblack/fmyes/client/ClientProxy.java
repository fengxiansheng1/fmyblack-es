package com.fmyblack.fmyes.client;

import org.elasticsearch.client.Client;

public class ClientProxy {

	public static Client getClient() {
		return ClientIns.CLIENT.getClient();
	}
}
