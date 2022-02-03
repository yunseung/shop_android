package gsshop.mobile.v2.home.main;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * TextImgModule (GS X 브랜드 및 promotion tier 1 에서 사용
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class TextImageModule {
    public String moduleType = null;
    public String viewType = null;
    public String mseq = null;
    public String wiseLog = null;
    public int shopNumber = 0;
    public ArrayList<SectionContentList> productList = null;
    public String name = null;
    public String imageUrl = null;
    public String linkUrl = null;
    public int height = 0;
    public String title1 = null;
    public String title2 = null;
    public String textColor = null;
    public String bgColor = null;
}
