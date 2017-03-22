package com.fmyblack.fmyes.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IostatAnalyze {

	public List<Cpu> cpus = new ArrayList<Cpu>();
	public List<Device> devices = new ArrayList<Device>();
	public Set<String> deviceNames = new HashSet<String>();
	
	Status now = Status.none;
	
	public void parseLine(String line) {
		line = line.trim();
		if (line.equals("")) {
			
		} else if (line.startsWith("avg-cpu")) {
			now = Status.cpu;
		} else if(line.startsWith("Device")) {
			now = Status.device;
		} else {
			if(now == Status.cpu) {
				cpus.add(new Cpu(line));
			} else if(now == Status.device){
				devices.add(new Device(line));
			}
		}
	}
	
	public void analyzeIoFile(String inputResult) {
		File input = new File(inputResult);
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = null;
			while( (line = br.readLine()) != null) {
				parseLine(line);
			}
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void caculateCpu() {
		Cpu all = new Cpu();
		for(Cpu cpu : cpus) {
			all.sum(cpu);
		}
		System.out.println(all.toString());
	}
	
	public void caculateDevice() {
		Map<String, Device> devAll = new HashMap<String, Device>();
		for(String dev : deviceNames) {
			devAll.put(dev, new Device(dev, 0));
		}
		for(Device dev : devices) {
			devAll.get(dev.getDeviceName()).sum(dev);
		}
		boolean needHead = true;
		for(Map.Entry<String, Device> entry : devAll.entrySet()) {
			if(needHead) {
				StringBuilder sb = new StringBuilder();
				for(String name : entry.getValue().getNames()) {
					sb.append(name + "\t\t");
				}
				System.out.println(sb.toString());
				needHead = false;
			}
			System.out.println(entry.getValue().toString());
		}
	}
	
	public void caculate() {
		caculateCpu();
		caculateDevice();
	}
	
	public void printSingleName(String file) {
		System.out.println("\n" + file);
		analyzeIoFile(file);
		caculate();
	}
	
	public static void main(String[] args) {
		String path = "/Users/fmyblack/javaproject/fmyblack-es/src/main/resources/yace/";
		for(char a = 'a'; a <= 'n'; a++) {
			String file = path + a + ".result";
//			System.out.println(file);
			IostatAnalyze ia = new IostatAnalyze();
			ia.printSingleName(file);
		}
	}
	
	class Cpu {
		 String[] names = {"user", "nice", "system", "iowait", "steal", "idle"};
		 int nameNum = names.length;
		 double[] values = new double[nameNum];
		 int all;
		 
		 public Cpu() { }
		 
		 public Cpu(String cpuNumLine) {
			 String[] numbers = cpuNumLine.split("\\s+");
			 for(int i = 0; i < 6; i++) {
				 values[i] = toDouble(numbers[i]);
			 }
		 }
		 
		 public void sum(Cpu other) {
			 for(int i = 0; i < nameNum; i++) {
				 this.values[i] += other.values[i];
			 }
			 this.all++;
		 }
		 
		 @Override
		 public String toString() {
			 StringBuilder sb = new StringBuilder();
			 for(int i = 0; i < nameNum; i++) {
				 sb.append(names[i] + "\t\t");
			 }
			 sb.append("\n");
			 for(int i = 0; i < nameNum; i++) {
				 sb.append(formatDouble(values[i] / this.all) + "\t\t");
			 }
			 sb.append("\n");
			 return sb.toString();
		 }
	}
	
	class Device {
		String ori = "rrqm/s   wrqm/s     r/s     w/s  "
				+ " rsec/s   wsec/s avgrq-sz avgqu-sz   await  svctm  %util";
		String[] names = ori.split("\\s+");
		double[] values = new double[names.length];
		String deviceName;
		int all;
		
		public Device(String name, int all) { 
			this.deviceName = name;
			this.all = all;
		}
		
		public Device(String deviceLine) {
			String[] cols = deviceLine.split("\\s+");
			deviceName = cols[0];
			deviceNames.add(deviceName);
			for(int i = 0; i < names.length; i++) {
				values[i] = toDouble(cols[i+1]);
			}
		}
		
		public String getDeviceName() {
			return this.deviceName;
		}
		
		public String[] getNames() {
			return this.names;
		}
		
		public boolean nameEquals(Device other) {
			return this.deviceName.equals(other.deviceName);
		}
		
		public void sum(Device other) {
			for(int i = 0; i < names.length; i++) {
				 this.values[i] += other.values[i];
			 }
			this.all++;
		}
		
		@Override
		 public String toString() {
			 StringBuilder sb = new StringBuilder();
			 for(int i = 0; i < names.length; i++) {
				 sb.append(formatDouble(values[i] / this.all) + "\t\t");
			 }
			 return sb.toString();
		 }
	}
	
	public static double toDouble(String number) {
		return Double.parseDouble(number);
	}
	
	public static String formatDouble(double d) {
		return new DecimalFormat("0.00").format(d);
	}
	
	enum Status {
		none, cpu, device
	}

}
