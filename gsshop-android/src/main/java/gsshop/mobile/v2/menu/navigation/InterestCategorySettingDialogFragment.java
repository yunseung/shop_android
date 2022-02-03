package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.gsshop.mocha.ui.util.ViewUtils;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

import static org.zakariya.stickyheaders.SectioningAdapter.TYPE_HEADER;

/**
 * 관심 카테고리 설정.
 */

public class InterestCategorySettingDialogFragment extends DialogFragment {
    private static final String ARG_CATEGORY_JSON = "_arg_category";

    private static final String WISELOG_REPLACE_KEY = "CA0";
    private static final String WISELOG_REPLACE_ON = "CA1";
    private static final String WISELOG_REPLACE_OFF = "CA2";

    private static final int TYPE_HELP = 0x00;
    private static final int TYPE_INTEREST = 0x01;

    private List<String> interestedIds =null;
    public interface OnInterestCategorySettingListener {
        void onCloseCategorySetting();
    }

    private OnInterestCategorySettingListener callback;

    private RecyclerView recyclerView;
    public List<CateContentList> categorySections = new ArrayList<>();

    public static InterestCategorySettingDialogFragment newInstance(String category) {
        InterestCategorySettingDialogFragment fragment = new InterestCategorySettingDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_JSON, category);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonStr = getArguments().getString(ARG_CATEGORY_JSON);

            CateContentList[] sections = new Gson().fromJson(jsonStr, CateContentList[].class);

            // help
            CateContentList help = new CateContentList();
            help.subContentList = new ArrayList<>();
            help.subContentList.add(new CateContentList());
            categorySections.add(help);
            for(CateContentList section: sections) {
                categorySections.add(section);
            }

            String[] ids = new Gson().fromJson(PrefRepositoryNamed.get(MainApplication.getAppContext(),
                    Keys.CACHE.INTEREST_CATEGORY_JSON_STRING, String.class), String[].class);
            if(ids == null) {
                interestedIds = new ArrayList<>();
                return;
            }

            interestedIds = new ArrayList<>(Arrays.asList(ids));
            for (CateContentList section : sections) {
                for (CateContentList category : section.subContentList) {
                    for (String id : ids) {
                        if (category.gb != null && category.gb.equals(id)) {
                            category.isChecked = true;
                            break;
                        }
                    }
                }
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interest_category_setting_dialog, container, false);
        rootView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null) {
                    callback.onCloseCategorySetting();
                }
                dismiss();
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        StickyHeaderLayoutManager mLayoutManager = new StickyHeaderLayoutManager();
        recyclerView.setLayoutManager(mLayoutManager);


        final CategoryAdapter mAdapter = new CategoryAdapter(getContext(), categorySections);
        recyclerView.setAdapter(mAdapter);
        /**
         * 카테고리 구분선 넣기.
         */
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int baseType = mAdapter.getItemViewBaseType(position);
                if (baseType == TYPE_HEADER) {
                    return;
                }

                int next = position + 1;
                if (next == mAdapter.getItemCount()) {
                    return;
                }
                int userType = mAdapter.getItemViewUserType(position);
                int nextBaseType = mAdapter.getItemViewBaseType(next);
                if (userType == TYPE_INTEREST && nextBaseType != TYPE_HEADER) {
                    outRect.bottom = DisplayUtils.convertDpToPx(getContext(), 1);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        getDialog().getWindow().getAttributes().windowAnimations = R.style.FadeInAnimation;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(callback != null) {
            callback.onCloseCategorySetting();
        }
        super.onCancel(dialog);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (OnInterestCategorySettingListener) activity;
        } catch (ClassCastException e) {
            callback = null;
        }
    }

    /**
     * Adapter class
     */
    private class CategoryAdapter extends SectioningAdapter {

        private final Context context;
        List<CateContentList> sections;

        private CategoryAdapter(Context context, List<CateContentList> sections) {
            this.context = context;

            this.sections = sections;


            /**
             * 관심 카테고리 리스트 마자막에 여백 추가.
             */
            CateContentList section = new CateContentList();
            section.title="";
            section.subContentList = new ArrayList<>();
            this.sections.add(section);
        }

        @Override
        public int getSectionItemUserType(int sectionIndex, int itemIndex) {
            return sectionIndex == 0 ? TYPE_HELP : TYPE_INTEREST;
        }

        @Override
        public int getNumberOfSections() {
            return sections.size();
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            List<CateContentList> interests = sections.get(sectionIndex).subContentList;
            return (interests.size() / 2 + interests.size() % 2);
        }

        @Override
        public boolean doesSectionHaveHeader(int sectionIndex) {
            return sectionIndex == 0 ? false : true;
        }

        @Override
        public boolean doesSectionHaveFooter(int sectionIndex) {
            return false;
        }

        @Override
        public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (itemType == TYPE_HELP) {
                View v = inflater.inflate(R.layout.recycler_item_interest_catetory_help, parent, false);
                return new ItemViewHolder(v);
            } else {
                View v = inflater.inflate(R.layout.recycler_item_interest_catetory_row, parent, false);
                return new CategoryViewHolder(v);
            }
        }

        @Override
        public SectionViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycler_item_interest_catetory_header, parent, false);
            return new SectionViewHolder(v);
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
            if (itemType == TYPE_INTEREST) {
                final CategoryViewHolder holder = (CategoryViewHolder) viewHolder;

                // first cateogry item
                int index = itemIndex * 2;
                final CateContentList firstCategory = sections.get(sectionIndex).subContentList.get(index);
                holder.firstContentView.categoryText.setText(firstCategory.title);
                holder.firstContentView.categoryCheck.setChecked(firstCategory.isChecked);
                holder.firstContentView.categoryButton.setContentDescription(firstCategory.title);
                holder.firstContentView.categoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isChecked = !holder.firstContentView.categoryCheck.isChecked();
                        holder.firstContentView.categoryCheck.setChecked(isChecked);
                        firstCategory.isChecked = isChecked;
                        updateCategoryPref(firstCategory.gb, isChecked);
                        CustomToast.makeInterestCategorySetting(context, isChecked, Toast.LENGTH_SHORT).show();

                        //와이즈로그 전송
                        if (!TextUtils.isEmpty(firstCategory.wiseLogUrl)) {
                            String wiselogUrl = isChecked ? firstCategory.wiseLogUrl.replace(WISELOG_REPLACE_KEY, WISELOG_REPLACE_ON) :
                                    firstCategory.wiseLogUrl.replace(WISELOG_REPLACE_KEY, WISELOG_REPLACE_OFF);
                            ((AbstractBaseActivity) context).setWiseLogHttpClient(wiselogUrl);
                        }
                    }
                });


                // second category item
                index++;
                if (index >= sections.get(sectionIndex).subContentList.size()) {
                    holder.secondContentView.itemView.setVisibility(View.INVISIBLE);
                    return;
                }

                ViewUtils.showViews(holder.secondContentView.itemView);
                final CateContentList secondCategory = sections.get(sectionIndex).subContentList.get(index);
                holder.secondContentView.categoryText.setText(secondCategory.title);
                holder.secondContentView.categoryCheck.setChecked(secondCategory.isChecked);
                holder.secondContentView.categoryButton.setContentDescription(secondCategory.title);
                holder.secondContentView.categoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isChecked = !holder.secondContentView.categoryCheck.isChecked();
                        secondCategory.isChecked = isChecked;
                        holder.secondContentView.categoryCheck.setChecked(isChecked);
                        updateCategoryPref(secondCategory.gb, isChecked);
                        CustomToast.makeInterestCategorySetting(context, isChecked, Toast.LENGTH_SHORT).show();

                        //와이즈로그 전송
                        if (!TextUtils.isEmpty(secondCategory.wiseLogUrl)) {
                            String wiselogUrl = isChecked ? secondCategory.wiseLogUrl.replace(WISELOG_REPLACE_KEY, WISELOG_REPLACE_ON) :
                                    secondCategory.wiseLogUrl.replace(WISELOG_REPLACE_KEY, WISELOG_REPLACE_OFF);
                            ((AbstractBaseActivity) context).setWiseLogHttpClient(wiselogUrl);
                        }
                    }
                });
            }
        }

        private void updateCategoryPref(String gb, boolean isChecked) {
            if(isChecked) {
                interestedIds.add(gb);
            } else {
                for(int i = 0; i< interestedIds.size(); i++) {
                    if(interestedIds.get(i).equals(gb)) {
                        interestedIds.remove(i);
                        break;
                    }
                }
            }

            PrefRepositoryNamed.remove(MainApplication.getAppContext(), Keys.CACHE.INTEREST_CATEGORY_JSON_STRING);
            PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.INTEREST_CATEGORY_JSON_STRING,
                    new Gson().toJson(interestedIds.toArray(), String[].class));
        }

        @Override
        public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
            SectionViewHolder holder = (SectionViewHolder) viewHolder;
            holder.titleTextView.setText(sections.get(sectionIndex).title);

        }


    }

    /**
     * View Holder class
     */
    static class CategoryViewHolder extends SectioningAdapter.ItemViewHolder {
        public final CategoryContentView firstContentView;
        public final CategoryContentView secondContentView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ViewGroup group = (ViewGroup) itemView.findViewById(R.id.interest_frame_1st);
            firstContentView = new CategoryContentView(group);
            group = (ViewGroup) itemView.findViewById(R.id.interest_frame_2nd);
            secondContentView = new CategoryContentView(group);
        }
    }

    static class CategoryContentView {
        public final TextView categoryText;
        public final CheckBox categoryCheck;
        public final Button categoryButton;
        public final ViewGroup itemView;

        public CategoryContentView(ViewGroup itemView) {
            this.itemView = itemView;
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.recycler_item_interest_category_content, null, false);
            categoryText = (TextView) view.findViewById(R.id.text_category);
            categoryCheck = (CheckBox) view.findViewById(R.id.check_category);
            categoryButton = (Button) view.findViewById(R.id.button_category);
            itemView.addView(view);
        }
    }

    static class SectionViewHolder extends SectioningAdapter.HeaderViewHolder {
        public final TextView titleTextView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.text_header_title);
        }
    }
}
