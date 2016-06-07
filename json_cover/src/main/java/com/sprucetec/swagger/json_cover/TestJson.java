package com.sprucetec.swagger.json_cover;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class TestJson {
	public static void main(String[] args) {
		System.out.println(JSONObject.parse("{\"x\":\"1\",\"y\":1,\"z\":1,\"a\":1,}", Feature.OrderedField).toString());
	}
}
