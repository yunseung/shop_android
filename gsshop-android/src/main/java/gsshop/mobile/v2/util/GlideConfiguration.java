/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;


/**
 *
 *
 */
public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    /**
     * tensera
     * 2. Add the 2 attached classes: TenseraOkHttpInterceptor,
     * TenseraOkHttpCacheResponseBody to the project
     * 3. Modify gsshop.mobile.v2.util. GlideConfiguration class:
     */
    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.addInterceptor(new TenseraOkHttpInterceptor());
//        glide.register(GlideUrl.class, InputStream.class, new
//                OkHttpUrlLoader.Factory(builder.build()));
    }
}
