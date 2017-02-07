package com.fmyblack.fmyes;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

public class QueryUtil{

	// ######### 基础查询
	
	/**
	 * term查询
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryBuilder termQuery(String name, String value) {
		return QueryBuilders.termQuery(name, value);
	}
	
	/**
	 * terms查询
	 * @param name
	 * @param values
	 * @return
	 */
	public static QueryBuilder termsQuery(String name, String... values) {
		return QueryBuilders.termsQuery(name, values);
	}
	
	/**
	 * 
	 * @param queryString
	 * @return
	 */
	public static QueryBuilder queryStringQuery(String queryString) {
		return QueryBuilders.queryStringQuery(queryString);
	}
	
	/**
	 * 指定作用字段的querystring
	 * @param queryString
	 * @param fields
	 * @return
	 */
	public static QueryBuilder queryStringQuery(String queryString, String... fields) {
		QueryStringQueryBuilder qb = QueryBuilders.queryStringQuery(queryString);
		for(String field : fields) {
			qb.field(field);
		}
		return qb;
	}

	/**
	 * 模糊匹配
	 * @param name
	 * @param query
	 * @return
	 */
	public static QueryBuilder wildcardQuery(String name, String query) {
		return QueryBuilders.wildcardQuery(name, query);
	}
	
	// ######### 混合查询
	
	/**
	 * 同时满足多个query
	 * @param builders
	 * @return
	 */
	public static QueryBuilder mustQuery(QueryBuilder... builders) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		for(QueryBuilder builder : builders) {
			qb.must(builder);
		}
		return qb;
	}
	
	// ########## 常用查询
	
	/**
	 * 查询今天
	 * @param name
	 * @return
	 */
	public static QueryBuilder queryToday(String name) {
		return QueryBuilders.rangeQuery(name)
				.gte("now/d").lte("now")
				.timeZone("+08:00");
	}
	
	/**
	 * 查询昨天
	 * @param name
	 * @return
	 */
	public static QueryBuilder queryYesterday(String name) {
		return QueryBuilders.rangeQuery(name)
				.gte("now-1d/d").lt("now/d")
				.timeZone("+08:00");
	}
}
