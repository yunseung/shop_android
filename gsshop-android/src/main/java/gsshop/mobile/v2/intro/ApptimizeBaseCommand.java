package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.text.TextUtils;

import com.apptimize.ApptimizeVar;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.ApptimizeFlexibleExp;
import gsshop.mobile.v2.CommandJob;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.BuildCheckUtils;
import roboguice.util.Ln;

public class ApptimizeBaseCommand extends BaseCommand {

    public void apptimizeBase(Activity activity, CommandChain chain, String expName){
        try {
            String[] arr = ApptimizeExpManager.findProd(expName);
            if( arr != null ) {
                String appver = arr[0]; //263
                String target = arr[1]; //54
                String exp = arr[2]; //IMG

                if(BuildCheckUtils.isApptimizeApply(appver)){
                    //해당 실험명이 존재하면 작업 start 처리
                    IntroActivity.parallelHandler.sendMessage(expName, CommandJob.JOB_START);
                    MainApplication.apptimizeWaitFlag = true;

                    ApptimizeVar<String> dynamicVariable = ApptimizeVar.createString(appver+"_"+target+"_"+exp, ApptimizeExpManager.TYPE_O);
                    String type = dynamicVariable.value(); //A or B or O

                    if(type != null && !TextUtils.isEmpty(type)){
                        String all = appver + "_" + target + "_" + exp + "_" + type;
                        ApptimizeFlexibleExp flexibleExp = new ApptimizeFlexibleExp(appver,target,exp,type,all);
                        ApptimizeExpManager.addExp(flexibleExp);
                    }

                    if(MainApplication.apptimizeNaviFlag == false){ //아직 네비 호출 안했으면
                        MainApplication.apptimizeWaitFlag = true; //0.3초 기다려야되니까 apptimizeFlag = true
                    }else{ //이미 네비 호출했으면
                        MainApplication.apptimizeWaitFlag = false; //0.3ch 안기다려도되니까 apptimizeFlag = false
                    }

                    //해당 실험명이 존재하는 경우에만 end 처리
                    IntroActivity.parallelHandler.sendMessage(expName, CommandJob.JOB_END);
                }
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void execute(Activity activity, CommandChain commandChain) {

    }
}
