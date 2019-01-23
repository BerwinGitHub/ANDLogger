package com.berwin.logger.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class UserDefault {
	public static final String FILE_PATH = "./res/userdefault.json";
	private static UserDefault instance = null;
	private JSONObject jsonData = null;

	private UserDefault() {
		this.loadData();
	}

	public static synchronized UserDefault getInstance() {
		if (instance == null) {
			instance = new UserDefault();
		}
		return instance;
	}

	public void setValueForKey(String key, Object value) {
		jsonData.put(key, value);
		this.saveData();
	}

	@SuppressWarnings("unchecked")
	public <T> T getValueForKey(String key, T def) {
		Object value = jsonData.get(key);
		if (value == null)
			return def;
		if (value instanceof JSONArray) {
			Object[] objs = ((JSONArray) value).toArray();
			return (T) objs;
		}
		return (T) value;
	}

	private void loadData() {
		try {
			FileInputStream inputStream = new FileInputStream(FILE_PATH);
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String lineTxt;
			while ((lineTxt = reader.readLine()) != null) {
				sb.append(lineTxt);
			}
			reader.close();
			if (sb != null && !sb.toString().equals("")) {
				this.jsonData = JSONObject.parseObject(sb.toString());
			} else {
				this.jsonData = JSONObject.parseObject("{}");
			}
		} catch (Exception e) {
			this.jsonData = JSONObject.parseObject("{}");
		}
	}

	private void saveData() {
		String path = FILE_PATH.substring(0, FILE_PATH.lastIndexOf("/") + 1);
		String name = FILE_PATH.substring(FILE_PATH.lastIndexOf("/") + 1,
				FILE_PATH.length());
		byte[] data = this.jsonData.toString().getBytes();
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			File folder;
			folder = new File(path);
			if (!folder.exists()) // 判断文件目录是否存在
				folder.mkdirs();
			File file = new File(path + File.separator + name);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
