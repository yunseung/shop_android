package gsshop.mobile.v2.support.share;


public class ShareFactory {

    public static ShareInterface getShareProvider(ShareInfo shareInfo){

        String target = shareInfo.getTarget();

        if ("facebook".equals(target)){//페이스북
            return new FacebookShareProvider(shareInfo);
        }else if("twitter".equals(target)){//트위터
            return new TwitterShareProvider(shareInfo);
        }else if("pinterest".equals(target)){//핀터레스트
            return new PinterestShareProvider(shareInfo);
        }else if("line".equals(target)){//라인
            return new LineShareProvider(shareInfo);
        }else if("kakaotalk".equals(target)){//카카오톡
            return new KaKaoTalkShareProvider(shareInfo);
        }else if("kakaostory".equals(target)){//카카오스토리
            return new KaKaoStoryShareProvider(shareInfo);
        }else if("etc".equals(target) || "".equals(target)){//기타공유
            return new EtcShareProvider(shareInfo);
        }

        return null;
    }

}
