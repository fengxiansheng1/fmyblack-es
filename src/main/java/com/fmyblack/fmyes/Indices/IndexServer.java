package com.fmyblack.fmyes.Indices;

import java.io.File;
import java.util.Map;

public interface IndexServer {

	CreateState createIndex(String index);
	
	CreateState createIndex(String index, File file);
	
	CreateState createIndex(String index, String source);
	
	CreateState createIndex(String index, Map<String, ?> map);
	
	CreateState createMapping(String index, String type);
	
	CreateState createMapping(String index, String type, File file);
	
	CreateState createMapping(String index, String type, String source);
	
	CreateState updateSetting(String index);
	
	CreateState updateSetting(String index, File file);
	
	CreateState updateSetting(String index, String source);
	
	CreateState updateSetting(String index, Map<String, Object> map);
	
	CreateState createTemplate(String index, String type, 
			String alias, String template);
	
	CreateState createTemplate(String index, String type, 
			String setting, String mapping, String alias, String template);
	
	boolean existTemplate(String template);
	
	boolean deleteTemplate(String template);
	
	boolean addAlias(String index, String alias);
	
	boolean deleteAlias(String index, String alias);
}
