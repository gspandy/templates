package com.sprucetec.swagger.json_cover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class JSONCover {

	BufferedReader br = null;
	Map<String, Object> paramMap = null;

	public void cover(File f, boolean debug) throws Exception {
		paramMap = new LinkedHashMap<String, Object>();

		if (f == null) {
			br = new BufferedReader(new InputStreamReader(System.in));
		} else {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			paramMap.put("outfilename",
					f.getAbsolutePath().replace(".desc", ".php"));
		}

		readJson("request");
		readJson("response");
		readCheckExtra();
		br.close();
		if (debug) {
			System.out.println(paramMap);
		}
	}

	private void readCheckExtra() throws Exception {
		String line = null;
		while ((line = br.readLine()) != null) {
			if ("/".equals(line.trim())) {
				doCover();
				break;
			} else {
				handle(line);
			}
		}
	}

	private void handle(String line) throws IOException {
		line = line.trim();
		int pos = line.indexOf("=");
		if (pos != -1 && pos < line.length() - 1) {
			paramMap.put(line.substring(0, pos).trim(), line.substring(pos + 1)
					.trim());
		}
	}

	private void readJson(String paramType) throws IOException {
		String line = null;
		StringBuilder sbd = new StringBuilder();
		while (true) {
			// 读json
			line = br.readLine();
			if (line.startsWith("/" + paramType)) {
				break;
			} else {
				sbd.append(line).append("\n");
			}
		}
		paramMap.put(paramType, sbd.toString());
	}

	/**
	 * 执行转化并写文件
	 * 
	 * @throws Exception
	 */
	private void doCover() throws Exception {
		StringBuilder sbd = new StringBuilder();
		
		//开始
		sbd.append("<?php\n\n");
		
		//接口描述
		sbd.append("class PhpDoc {\n"
				+ "\t/**\n"
				+ "\t * @SWG\\" + paramMap.get("m") + "(\n"
				+ "\t *     tags={\"" + paramMap.get("tags") + "\"},\n"
				+ "\t *     path=\"" + paramMap.get("path") + "\",\n"
				+ "\t *     summary=\"" + paramMap.get("summary") + "\",\n"
				+ "\t *     description=\"" + paramMap.get("description")+ "\",\n" 
				+ "\t *     produces={\"application/json\"},\n"
				+ "\t *     consumes={\"application/json\"},\n");
		
		//请求参数定义
		if(!"0".equals(paramMap.get("has_param"))){
			sbd.append("\t *     @SWG\\Parameter (\n"
					+ "\t *         description=\""+ (paramMap.get("request_description") == null ? "" : paramMap.get("request_description")) + "\",\n"
					+ "\t *         format=\"int32\",\n"
					+ "\t *         name=\"params\",\n"
					+ "\t *         in=\""+("post".equalsIgnoreCase((String)paramMap.get("m"))?"body":"path")+"\",\n"
					+ ("0".equals(paramMap.get("param_required"))?"":"\t *         required=true,\n")
					+ "\t *         type=\"integer\",\n"
					+ "\t *         @SWG\\Schema(ref=\"#/definitions/"+ paramMap.get("path") + "/request\"),\n" + "\t *     "
					+ "),\n");
		}
		//结果参数定义
		sbd.append("\t *     @SWG\\Response(\n" + "\t *         response=200,\n"
				+ "\t *         description=\"操作结果状态\",\n"
				+ "\t * \t       @SWG\\Schema(ref=\"#/definitions/"
				+ paramMap.get("path") + "/response\"),\n" + "\t *     ),\n"
				+ "\t * )\n");
		
		//参数定义(json)
		String jsonStr = (String) paramMap.get("request");
		JSONObject jo = JSONObject.parseObject(jsonStr,Feature.OrderedField);
		if(jo!=null){
			sbd.append("\t * @SWG\\Definition(\n" + "\t *     definition=\""
					+ paramMap.get("path") + "/request" + "\",\n"
					+ "\t *     required={" + getRequiredKeys(jo) + "},\n"
					+ getProperties(jo) + "\n" + "\t * )\n");
		}
		//结果定义
		sbd.append("\t * @SWG\\Definition(\n"
				+ "\t *     definition=\""
				+ paramMap.get("path")
				+ "/response"
				+ "\",\n"
				+ "\t *     required={},\n"
				+ getProperties(JSONObject.parseObject((String) paramMap
						.get("response"),Feature.OrderedField)) + "\n" + "\t * )\n");

		//收尾
		sbd.append("\t */\n");
		sbd.append("}\n");

		writeFile(sbd);
	}

	/**
	 * 写php文件<br>
	 * 默认和描述文件同名,如果是控制台输入则使用out参数指定的文件名,若未指定则报错
	 * 
	 * @param sbd
	 * @throws Exception
	 */
	private void writeFile(StringBuilder sbd) throws Exception {
		String outFilename = (String) paramMap.get("outfilename");
		if (outFilename == null) {
			outFilename = (String) paramMap.get("out");
		}
		if (outFilename == null) {
			throw new RuntimeException("未指定输出目录out");
		}
		FileOutputStream out = new FileOutputStream(outFilename);
		out.write(sbd.toString().getBytes("utf-8"));
		out.close();
	}

	/**
	 * required,required_exclude规则<br>
	 * 指定了required则required_exclude将被忽略<br>
	 * 如果required未指定,且指定required_exclude则除指定key外全部必须
	 * 
	 * @param jo
	 * @return
	 */
	private String getRequiredKeys(JSONObject jo) {
		StringBuilder sbd = new StringBuilder();
		// required
		String required = (String) paramMap.get("required");
		// required_exclude
		String required_exclude = (String) paramMap.get("required_exclude");
		if (required != null && !required.isEmpty()) {
			String ss[] = required.split(",");
			for (String s : ss) {
				sbd.append(",").append('"').append(s.trim()).append('"');
			}
		} else if (required_exclude != null && !required_exclude.isEmpty()) {
			Set<String> set = new HashSet<String>();
			for (String s : jo.keySet()) {
				set.add(s);
			}
			String ss[] = required_exclude.split(",");
			for (String s : ss) {
				set.remove(s.trim());
			}
			for (String s : set) {
				sbd.append(",").append('"').append(s.trim()).append('"');
			}
		}
		return sbd.length() > 0 ? sbd.substring(1) : "";
	}

	/**
	 * 把json入参转成php注释格式
	 * 
	 * @param jo
	 * @return
	 */
	private String getProperties(JSONObject jo) {
		if (jo == null) {
			return "";
		}
		StringBuilder sbd = new StringBuilder();
		for (Entry<String, Object> e : jo.entrySet()) {
			sbd.append("\t *     @SWG\\Property(\n"
					+ "\t *         property=\"" + e.getKey() + "\",\n"
					+ "\t *         description=\"description\",\n"
					+ "\t *         type=\""
					+ e.getValue().getClass().getSimpleName().toLowerCase()
					+ "\",\n" + "\t *         default=" + getJSONValue(e, jo)
					+ "\n" + "\t * \t   ),\n");
		}
		return sbd.toString();
	}

	/**
	 * 获取default的json输出
	 * 
	 * @param e
	 * @param jo
	 * @return
	 */
	private String getJSONValue(Entry<String, Object> e, JSONObject jo) {
		Object value = e.getValue();
		String key = e.getKey();
		if(value==null){
			return null;
		}
		if (value instanceof List) {
			return jo.getJSONArray(key).toJSONString().replace('[', '{')
					.replace(']', '}');
		} else if (value instanceof Map) {
			return jo.getJSONObject(key).toJSONString().replace('[', '{')
					.replace(']', '}');
		} else if (value instanceof String) {
			return "\"" + value + "\"";
		} else {
			return value.toString();
		}
	}
}
