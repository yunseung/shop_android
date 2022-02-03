package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.InjectResource;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * Created by jhkim on 16. 2. 24..
 * IntroImageCommand
 */
public class IntroImageCommand extends BaseCommand {
    /**
     * IntroImageCommand restClient
     */
    @Inject
    private RestClient restClient;

    /**
     * IntroImageCommand connectionTimeout
     */
    @InjectResource(R.integer.update_check_connection_timeout)
    private int connectionTimeout;

    /**
     * 인트로 이미지 받아오는 api 호출
     * 데이터 받아서 preference에 저장
     * api 통신 후 받아온 이미지 URL, 시작일, 종료일, 수정일 preference에 저장, 저장할 때 값 비교하고 저장
     * 저장된 pref 없는 경우 api 통신
     * 있더라도 modify가 다르면 api 통신
     *
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(final Activity activity, CommandChain chain) {
        super.injectMembers(activity);

        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                // /data/data/gsshop.mobile.v2/files/intro.png
                String fileDir = activity.getFilesDir() + File.separator + activity.getResources().getString(R.string.intro_img_filename);

                IntroImageInfo imageInfo = getIntroImage(activity);
                /**
                 * 인트로 등급 메시지
                 */
                IntroActivity introActivity = (IntroActivity) activity;
                introActivity.appIntroTxt = imageInfo.appIntroTxt;

                LoginOption option = LoginOption.get();
                if (option == null || (option != null && !option.keepLogin)) {
                    ThreadUtils.INSTANCE.runInUiThread(()->{
                        try{
                            ViewUtils.showViews(introActivity.roundLineView);
                            for (IntroImageInfo.AppIntroTxt introTxt : imageInfo.appIntroTxt) {
                                if ("BASIC".equalsIgnoreCase(introTxt.grade)) {
                                    introActivity.messageText.setText(introTxt.subTitle);
                                    if (!TextUtils.isEmpty(introTxt.fontColor))
                                        introActivity.messageText.setTextColor(Color.parseColor("#" + introTxt.fontColor));
                                }
                            }
                        }catch (Exception e)
                        {
                            Ln.e(e);
                        }
                    });
                }

                if (null == PrefRepositoryNamed.get(MainApplication.getAppContext(),
                        Keys.PREF.INTRO_IMAGE_INFO, IntroImageInfo.class) ) {
                    savePref(activity, imageInfo, fileDir);
                } else {
                    IntroImageInfo introImageInfo = PrefRepositoryNamed.get(MainApplication.getAppContext(),
                            Keys.PREF.INTRO_IMAGE_INFO, IntroImageInfo.class);

                    String savedModifyDate = introImageInfo.modiDate;
                    String savedImageUrl = introImageInfo.imageUrl;

                    if (!savedModifyDate.equals(imageInfo.modiDate) || (!savedImageUrl.equals(imageInfo.imageUrl))) {
                        savePref(activity, imageInfo, fileDir);
                    }

                    File imgFile = new File(fileDir);
                    if(!imgFile.exists()){
                        PrefRepositoryNamed.remove(MainApplication.getAppContext(), Keys.PREF.INTRO_IMAGE_INFO);
                    }
                }

            }catch(Exception e){
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
        });
    }

    /**
     * pref 저장 및 다운로드 받은 이미지 파일 복사
     * 이미지 url ''인 경우, 파일 지우고 디폴트 노출
     * internal storage size 부족한 경우 ???
     * 다운로드 실패 다시 다운, 파일 카피 실패
     *
     * @param activity activity
     * @param imageInfo appIntroTxt
     * @param fileDir fileDir
     */
    private void savePref(Activity activity, final IntroImageInfo imageInfo, String fileDir){
        File imgFile = new File(fileDir);

        try{
            if(imageInfo.imageUrl.isEmpty()){
                boolean isDelete = imgFile.delete();
                if(!isDelete){
                    Ln.d("deleteCacheFile failure : " + imgFile.getName());
                }

                PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.PREF.INTRO_IMAGE_INFO, imageInfo);
            }else {
                imageDownload(activity, imageInfo, fileDir);
            }


        } catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
            deleteIntroInfo(imgFile);
        }

    }

    /**
     * imageDownload
     *
     *
     * NOTE : 헤더 정보 보고 파일 용량 가져와서 파일 정상적으로 다운 받았는지 확인 하는 방법이 좋을까?
     *
     * @param activity activity
     * @param imageInfo appIntroTxt
     * @param fileDir fileDir
     */
    private void imageDownload(Activity activity, final IntroImageInfo imageInfo, final String fileDir) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            try {
                Glide.with(activity).load(trim(imageInfo.imageUrl)).downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {

                        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.PREF.INTRO_IMAGE_INFO, imageInfo);

                        boolean isSucc = resource.renameTo(new File(fileDir));

                        if (!isSucc) {
                            try {
                                copyFile(resource, new File(fileDir));
                            }catch(IOException e){
                                Ln.e(e);
                            }
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                Ln.e(e);
            }
        });
    }

    /**
     * deleteIntroInfo
     *
     * @param imgFile 파일명
     */
    private void deleteIntroInfo(File imgFile){
        boolean isDelete = imgFile.delete();
        if(!isDelete){
            Ln.d("deleteCacheFile failure : " + imgFile.getName());
        }
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), Keys.PREF.INTRO_IMAGE_INFO);
    }

    /**
     * copyFile
     *
     * @param src 원본
     * @param dst 대상
     */
    private void copyFile(File src, File dst) throws IOException{
        FileInputStream fis = null;
        FileChannel ic = null;

        FileOutputStream fos = null;
        FileChannel oc = null;

        try {
            fis = new FileInputStream(src);
            ic = fis.getChannel();

            fos = new FileOutputStream(dst);
            oc = fos.getChannel();

            long size = ic.size();
            ic.transferTo(0, size, oc);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적 으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
            deleteIntroInfo(dst);
        } finally {
                if(oc != null) {
                    oc.close();
                }
                if(ic != null) {
                    ic.close();
                }
                if(fos != null) {
                    fos.close();
                }
                if(fis != null) {
                    fis.close();
                }
        }
    }

    /**
     * onCancelled
     */
    @Override
    public void onCancelled() {
        super.onCancelled();
    }

    /**
     * IntroImageInfo
     *
     * @param activity activity
     * @return IntroImageInfo
     */
    private IntroImageInfo getIntroImage(Activity activity){
        try{
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            String url = ServerUrls.getHttpRoot() + ServerUrls.REST.INTRO_IMAGE_API+"?os=A&h="+metrics.heightPixels+"&w="+metrics.widthPixels+"&density="+metrics.density;
            //String url = ServerUrls.HTTP_ROOT + ServerUrls.REST.INTRO_IMAGE_API+"?openDate=20160601222222&os=A&h="+metrics.heightPixels+"&w="+metrics.widthPixels+"&density="+metrics.density;
            return restClient.getForObject(url, IntroImageInfo.class);
        }catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
            return null;
        }
    }
}
