package com.fmyblack.fmyes.bulk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class BulkPerformanceTest implements Runnable{

	static String[] oriLine;
	static int thread_size;
	static int circle_time;
	static int bulk_size;
	static int file_length;
	static String log_path;
	static String index = "test";
	static String type = "test";
	
	static long start;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigHelper.init(args[0]);
		thread_size = Integer.parseInt(ConfigHelper.getConf("performance", "thread_size"));
		circle_time = Integer.parseInt(ConfigHelper.getConf("performance", "circle_time"));
		bulk_size = Integer.parseInt(ConfigHelper.getConf("performance", "bulk.size"));
		file_length = Integer.parseInt(ConfigHelper.getConf("performance", "file.length"));
		log_path = ConfigHelper.getConf("performance", "log.path");
		oriLine = new String[file_length];
		index = ConfigHelper.getConf("performance", "index.name");
		type = ConfigHelper.getConf("performance", "type.name");
		
		try {
			loadFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start = System.currentTimeMillis();
		for(int i = 0; i < thread_size; i++) {
			BulkPerformanceTest bpft = new BulkPerformanceTest();
			new Thread(bpft).start();
		}
	}
	
	public static void loadFile() throws IOException{
		BufferedReader br;
		br = new BufferedReader(new FileReader(log_path));
		String line_ori = null;
		int length = 0;
		while((line_ori=br.readLine())!=null){
			length++;
			if(length >= file_length) {
				break;
			}
			oriLine[length] = line_ori;
		}
		for( ; length < file_length; length++) {
			oriLine[length] = oriLine[0];
		}
	}

	public static String loadSource(String ori) {
		
		JSONObject jo = new JSONObject();
		jo.put("one", ori);
		
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
