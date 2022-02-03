package gsshop.mobile.v2.home.main;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 이런상품 어떠세요 몰로코/GSSHOP 효율
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MktInfo {
    public String impMseq; // GSSHOP상품 2초 봤을때
    public String sourceType; // Moloco, GSSHOP 구분
    public String mktType; // 광고, 추천 구분
    public String impTracker; // Moloco상품 2초 봤을때
    public String clickTracker; // Moloco상품 클릭시
}
