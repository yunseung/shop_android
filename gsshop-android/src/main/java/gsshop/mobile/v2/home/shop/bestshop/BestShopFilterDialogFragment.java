package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import roboguice.util.Ln;


/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link BestShopFilterDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BestShopFilterDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_LIST = "_arg_param_list";
    private static final String ARG_PARAM_TITLE = "_arg_param_title";

    private final int COLOR_SELECTED = Color.parseColor("#111111");
    private final int COLOR_UNSELECTED = Color.parseColor("#777777");

    private static final int DURATION = 680;


    // TODO: Rename and change types of parameters
    private SectionContentList[] filters;
    private int mSelectedPosition = 0;

    private View mFilterView;
    private ListView mListView;

    public BestShopFilterDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BestShopFilterDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BestShopFilterDialogFragment newInstance(List<SectionContentList> param1, String param2) {
        BestShopFilterDialogFragment fragment = new BestShopFilterDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_LIST, new Gson().toJson(param1));
        args.putString(ARG_PARAM_TITLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(ARG_PARAM_LIST);
            filters = new Gson().fromJson(json, SectionContentList[].class);
            String title = getArguments().getString(ARG_PARAM_TITLE);
            for (int i = 0; i < filters.length; i++) {
                if (title.equals(filters[i].productName)) {
                    mSelectedPosition = i;
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_best_shop_filter_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setOnKeyListener(onKeyListener);

        View dimView = view.findViewById(R.id.view_dim);
        dimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btn = (Button) view.findViewById(R.id.button_ok);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = mListView.getFirstVisiblePosition();

                EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(position, filters[position].linkUrl, filters[position].productName));
                //와이즈로그 호출
                ((HomeActivity) getContext()).setWiseLog(filters[position].wiseLog);
                dismiss();
            }
        });

        btn = (Button) view.findViewById(R.id.button_cancel);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mFilterView = view.findViewById(R.id.view_filter);
        mFilterView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (mFilterView.getHeight() > 0) {
                    mFilterView.getViewTreeObserver().removeOnPreDrawListener(this);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mFilterView, "translationY", mFilterView.getHeight(), ViewHelper.getTranslationY(mFilterView));
                    anim.setDuration(DURATION);
                    anim.start();

                }
                return true;
            }
        });

        ListAdapter adapter = new FilterAdapter(getContext(), filters);
        mListView = (ListView) view.findViewById(R.id.list_filter);

        /**
         * empty view
         */
        View emptyView = new View(getContext());
        int emptyHeight = getResources().getDimensionPixelSize(R.dimen.best_shop_filter_top_empty_item) / 2;
        emptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, emptyHeight));
        mListView.addHeaderView(emptyView);
        emptyView = new View(getContext());
        emptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, emptyHeight));
        mListView.addHeaderView(emptyView);
        emptyView = new View(getContext());
        emptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.best_shop_filter_bottom_empty_item)));
        mListView.addFooterView(emptyView);

        mListView.setAdapter(adapter);

        final int ITEM_HEIGHT = getResources().getDimensionPixelSize(R.dimen.best_shop_filter_item);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean aligned;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    aligned = false;
                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!aligned) {
                        int position = view.getFirstVisiblePosition();
                        boolean next = false;
                        if (Math.abs(ViewHelper.getY(view.getChildAt(0))) > ITEM_HEIGHT / 2) {
                            next = true;
                            position++;
                        }

                        mListView.setSelection(position);
                        int count = view.getChildCount();
                        for (int i = 0; i < count; i++) {
                            View child = view.getChildAt(i);
                            if (child instanceof TextView) {
                                if (next && i == 3 || !next && i == 2) {
                                    ((TextView) child).setTextColor(COLOR_SELECTED);
                                } else {
                                    ((TextView) child).setTextColor(COLOR_UNSELECTED);
                                }
                            }
                        }
                    }
                    aligned = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 2;
                if (position >= 0) {
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View child = parent.getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(COLOR_UNSELECTED);
                        }
                    }
                    ((TextView) view).setTextColor(COLOR_SELECTED);
                    mListView.setSelection(position);

                }
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ln.i("position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mListView.setSelection(mSelectedPosition);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        getDialog().getWindow().getAttributes().windowAnimations = R.style.FadeInAnimation;
        super.onActivityCreated(savedInstanceState);
    }


    // back button
    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                dismiss();
                return true;
            }

            return false;
        }
    };

    @Override
    public void dismiss() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mFilterView, "translationY", ViewHelper.getTranslationY(mFilterView), mFilterView.getHeight());
        anim.setDuration(DURATION);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                BestShopFilterDialogFragment.super.dismiss();
            }


        });
    }

    private class FilterAdapter extends ArrayAdapter<SectionContentList> {


        public FilterAdapter(Context context, SectionContentList[] objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_best_shop_filter, parent, false);
            }

            TextView textView = (TextView) convertView;
            textView.setText(getItem(position).productName);
            if (position == mListView.getFirstVisiblePosition()) {
                textView.setTextColor(COLOR_SELECTED);
            } else {
                textView.setTextColor(COLOR_UNSELECTED);
            }


            return convertView;
        }
    }

}
