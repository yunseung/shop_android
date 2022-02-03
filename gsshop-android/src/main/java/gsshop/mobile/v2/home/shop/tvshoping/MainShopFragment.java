package gsshop.mobile.v2.home.shop.tvshoping;

import gsshop.mobile.v2.home.CustomFragment;

/**
 * 메인메장 class
 */
public abstract class MainShopFragment extends CustomFragment {

//    private Runnable reportFeedViewRunnable;
//
//    /**
//     * tensera
//     * @param isVisibleToUser
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if(isVisibleToUser)
//        {
//            try{
//                if(TenseraApi.preloader().isPreloading())
//                {
//                    reportFeedViewRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            TenseraReporterHelper.reportFeedView(getTag());
//                        }
//                    };
//                    TenseraApi.preloader().addOnUserEnterAfterPrerenderCallback(reportFeedViewRunnable);
//                }
//                else
//                {
//                    TenseraReporterHelper.reportFeedView(getTag());
//                }
//            }catch (Exception e)
//            {
//                //무슨일이 있을줄 알고 ..
//            }
//
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }
//
//    /**
//     * tensera
//     */
//    @Override
//    public void onDestroy() {
//        try{
//            if(reportFeedViewRunnable != null)
//            {
//                TenseraApi.preloader().removeOnUserEnterAfterPrerenderCallback(reportFeedViewRunnable);
//            }
//            reportFeedViewRunnable = null;
//        }catch (Exception e)
//        {
//            //무슨일이?
//        }
//        super.onDestroy();
//    }

    /**
     * Fragment 의 포지션
     */
    public int mPosition;

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    abstract public void drawFragment();

    /**
     * back button handling
     */
    public boolean backPressed() {
        return false;
    }
}
