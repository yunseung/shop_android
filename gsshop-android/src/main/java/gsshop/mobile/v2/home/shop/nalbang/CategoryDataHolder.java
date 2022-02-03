package gsshop.mobile.v2.home.shop.nalbang;

import java.util.List;

import gsshop.mobile.v2.home.main.SectionContentList;

public class CategoryDataHolder {

    private static List<SectionContentList> mCategoryData = null;

    public static void putCategoryData(List<SectionContentList>  data){
        mCategoryData = data;
    }

    public static List<SectionContentList> getCategoryData(){
        return mCategoryData;
    }

}
