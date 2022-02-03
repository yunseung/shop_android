package gsshop.mobile.v2.home.shop.event.renewal.Holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.ltype.BaseRollViewHolderLtype;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

public class BanImgTxtGbaVH extends BaseRollViewHolderLtype {

    /**
     * 메인 이미지
     */
    private final ImageView mImgThumb;
    /**
     * 아래 txt
     */
    private final TextView mTxtMain, mTxtSub;

    /**
     * 최 상단 루트뷰
     */
    private final View mRootView;

    /**
     * @param itemView
     */
    public BanImgTxtGbaVH(View itemView) {
        super(itemView);
        mRootView = itemView.findViewById(R.id.root_view);
        mImgThumb = itemView.findViewById(R.id.img_sumbnail);
        mTxtMain = itemView.findViewById(R.id.txt_main);
        mTxtSub = itemView.findViewById(R.id.txt_sub);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);

        try {
            ModuleList content = moduleList.get(position);
            mTxtMain.setText(content.title1);
            mTxtSub.setText(content.title2);
            ImageUtil.loadImageFit(context, content.imageUrl, mImgThumb, R.drawable.noimage_375_188);

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity) context).setWiseLog(content.wiseLog);

                    WebUtils.goWeb(context, content.linkUrl);
                }
            });

            // 해당 홀더 전 홀더 타입이 앵커이거나 다음 홀더 타입이 앵커일 경우 padding을 더해 준다.
            int topPadding = 0, bottomPadding = 0;
            if (position > 0 && (moduleList.get(position - 1).tempViewType == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA ||
                    moduleList.get(position - 1).tempViewType == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST)) {
                topPadding = 4;
            }
            if (position + 1 < moduleList.size() && (moduleList.get(position + 1).tempViewType == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA ||
                    moduleList.get(position + 1).tempViewType == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST)) {
                bottomPadding = 4;
            }

            boolean isExist = false;
            for (int i = position + 1; i < moduleList.size(); i++) {
                if (moduleList.get(i).tempViewType == ViewHolderType.VIEW_TYPE_IMG_TXT_GBA) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                bottomPadding = 16;
            }

            if (topPadding > 0 || bottomPadding > 0) {
                mRootView.setPadding(mRootView.getPaddingLeft(), DisplayUtils.convertDpToPx(context, topPadding),
                        mRootView.getPaddingRight(), DisplayUtils.convertDpToPx(context, bottomPadding));
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
            return;
        }
        catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
            return;
        }
    }

}
