package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.WebUtils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 메세지카드 레이아웃 뷰
 */
public class MassageCardLayout extends RelativeLayout {

    private Context mContext;

    public MassageCardLayout(Context context) {
        super(context);
        initView(context);
    }

    public MassageCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MassageCardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    private void initView(Context context) {
        mContext = context;
    }

    public void setImage(String url, final String link){
        ImageView image = (ImageView)findViewById(R.id.image);
        Glide.with(mContext).load(trim(url)).diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new RoundedCornersTransformation(mContext,25,0))
        .into(new GlideDrawableImageViewTarget(image) {
            @Override
            public void onResourceReady(final GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> animation) {
                view.setImageResource(android.R.color.transparent);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                super.onResourceReady(resource, animation);

            }
        });


//                into(image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                EventBus.getDefault().post(new Events.NavigationCloseEvent());
                WebUtils.goWeb(mContext, link);
            }
        });
    }

    public void open(){
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1, 1),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1, 1),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1, 1)
        );

        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }
    public void close(){
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("scaleX", 1, 0.8f, 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 1, 0.8f, 0.8f),
                PropertyValuesHolder.ofFloat("alpha", 1, 0.0f, 0.0f)
        );
        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }

}
