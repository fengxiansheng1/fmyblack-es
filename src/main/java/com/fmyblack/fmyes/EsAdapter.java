package com.fmyblack.fmyes;

import java.io.File;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.Indices.CreateState;
import com.fmyblack.fmyes.Indices.IndexIns;
import com.fmyblack.fmyes.Indices.IndexServer;

public abstract class EsAdapter implements IndexServer{

	public abstract Client getClient();
	
	IndexServer is = IndexIns.getInstance();
	
	@Override
	public CreateState createIndex(String index) {
		// TODO Auto-generated method stub
		return is.createIndex(index);
	}

	@Override
	public CreateState createIndex(String index, File file) {
		// TODO Auto-generated method stub
		return is.createIndex(index, file);
	}

	@Override
	public CreateState createIndex(String index, String source) {
		// TODO Auto-generated method stub
		return is.createIndex(index, source);
	}

	@Override
	public CreateState createIndex(String index, Map<String, ?> map) {
		// TODO Auto-generated method stub
		return is.createIndex(index, map);
	}

	@Override
	public CreateState createMapping(String index, String type) {
		// TODO Auto-generated method stub
		return is.createMapping(index, type);
	}

	@Override
	public CreateState createMapping(String index, String type, File file) {
		// TODO Auto-generated method stub
		return is.createMapping(index, type, file);
	}

	@Override
	public CreateState createMapping(String index, String type, String source) {
		// TODO Auto-generated method stub
		return is.createMapping(index, type, source);
	}

	@Override
	public CreateState updateSetting(String index) {
		// TODO Auto-generated method stub
		return is.updateSetting(index);
	}

	@Override
	public CreateState updateSetting(String index, File file) {
		// TODO Auto-generated method stub
		return is.updateSetting(index, file);
	}

	@Override
	public CreateState updateSetting(String index, String source) {
		// TODO Auto-generated method stub
		return is.updateSetting(index, source);
	}

	@Override
	public CreateState updateSetting(String index, Map<String, Object> map) {
		// TODO Auto-generated method stub
		return is.updateSetting(index, map);
	}

	@Override
	public CreateState createTemplate(String index, String type, String alias, String template) {
		// TODO Auto-generated method stub
		return is.createTemplate(index, type, alias, template);
	}

	@Override
	public CreateState createTemplate(String index, String type, String setting, String mapping, String alias,
			String template) {
		// TODO Auto-generated method stub
		return is.createTemplate(index, type, setting, mapping, alias, template);
	}

	@Override
	public boolean existTemplate(String template) {
		// TODO Auto-generated method stub
		return is.existTemplate(template);
	}

	@Override
	public boolean deleteTemplate(String template) {
		// TODO Auto-generated method stub
		return is.deleteTemplate(template);
	}

	@Override
	public boolean addAlias(String index, String alias) {
		// TODO Auto-generated method stub
		return is.addAlias(index, alias);
	}

	@Override
	public boolean deleteAlias(String index, String alias) {
		// TODO Auto-generated method stub
		return is.deleteAlias(index, alias);
	}
	
}
