package com.fmyblack.fmyes.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.EsServer;
import com.fmyblack.fmyes.Indices.CreateState;
import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class UpdatePerformanceTest implements Runnable{

	static String[] oriLine;
	static int thread_size;
	static int circle_time;
	static int bulk_size;
	static int file_length;
	static String setting_file;
	static String log_path;
	static String index = "test";
	static String type = "test";
	
	static Client client;
	
	static long start;
	
	static Random rand = new Random(47);
	
	int thread_num;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigHelper.init(args[0]);
		client = ClientProxy.getClient();
		thread_size = Integer.parseInt(ConfigHelper.getConf("performance", "thread_size"));
		circle_time = Integer.parseInt(ConfigHelper.getConf("performance", "circle_time"));
		bulk_size = Integer.parseInt(ConfigHelper.getConf("performance", "bulk.size"));
		file_length = Integer.parseInt(ConfigHelper.getConf("performance", "file.length"));
		log_path = ConfigHelper.getConf("performance", "log.path");
		setting_file = ConfigHelper.getConf("performance", "setting.file");
		oriLine = new String[file_length];
		index = ConfigHelper.getConf("performance", "index.name");
		type = ConfigHelper.getConf("performance", "type.name");
		
		CreateState cs = EsServer.getInstance().createIndex(index, new File(setting_file));
		System.out.println(cs.toString());

		try {
			loadFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start = System.currentTimeMillis();
		for(int i = 0; i < thread_size; i++) {
			UpdatePerformanceTest bpft = new UpdatePerformanceTest(i);
			new Thread(bpft).start();
		}
	}
	
	public static void loadFile() throws IOException{
		BufferedReader br;
		System.out.println("log file:\t" + log_path);
		br = new BufferedReader(new FileReader(log_path));
		String line_ori = null;
		int length = 0;
		long startid = 123456789l;
		while((line_ori=br.readLine())!=null){
			oriLine[length] = startid + "\t" + line_ori;
			length++;
			if(length >= file_length) {
				break;
			}
			startid++;
		}
		for( ; length < file_length; length++) {
			oriLine[length] = oriLine[0];
		}
	}

	public static IndexRequestBuilder loadSource(int thread_num, int c, String ori) {
		String[] cols = ori.split("\t", 2);
		
		JSONObject jo = new JSONObject();
		jo.put("one", cols[1] + rand.nextInt(100));
		String id = cols[0] + thread_num + c;
		return client.prepareIndex(index, type, id).setSource(jo.toString());
	}
	
	public UpdatePerformanceTest(int num) {
		this.thread_num = num;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int c = 0; c < circle_time; c++) {
			List<IndexRequestBuilder> bulkSource = new ArrayList<IndexRequestBuilder>();
			for(int i = 1; i <= file_length; i++) {
				IndexRequestBuilder source = loadSource(this.thread_num, c, oriLine[i-1]);
				bulkSource.add(source);
				if(i % bulk_size == 0 ) {
					BulkUtil.bulk(bulkSource);
					bulkSource.clear();
				}
			}
			BulkUtil.bulk(bulkSource);
		}
		long end = System.currentTimeMillis();
		System.out.println("I am over :\t " + (end - start));
	}
}
