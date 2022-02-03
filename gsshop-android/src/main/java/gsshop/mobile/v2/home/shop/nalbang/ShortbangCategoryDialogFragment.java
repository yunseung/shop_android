package gsshop.mobile.v2.home.shop.nalbang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 쑛방카테고리 보기.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShortbangCategoryDialogFragment extends DialogFragment {
    private static final String ARG_CATEGORY = "_arg_category";

    private static final int VIDEO_COLUMN_COUNT = 3;
    private static final int VIDEO_OUTER_BLANK_SPACE;
    private static final int VIDEO_INNER_BLANK_SPACE;

    private static final int CATEGORY_OUTER_BLANK_SPACE;
    private static final int CATEGORY_INNER_BLANK_SPACE;

    static {
        VIDEO_OUTER_BLANK_SPACE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.shortbang_video_outer_blank_space);
        VIDEO_INNER_BLANK_SPACE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.shortbang_video_inner_blank_space);

        CATEGORY_OUTER_BLANK_SPACE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.shortbang_category_outer_blank_space);
        CATEGORY_INNER_BLANK_SPACE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.shortbang_category_inner_blank_space);
    }

    private List<SectionContentList> categoryData;
    private View emptyView;
    private String categoryGb;

    interface OnShortbangCategoryListenr {
        void onSelectVideo(String linkUrl, String thumbnail);
        void onCloseCategory(boolean isBackButton);
        void onSendWiseLog(String url);
    }

    private OnShortbangCategoryListenr callback;

    private RecyclerView categoryRecycler;
    private RecyclerView videoRecycler;

    private int selectedCategoryIndex = 0;

    public static ShortbangCategoryDialogFragment newInstance(String category) {
        ShortbangCategoryDialogFragment fragment = new ShortbangCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryGb = getArguments().getString(ARG_CATEGORY);
        }

        categoryData = CategoryDataHolder.getCategoryData();

        for(int i=0; i < categoryData.size(); i++) {
            SectionContentList data = categoryData.get(i);
//            sbCateGb
            if (TextUtils.isEmpty(categoryGb)) {
                break;
            }
            if(categoryGb.equals(data.sbCateGb)) {
                selectedCategoryIndex = i;
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shortbang_category_dialog, container, false);
        rootView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCloseCategory(false);
                dismiss();
            }
        });

        categoryRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_category);
        videoRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_video);
        emptyView = rootView.findViewById(R.id.view_shortbang_empty);
        ViewUtils.hideViews(categoryRecycler, videoRecycler, emptyView);

        new BaseAsyncController<Void>(getActivity()) {
            @Override
            protected Void process() throws Exception {
                for(SectionContentList data : categoryData) {
                    // on image
                    if (data.imageList.on.startsWith("http://") || data.imageList.on.startsWith("https://")) {
                        data.imageList.on = Glide.with(getActivity()).load(trim(data.imageList.on)).downloadOnly(50, 74).get().getAbsolutePath();
                    }

                    if (data.imageList.off.startsWith("http://") || data.imageList.off.startsWith("https://")) {
                        data.imageList.off = Glide.with(getActivity()).load(trim(data.imageList.off)).downloadOnly(50, 74).get().getAbsolutePath();
                    }
                }
                return null;
            }

            @Override
            protected void onSuccess(Void aVoid) throws Exception {
                super.onSuccess(aVoid);
                setup();
            }
        }.execute();

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        getDialog().getWindow().getAttributes().windowAnimations = R.style.FadeInAnimation;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnShortbangCategoryListenr) activity;
        } catch (ClassCastException e) {
            callback = null;
        }
    }


    public void setup() {
        /**
         * category recycler
         */

        ViewUtils.showViews(categoryRecycler, videoRecycler, emptyView);

        categoryRecycler.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        categoryRecycler.setLayoutManager(llm);
        final RecyclerView.Adapter categoryAdapter = new RecyclerView.Adapter<CategoryViewHolder>() {
            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_shortbang_category, viewGroup, false);
                return new CategoryViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(CategoryViewHolder holder, int position) {
                holder.onBindHolder(position);
            }

            @Override
            public int getItemCount() {
                return categoryData.size();
            }
        };

        /**
         * 아이템간격을 데코레이션 한다.
         *
         */
        categoryRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildAdapterPosition(view);
                outRect.right = CATEGORY_INNER_BLANK_SPACE;
                if (position == 0) {
                    outRect.left = CATEGORY_OUTER_BLANK_SPACE;
                } else if(position == categoryAdapter.getItemCount() -1) {
                    outRect.right = CATEGORY_OUTER_BLANK_SPACE;
                }
            }
        });


        categoryRecycler.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            categoryRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            categoryRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        if (categoryRecycler.getChildCount() > 0) {
                            View child = categoryRecycler.getChildAt(0).findViewById(R.id.content_frame);
                            int height = child.getHeight();
                            ViewGroup.LayoutParams params = categoryRecycler.getLayoutParams();
                            params.height = height;
                            categoryRecycler.setLayoutParams(params);
                            categoryRecycler.requestLayout();
                        }
                    }
                });

        categoryRecycler.setAdapter(categoryAdapter);

        // video recycler
        videoRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildAdapterPosition(view);


                outRect.right = VIDEO_INNER_BLANK_SPACE / 2;
                outRect.bottom = VIDEO_INNER_BLANK_SPACE;

                // outer space
                if (position % VIDEO_COLUMN_COUNT == 0) {
                    outRect.left = VIDEO_OUTER_BLANK_SPACE;
                } else {
                    outRect.left = VIDEO_INNER_BLANK_SPACE / 2;
                }

                if (position < VIDEO_COLUMN_COUNT) {
                    outRect.top = VIDEO_OUTER_BLANK_SPACE;
                }
                if (position % VIDEO_COLUMN_COUNT == VIDEO_COLUMN_COUNT-1) {
                    outRect.right = VIDEO_OUTER_BLANK_SPACE;
                }

                int itemCount = videoRecycler.getAdapter().getItemCount();
                if(itemCount-position <= VIDEO_COLUMN_COUNT) {
                    outRect.bottom = VIDEO_OUTER_BLANK_SPACE;
                }

            }
        });

        GridLayoutManager slm = new GridLayoutManager(getActivity(), VIDEO_COLUMN_COUNT);
        videoRecycler.setLayoutManager(slm);


        final RecyclerView.Adapter videoAdapter = new RecyclerView.Adapter<VideoViewHolder>() {
            @Override
            public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_shortbang_shop, viewGroup, false);
                return new VideoViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(VideoViewHolder holder, int position) {
                holder.onBindHolder(position);

            }

            @Override
            public int getItemCount() {
                int size = categoryData.get(selectedCategoryIndex).subProductList.size();
                if(size > 0) {
                    ViewUtils.hideViews(emptyView);
                } else {
                    ViewUtils.showViews(emptyView);
                }
                return size;
            }
        };
        videoRecycler.setAdapter(videoAdapter);


        categoryRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                videoRecycler.stopScroll();
                selectedCategoryIndex = position;
                categoryAdapter.notifyDataSetChanged();
                videoAdapter.notifyDataSetChanged();
                videoRecycler.getLayoutManager().scrollToPosition(0);
                callback.onSendWiseLog(categoryData.get(selectedCategoryIndex).wiseLog);
            }

        }));

        videoRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String linkUrl = categoryData.get(selectedCategoryIndex).subProductList.get(position).linkUrl;
                String thumbnail = categoryData.get(selectedCategoryIndex).subProductList.get(position).imageUrl;
                playShortbang(linkUrl, thumbnail);
                //와이즈로그 전송
                callback.onSendWiseLog(ServerUrls.WEB.SHORTBANG_CATEGORY_VIDEO);
                dismiss();
            }
        }));
    }

    public void playShortbang(String linkUrl, String thumbnail) {
        getActivity().runOnUiThread(() -> callback.onSelectVideo(linkUrl, thumbnail));
    }

    /**
     * 하드웨어 백키 클릭시 콜백
     *
     * @param dialog DialogInterface
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        callback.onCloseCategory(true);
        dismiss();
    }

    /**
     * category recycler view holder
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImage;
        private final View pointView;
        private final TextView categoryText;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            iconImage = (ImageView) itemView.findViewById(R.id.image_icon);
            pointView = itemView.findViewById(R.id.view_point);
            categoryText = (TextView) itemView.findViewById(R.id.text_category);

            DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), iconImage);
            DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), iconImage);

        }

        public void onBindHolder(final int position) {
            SectionContentList item = categoryData.get(position);

            if (selectedCategoryIndex == position) {
                ViewUtils.showViews(pointView);
                String path = item.imageList.on;
                if(path != null && path.length() > 0) {
                    iconImage.setImageDrawable(Drawable.createFromPath(path));
                }
            } else {
                ViewUtils.hideViews(pointView);
                String path = item.imageList.off;
                if(path != null && path.length() > 0) {
                    iconImage.setImageDrawable(Drawable.createFromPath(path));
                }
            }
        }
    }

    /**
     * video recycler view holder
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final ImageView thumbImage;


        public VideoViewHolder(View itemView) {
            super(itemView);
            thumbImage = (ImageView) itemView.findViewById(R.id.image_thumb);
            titleText = (TextView) itemView.findViewById(R.id.text_title);

            DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), thumbImage);
        }

        public void onBindHolder(final int position) {
            SectionContentList category = categoryData.get(selectedCategoryIndex);
            SectionContentList video = category.subProductList.get(position);

            ImageUtil.loadImageFit(getActivity(), video.imageUrl, thumbImage, R.drawable.noimg_logo);
            titleText.setText(video.promotionName);

        }

    }

}
