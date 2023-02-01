package com.scbe.jpa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Util {
	
	public String alg = "AES/CBC/PKCS5Padding";
	public String key = "12345678910111213";
    public String iv = key.substring(0, 16); // 16byte

    /**
     * SHA512 암호화
     * @param str
     * @return
     */
	public String encryptSHA512(String str) throws Exception {
		String sha = ""; 
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(str.getBytes("utf8"));
			sha = String.format("%0128x", new BigInteger(1, digest.digest()));
		}catch(NoSuchAlgorithmException e){
			//e.printStackTrace(); 
			System.out.println("Encrypt Error - NoSuchAlgorithmException");
			sha = null; 
		}catch (Exception e) {
			System.out.println("Encrypt Error - Exception");
			sha = null; 
		}
		return sha;
	}
	
	/**
	 * AES256 암호화
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

	/**
	 * AES256 복호화
	 * @param cipherText
	 * @return
	 * @throws Exception
	 */
    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
    
    /**
     * HttpURLConnection post
     * @param targetUrl
     * @param requestMap
     * @return
     * @throws Exception
     */
    public String postRequest(String targetUrl, Map<String, Object> requestMap) throws Exception {

		String response = "";

		try {
			URL url = new URL(targetUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST"); // 전송 방식
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setConnectTimeout(20000); // 연결 타임아웃 설정(20초) 
			conn.setReadTimeout(20000); // 읽기 타임아웃 설정(20초)
			conn.setDoOutput(true);	// URL 연결을 출력용으로 사용(true)
			
			String requestBody = getJsonStringFromMap(requestMap);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			bw.write(requestBody);
			bw.flush();
			bw.close();

			Charset charset = Charset.forName("UTF-8");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			
			String inputLine;			
			StringBuffer sb = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
			
			response = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
    
    /**
     * map -> json 변환
     * @param map
     * @return
     * @throws Exception
     */
    public String getJsonStringFromMap(Map<String, Object> map) throws Exception {
		JSONObject json = new JSONObject();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
            Object value = entry.getValue();
            json.put(key, value);
        }
        return json.toJSONString();
	}
    
    /**
     * json -> map 변환
     * @param json
     * @return
     * @throws Exception
     */
    public Map<String, Object> getMapFromJsonString(String json) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    }
    
}
