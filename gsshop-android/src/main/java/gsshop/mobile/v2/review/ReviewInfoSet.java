package gsshop.mobile.v2.review;

import gsshop.mobile.v2.WebError;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ReviewInfoSet {

    private String _TEMPLATE_KEY = ""; // 에러 여부확인
    private ReviewInfoV2 _CONTENT_KEY;
    private WebError webError;
    private String _VIEW_FILE_PATH = "";
    private String hidden_name_0 = "";

    /**
     * @return the hidden_name_0
     */
    public String getHidden_name_0() {
        return hidden_name_0;
    }

    /**
     * @param hidden_name_0 the hidden_name_0 to set
     */
    public void setHidden_name_0(String hidden_name_0) {
        this.hidden_name_0 = hidden_name_0;
    }

    /**
     * @return the _TEMPLATE_KEY
     */
    public String get_TEMPLATE_KEY() {
        return _TEMPLATE_KEY;
    }

    /**
     * @param _TEMPLATE_KEY the _TEMPLATE_KEY to set
     */
    public void set_TEMPLATE_KEY(String _TEMPLATE_KEY) {
        this._TEMPLATE_KEY = _TEMPLATE_KEY;
    }

    /**
     * @return the _CONTENT_KEY
     */
    public ReviewInfoV2 get_CONTENT_KEY() {
        return _CONTENT_KEY;
    }

    /**
     * @param _CONTENT_KEY the _CONTENT_KEY to set
     */
    public void set_CONTENT_KEY(ReviewInfoV2 _CONTENT_KEY) {
        this._CONTENT_KEY = _CONTENT_KEY;
    }

    /**
     * @return the _VIEW_FILE_PATH
     */
    public String get_VIEW_FILE_PATH() {
        return _VIEW_FILE_PATH;
    }

    /**
     * @param _VIEW_FILE_PATH the _VIEW_FILE_PATH to set
     */
    public void set_VIEW_FILE_PATH(String _VIEW_FILE_PATH) {
        this._VIEW_FILE_PATH = _VIEW_FILE_PATH;
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

}
