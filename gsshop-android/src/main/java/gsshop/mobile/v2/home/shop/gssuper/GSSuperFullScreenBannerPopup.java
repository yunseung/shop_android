package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class GSSuperFullScreenBannerPopup extends AbstractBaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.popup_gs_super_full);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_products);
        View btnClose = (View) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        //배경 DIM 처리
        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        param.dimAmount = 0.6f;
        getWindow().setAttributes(param);

        setPopupLists();
    }

    private void setPopupLists() {
        Bundle bundle = getIntent().getExtras();
        ArrayList<GSSuperPopupProductsInfo> arrayListGSSuperPopupInfo =
                bundle.getParcelableArrayList(Keys.INTENT.INTENT_DATA_GSSUPER_POPUP);

        AdapterGSBannerPopup adapter = new AdapterGSBannerPopup(
                GSSuperFullScreenBannerPopup.this, arrayListGSSuperPopupInfo);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
    }

    private class ViewHolderGSBannerPopup extends RecyclerView.ViewHolder {

        ImageView mImageMain;

        public ViewHolderGSBannerPopup(View itemView) {
            super(itemView);
            mImageMain = (ImageView) itemView.findViewById(R.id.main_img);
        }
    }

    private class AdapterGSBannerPopup extends RecyclerView.Adapter<ViewHolderGSBannerPopup> {

        private List<GSSuperPopupProductsInfo> mArrPopupInfo = null;
        private final Context mContext;

        public AdapterGSBannerPopup(Context context, List<GSSuperPopupProductsInfo> info) {
            mContext = context;
            mArrPopupInfo = info;
        }

        @Override
        public ViewHolderGSBannerPopup onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_row_type_fx_image_banner_no_padding, parent, false);

            return new ViewHolderGSBannerPopup(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolderGSBannerPopup holder, final int position) {
            int bannerHeight = DeviceUtils.getDeviceWidth(mContext) * 2 / 3;
            holder.mImageMain.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, bannerHeight));

            ImageUtil.loadImageResize(mContext, mArrPopupInfo.get(position).imageUrl,
                    holder.mImageMain, R.drawable.noimg_logo);

            holder.mImageMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(mContext, mArrPopupInfo.get(position).linkUrl);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mArrPopupInfo != null) {
                return mArrPopupInfo.size();
            }
            return 0;
        }
    }

}
