package com.demo.microuser.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.micro.common.domain.RestResponse;
import com.demo.microuser.domain.ApplicationException;
import com.demo.microuser.domain.HttpRequestMethedEnum;
import com.demo.microuser.domain.WebErrorCode;
import com.demo.microuser.model.Info;
import com.demo.microuser.model.UserInfo;
import com.demo.microuser.model.UserRole;
import com.demo.microuser.util.CommonUtil;
import com.demo.microuser.util.DTOUtil;
import com.demo.microuser.util.HttpClientUtil;

@Service
//@Transactional
public class ResourceServiceImpl implements ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);
	
	@Override
	public Object findInfoList(Long infoType, String title, Double x, Double y, Double range, Integer number) {
		StringBuilder url = new StringBuilder(CommonUtil.GATEWAY_URL + "/resource/findRangedInfoList");
		url.append("?x=" + x);
		url.append("&y=" + y);
		if(infoType != null){
			url.append("&infoType=" + infoType);			
		}
		if(title != null){
			url.append("&title=" + title);
		}
		url.append("&range=" + range);
		url.append("&number=" + number);
	    // 存储相关的header值
	    Map<String,String> header = new HashMap<String, String>();
	    String response = HttpClientUtil.sendHttp(HttpRequestMethedEnum.HttpGet,url.toString(), null,header);
	    //json转为object，判断code并返回数据
//	    String jsonString = JSON.toJSONString(JSONObject.parseObject(response),true);
//	    System.out.println(response);
	    RestResponse restResponse = DTOUtil.convertJSONStrToObject(response);
	    logger.info("-->{}", restResponse);
	    if(restResponse.getCode() != 0){
	    	throw new ApplicationException(WebErrorCode.CUSTOM, restResponse.getMsg());
	    }
	    return restResponse.getResult();
	}

	@Override
	public UserInfo login(UserInfo userInfo) {
		StringBuilder url = new StringBuilder(CommonUtil.GATEWAY_URL + "/resource/login");
		//存储相关的header值
	    Map<String,String> header = new HashMap<String, String>();
		// 相关请求参数
	    Map<String,Object> params = new HashMap<String, Object>();
	    params.put("username", userInfo.getUsername());
	    params.put("password", userInfo.getPassword());
	    String response = HttpClientUtil.sendHttp(HttpRequestMethedEnum.HttpPost,url.toString(), params,header);
	    RestResponse restResponse = DTOUtil.convertJSONStrToObject(response);
	    logger.info("-->{}", restResponse);
	    if(restResponse.getCode() != 0){
	    	throw new ApplicationException(WebErrorCode.CUSTOM, restResponse.getMsg());
	    }
	    UserInfo userRole = JSON.toJavaObject((JSON)restResponse.getResult(), UserRole.class);
	    logger.info("userRole:{}", userRole);
	    System.out.println(userRole.getUsername());
	    return userRole;
	}
	

}
