/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import android.content.Context;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.WebError;
import roboguice.inject.ContextSingleton;

/**
 * 첨부파일 저장(쇼미카페, 이벤트 등) 커넥터.
 *
 */
@ContextSingleton
public class FileAttachConnector {

	/**
	 * context
	 */
	@Inject
	private Context context;

	/**
	 * restClient
	 */
    @Inject
    private RestClient restClient;

	/**
	 * 이벤트 동영상 업로드 타임아웃 (5분)
	 */
	private static final int VIDEO_UPLOAD_TIMEOUT = 300000;

	/**
	 * 서버에 파일을 업로드한다.
	 *
	 * @param formData
	 * @param uploadUrl
	 * @param isEventMovie
	 * @return FileAttachResult
	 * @throws RestClientException RestClientException
	 * @throws URISyntaxException URISyntaxException
     */
    public ShowmeAttachResult saveFiles(MultiValueMap<String, Object> formData, String uploadUrl, boolean isEventMovie)
            throws RestClientException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);

		ShowmeAttachResult rslt;

		if (isEventMovie) {
			//스트림 방식으로 파일 전송
			List<ClientHttpRequestInterceptor> clientHttpRequestInterceptor = restClient.getInterceptors();
			restClient.setInterceptors(null);
			SimpleClientHttpRequestFactory clientHttpRequestFactory = (SimpleClientHttpRequestFactory) restClient.getRequestFactory();
			clientHttpRequestFactory.setBufferRequestBody(false);
			clientHttpRequestFactory.setReadTimeout(VIDEO_UPLOAD_TIMEOUT);
			restClient.setRequestFactory(clientHttpRequestFactory);

			rslt = restClient.postForObject(new URI(uploadUrl), requestEntity, ShowmeAttachResult.class);

			//설정원복
			clientHttpRequestFactory.setBufferRequestBody(true);
			clientHttpRequestFactory.setReadTimeout(context.getResources().getInteger(R.integer.mc_asyncimage_read_timeout));
			restClient.setRequestFactory(clientHttpRequestFactory);
			restClient.setInterceptors(clientHttpRequestInterceptor);
		} else {
			rslt = restClient.postForObject(new URI(uploadUrl), requestEntity, ShowmeAttachResult.class);
		}

		return rslt;
    }

    /**
     * 서버에 파일을 업로드한다. (모바일상담용)
     * 
     * @param formData
     * @param uploadUrl
     * @return MobileTalkSendResult
     * @throws RestClientException
     * @throws URISyntaxException
     */
    public MobileTalkSendResult saveFile(MultiValueMap<String, Object> formData, String uploadUrl)
            throws RestClientException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);

        return restClient.postForObject(new URI(uploadUrl), requestEntity, MobileTalkSendResult.class);
    }
    
    /**
     * 서버에 메시지를 전송한다.
     * 
     * @param formData
     * @param saveTalkUrl
     * @return MobileTalkStartResult
     * @throws RestClientException
     * @throws URISyntaxException
     */
    public MobileTalkStartResult startTalk(MultiValueMap<String, Object> formData, String saveTalkUrl)
    		throws RestClientException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        //모바일상담 서버와 캐릭터셋 UTF-8로 통일
        MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));
        headers.setContentType(mediaType);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);
        
        return restClient.postForObject(new URI(saveTalkUrl), requestEntity, MobileTalkStartResult.class);
    }

    /**
     * 서버에 등록된 메시지를 삭제한다.
     * 
     * @param formData
     * @param deleteTalkUrl
     * @return MobileTalkResult
     * @throws RestClientException
     * @throws URISyntaxException
     */
    public MobileTalkResult deleteTalk(MultiValueMap<String, Object> formData, String deleteTalkUrl)
    		throws RestClientException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);

        return restClient.postForObject(new URI(deleteTalkUrl), requestEntity, MobileTalkResult.class);
    }

    /**
	 * ShowmeAttachResult
	 */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class ShowmeAttachResult {

		/**
		 * result
		 */
        public String result;
		/**
		 * tmpFileName
		 */
        public String tmpFileName;
		/**
		 * realFileName
		 */
        public String realFileName;
		/**
		 * fileUrl
		 */
        public String fileUrl;

		/**
		 * fileSize
		 */
		public String fileSize;

		/**
		 * imgType
		 */
		public String imgType;

		/**
		 * 동영상 업로드시 썸네일 경로를 받아서,
		 */
		public String thumbnail;

        /**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}

		/**
		 * @param result the result to set
		 */
		public void setResult(String result) {
			this.result = result;
		}

		/**
		 * @return the tmpFileName
		 */
		public String getTmpFileName() {
			return tmpFileName;
		}

		/**
		 * @param tmpFileName the tmpFileName to set
		 */
		public void setTmpFileName(String tmpFileName) {
			this.tmpFileName = tmpFileName;
		}

		/**
		 * @return the realFileName
		 */
		public String getRealFileName() {
			return realFileName;
		}

		/**
		 * @param realFileName the realFileName to set
		 */
		public void setRealFileName(String realFileName) {
			this.realFileName = realFileName;
		}

		/**
		 * @return the fileUrl
		 */
		public String getFileUrl() {
			return fileUrl;
		}

		/**
		 * @param fileUrl the fileUrl to set
		 */
		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		/**
		 * getImgType
		 *
		 * @return imgType
         */
		public String getImgType() {
			return imgType;
		}

		/**
		 * setImgType
		 *
		 * @param imgType imgType
         */
		public void setImgType(String imgType) {
			this.imgType = imgType;
		}

		/**
		 * getThumbnail
		 *
		 * @return thumbnail
		 */
		public String getThumbnail() {
			return thumbnail;
		}

		/**
		 * setThumbnail
		 *
		 * @param thumbnail thumbnail
		 */
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}


	}

    /**
	 * FileAttachResult
	 */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class FileAttachResult {

		/**
		 * rtnUrl
		 */
        private String rtnUrl;

		/**
		 * webError
		 */
        private WebError webError;

        /**
		 * 모바일상담 리턴 변수
		 */
		private int error_code = -1;
		/**
		 * error_message
		 */
        private String error_message;
		/**
		 * level
		 */
        private String level;

        /**
		 * 쇼미첨부 리턴 변수
		 */
		public String result;
		/**
		 * fileName
		 */
        public String fileName;
        
        /**
         * getRtnUrl
		 *
		 * @return the rtnUrl
         */
        public String getRtnUrl() {
            return rtnUrl;
        }

        /**
         * setRtnUrl
		 * @param rtnUrl the rtnUrl to set
         */
        public void setRtnUrl(String rtnUrl) {
            this.rtnUrl = rtnUrl;
        }

        /**
         * @return the webError
         */
        public WebError getWebError() {
            return webError;
        }

        /**
         * @param webError the webError to set
         */
        public void setWebError(WebError webError) {
            this.webError = webError;
        }

		/**
		 * @return the error_code
		 */
		public int getError_code() {
			return error_code;
		}

		/**
		 * @param error_code the error_code to set
		 */
		public void setError_code(int error_code) {
			this.error_code = error_code;
		}

		/**
		 * @return the error_message
		 */
		public String getError_message() {
			return error_message;
		}

		/**
		 * @param error_message the error_message to set
		 */
		public void setError_message(String error_message) {
			this.error_message = error_message;
		}

		/**
		 * @return the level
		 */
		public String getLevel() {
			return level;
		}

		/**
		 * @param level the level to set
		 */
		public void setLevel(String level) {
			this.level = level;
		}

		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}

		/**
		 * @param result the result to set
		 */
		public void setResult(String result) {
			this.result = result;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @param fileName the fileName to set
		 */
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		
    }
}
