package com.fmyblack.fmyes.Indices;

import java.io.File;
import java.util.Map;

import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.indices.IndexAlreadyExistsException;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.io.FileReaderUtil;

public class IndexIns implements IndexServer{

	private static IndexIns ins = null;
	
	private Client client;
	
	private IndexIns() {
		client = ClientProxy.getClient();
	}
	
	public static synchronized IndexIns getInstance(){
		if(ins == null) {
			ins = new IndexIns();
		}
		return ins;
	}

	@Override
	public CreateState createIndex(String index) {
		// TODO Auto-generated method stub
		return createIndex(index, getDefaultSetting());
	}

	@Override
	public CreateState createIndex(String index, File file) {
		// TODO Auto-generated method stub
		String fileContent = FileReaderUtil.readFile(file);
		return createIndex(index, fileContent);
	}
	
	@Override
	public CreateState createIndex(String index, String source) {
		// TODO Auto-generated method stub
		try{ 
			CreateIndexResponse cir = client.admin().indices()
					.prepareCreate(index)
					.setSettings(source)
					.execute()
					.actionGet();
			System.out.println(cir.isAcknowledged());
			if(responseSuccess(cir)) {
				return CreateState.Success;
			} else {
				return CreateState.Failed;
			}
		} catch (IndexAlreadyExistsException e){
			return CreateState.Exists;
		}
	}

	@Override
	public CreateState createIndex(String index, Map map) {
		// TODO Auto-generated method stub
		try{ 
			CreateIndexResponse cir = client.admin().indices()
					.prepareCreate(index)
					.setSettings(map)
					.execute()
					.actionGet();
			System.out.println(cir.isAcknowledged());
			if(responseSuccess(cir)) {
				return CreateState.Success;
			} else {
				return CreateState.Failed;
			}
		} catch (IndexAlreadyExistsException e){
			return CreateState.Exists;
		}
	}

	@Override
	public CreateState createMapping(String index, String type) {
		// TODO Auto-generated method stub
		return createMapping(index, type, getDefaultMapping());
	}

	@Override
	public CreateState createMapping(String index, String type, File file) {
		// TODO Auto-generated method stub
		String fileContent = FileReaderUtil.readFile(file);
		return createMapping(index, type, fileContent);
	}

	@Override
	public CreateState createMapping(String index, String type, String source) {
		// TODO Auto-generated method stub
		PutMappingRequest mappingRequest = Requests
											.putMappingRequest(index)
											.type(type)
											.source(source);
		PutMappingResponse pmr = client.admin().indices()
									.putMapping(mappingRequest)
									.actionGet();
		if(responseSuccess(pmr)) {
			return CreateState.Success;
		} else {
			return CreateState.Failed;
		}
	}
	
	@Override
	public CreateState updateSetting(String index) {
		// TODO Auto-generated method stub
		return updateSetting(index, getDefaultSetting());
	}

	@Override
	public CreateState updateSetting(String index, File file) {
		// TODO Auto-generated method stub
		String fileContent = FileReaderUtil.readFile(file);
		return updateSetting(index, fileContent);
	}

	@Override
	public CreateState updateSetting(String index, String source) {
		// TODO Auto-generated method stub
		UpdateSettingsResponse usr = client
									.admin().indices()
									.prepareUpdateSettings(index)
									.setSettings(source)
									.execute().actionGet();
		if(responseSuccess(usr)) {
			return CreateState.Success;
		} else {
			return CreateState.Failed;
		}
	}

	@Override
	public CreateState updateSetting(String index, Map<String, Object> map) {
		// TODO Auto-generated method stub
		UpdateSettingsResponse usr = client
				.admin().indices()
				.prepareUpdateSettings(index)
				.setSettings(map)
				.execute().actionGet();
		if(responseSuccess(usr)) {
			return CreateState.Success;
		} else {
			return CreateState.Failed;
		}
	}

	@Override
	public CreateState createTemplate(String index, String type, 
			String alias, String template) {
		// TODO Auto-generated method stub
		return createTemplate(index, type, getDefaultSetting(),
				getDefaultMapping(), alias, template);
	}

	@Override
	public CreateState createTemplate(String index, String type, 
			String setting, String mapping, String alias, String template) {
		// TODO Auto-generated method stub
		PutIndexTemplateResponse ptr = client.admin().indices()
										.preparePutTemplate(index)
										.setTemplate(template + "_*")
										.setSettings(setting)
										.addMapping(type, mapping)
										.addAlias(new Alias(alias))
										.execute()
										.actionGet();
		if(responseSuccess(ptr)) {
			return CreateState.Success;
		} else {
			return CreateState.Failed;
		}
	}

	private String getDefaultSetting() {
		String fileName = IndexIns.class.getClass().getResource("/").getPath() 
				+ File.separator 
				+ "es" + File.separator + "default.setting";
		return FileReaderUtil.readFile(fileName);
	}
	
	private String getDefaultMapping() {
		String fileName = IndexIns.class.getClass().getResource("/").getPath() 
				+ File.separator 
				+ "es" + File.separator + "default.mapping";
		return FileReaderUtil.readFile(fileName);
	}

	private boolean responseSuccess(AcknowledgedResponse resp) {
		return resp.isAcknowledged();
	}
	
	
	@Override
	public boolean existTemplate(String template) {
		// TODO Auto-generated method stub
		return !client.admin().indices()
				.prepareGetTemplates(template)
				.execute().actionGet()
				.isContextEmpty();
	}

	@Override
	public boolean deleteTemplate(String template) {
		// TODO Auto-generated method stub
		DeleteIndexTemplateResponse dtr = client.admin().indices()
											.prepareDeleteTemplate(template)
											.execute()
											.actionGet();
		return responseSuccess(dtr);
	}

	@Override
	public boolean addAlias(String index, String alias) {
		// TODO Auto-generated method stub
		IndicesAliasesResponse iar = client.admin().indices()
										.prepareAliases()
										.addAlias(index, alias)
										.execute()
										.actionGet();
		return responseSuccess(iar);
	}

	@Override
	public boolean deleteAlias(String index, String alias) {
		// TODO Auto-generated method stub
		IndicesAliasesResponse iar = client.admin().indices()
										.prepareAliases()
										.removeAlias(index, alias)
										.execute()
										.actionGet();
		return responseSuccess(iar);
	}
	
}
