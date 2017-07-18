package com.fmyblack.fmyes.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fmyblack.fmyes.EsServer;
import com.fmyblack.fmyes.Indices.CreateState;
import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class BulkLocal implements Runnable{

	static String[] oriLine;
	static int thread_size;
	static int circle_time;
	static int bulk_size;
	static int file_length;
	static String setting_file;
	static String log_path;
	static String index = "test";
	static String type = "test";
	
	static long start;
	static boolean isSep;
	static Random rand = new Random(47);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ConfigHelper.init(args[0]);
		thread_size = 1;
		circle_time = 1;
		bulk_size = 1000;
		file_length = 147044;
		log_path = "/Users/fmyblack/data/9z-access_log.log";
//		setting_file = ConfigHelper.getConf("performance", "setting.file");
		oriLine = new String[file_length];
		index = "t14";
		type = "test";
		isSep = true;
		
		CreateState cs = EsServer.getInstance().createIndex(index);
		EsServer.getInstance().createMapping(index, type);
		System.out.println(cs.toString());

		try {
			loadFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start = System.currentTimeMillis();
		for(int i = 0; i < thread_size; i++) {
			BulkLocal bpft = new BulkLocal();
			new Thread(bpft).start();
		}
	}
	
	public static void loadFile() throws IOException{
		BufferedReader br;
		br = new BufferedReader(new FileReader(log_path));
		String line_ori = null;
		int length = 0;
		while((line_ori=br.readLine())!=null){
			oriLine[length] = line_ori;
			length++;
			if(length >= file_length) {
				break;
			}
		}
		for( ; length < file_length; length++) {
			oriLine[length] = oriLine[0];
		}
	}

	public static String loadSource(String ori) {
		
		JSONObject jo = new JSONObject();
		String key = "one";
		if(isSep) {
			key += rand.nextInt(1000);
		}
		jo.put("a", key);
		jo.put("ip", ori.split(" ")[0]);
		
		return jo.toString();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int c = 0; c < circle_time; c++) {
			List<String> bulkSource = new ArrayList<String>();
			for(int i = 1; i <= file_length; i++) {
				String source = loadSource(oriLine[i-1]);
				bulkSource.add(source);
				if(i % bulk_size == 0 ) {
					BulkUtil.bulk(index, type, bulkSource);
					bulkSource.clear();
				}
			}
			BulkUtil.bulk(index, type, bulkSource);
		}
		long end = System.currentTimeMillis();
		System.out.println("I am over :\t " + (end - start));
	}
}
