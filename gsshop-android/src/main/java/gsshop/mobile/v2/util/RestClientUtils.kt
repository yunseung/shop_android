package gsshop.mobile.v2.util

import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.mocha.core.exception.SystemException
import com.gsshop.mocha.network.rest.RestClient
import gsshop.mobile.v2.ServerUrls
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.client.RestClientException
import roboguice.util.Ln
import java.net.URI
import java.net.URISyntaxException
import java.util.regex.Pattern

object RestClientUtils {

    /**
     * API base url
     * -디폴트는 http 사용,
     * -https 사용하는 경우 post, get 함수호출시 파라미터내에 https가 포함된 full url 넘김
     */
    private val BASE_URL = ServerUrls.HTTP_ROOT

    /**
     * GET 방식 파라미터 교체용 정규식
     */
    private const val regularExp = "\\{[^&/]*\\}"

    /**
     * REST POST 함수
     *
     * @param restClient RestClient
     * @param param Body에 추가할 파라미터
     * @param url API 주소
     * @param clazz response type
     * @param <T> element type
     * @return API 호출 결과
     */
    fun <T> post(restClient: RestClient, param: Any?, url: String, clazz: Class<T>, vararg params: String): T? {
        var result: T?
        try {
            val headers = HttpHeaders()
            headers["Connection"] = "Close"
            headers.contentType = MediaType.APPLICATION_JSON
            val requestEntity = HttpEntity(param, headers)
            result = restClient.postForObject(URI(getBaseUrl(replaceUrl(url, *params))), requestEntity, clazz)
        } catch (re: RestClientException) {
            throw re
        } catch (ue: URISyntaxException) {
            throw SystemException(ue)
        } catch (ie: IllegalArgumentException) {
            //responseType(clazz is null)
            throw ie
        } catch (he: HttpMessageNotReadableException) {
            //서버는 json 주지만 clazz 타입이 String인 경우 등
            throw he
        }
        return result
    }

    fun <T> post(restClient: RestClient, param: Any?, url: String, clazz: Class<T>): T? {
        return post(restClient, param, url, clazz, "")
    }

    /**
     * REST GET 함수
     *
     * @param restClient RestClient
     * @param url API 주소
     * @param clazz response type
     * @param params {}필드 교체값
     * @param <T> element type
     * @return API 호출 결과
     */
    fun <T> get(restClient: RestClient, url: String, clazz: Class<T>, vararg params: String): T? {
        var result: T?
        result = try {
            restClient.getForObject(URI(getBaseUrl(replaceUrl(url, *params))), clazz)
        } catch (re: RestClientException) {
            throw re
        } catch (ue: URISyntaxException) {
            throw SystemException(ue)
        } catch (ie: IllegalArgumentException) {
            //responseType(clazz is null)
            throw ie
        } catch (he: HttpMessageNotReadableException) {
            //서버는 json 주지만 clazz 타입이 String인 경우 등
            throw he
        }
        return result
    }

    fun <T> get(restClient: RestClient, url: String, clazz: Class<T>): T? {
        return get(restClient, url, clazz, "")
    }

    /**
     * url정보의 파라미터를 교체 후 리턴한다.
     *
     * @param url API 주소
     * @param params {}필드 교체값
     * @return 치환된 API 주소
     */
    private fun replaceUrl(url: String, vararg params: String): String {
        if (EmptyUtils.isEmpty(url)) {
            return ""
        }
        val pattern = Pattern.compile(regularExp)
        val matcher = pattern.matcher(url)
        val replacedString = StringBuffer()
        var i = 0
        while (matcher.find()) {
            //교체할 파라미터가 대상보다 적은경우 스킵
            if (EmptyUtils.isEmpty(params) || params.size <= i) {
                break
            }
            matcher.group()
            matcher.appendReplacement(replacedString, params[i++])
        }
        matcher.appendTail(replacedString)
        return replacedString.toString()
    }

    /**
     * url 값에 base url 정보가 포함되었는지 확인하여 리턴한다.
     *
     * @param url API 주소
     * @return 수정된 API 주소
     */
    private fun getBaseUrl(url: String): String {
        if (EmptyUtils.isEmpty(url)) {
            return ""
        }
        return if (url.startsWith(ServerUrls.HTTPS) || url.startsWith(ServerUrls.HTTP)) {
            url
        } else {
            BASE_URL + url
        }
    }
}