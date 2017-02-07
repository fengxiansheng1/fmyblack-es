package com.fmyblack.fmyes;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fmyblack.util.config.ConfigHelper;

public enum ClientUtil {

	CLIENT {
    };
    
    protected TransportClient client = null;

    private ClientUtil() {
    	ConfigHelper.init("/Users/fmyblack/javaproject/fmyblack-es/src/main/resources/conf");
        String cluster_name = ConfigHelper.getConf("es", "cluster.name");
        String transports = ConfigHelper.getConf("es", "transport");
        
        Settings settings = Settings.settingsBuilder()
                                .put("cluster.name", cluster_name).build();
        client = TransportClient.builder().settings(settings).build();
        try {
        	for(String transport : transports.split(";")) {
        		String[] cols = transport.split(":");
        		String host = cols[0];
        		int port = Integer.parseInt(cols[1]);
        		client.addTransportAddress(new InetSocketTransportAddress(
        				InetAddress.getByName(host), port));
        	}
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("your " + getClusterStat().getClusterName().toString()
        		+ " exists");
    }
    
    public Client getClient() {
    	return client;
    }
    
    public ClusterStateResponse getClusterStat() {
    	return client.admin().cluster()
				.prepareState()
				.execute().actionGet();
    }
    
    public boolean checkClient() {
    	ClusterStateResponse resp = getClusterStat();
    	if (resp.getClusterName() == null ) {
    		return false;
    	}
    	return true;
    }
    
    public static void main(String[] args) {
    	CLIENT.getClient();
    	CLIENT.getClient();
    	if(CLIENT.checkClient()) {
    		System.out.println("yes");
    	}
    }
}
