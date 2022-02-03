/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.text.TextUtils;

import com.apptimize.ApptimizeVar;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.ApptimizeTabNameExp;
import gsshop.mobile.v2.CommandJob;
import gsshop.mobile.v2.util.BuildCheckUtils;

/**
 * 앱티마이저 초기화 및 실험요소 정리
 */
public class ApptimizeTABNAME2Command extends BaseCommand {
    /**
     * 앱티마이즈 AB테스트를 위한 Command 추가
     * @param activity
     * @param chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {

        chain.next(activity);

        try {
            String[] arr = ApptimizeExpManager.findProd(ApptimizeExpManager.TABNAME2);
            String appver = arr[0]; //ALL
            String target = arr[1]; //ALL
            String exp = arr[2]; //TABNAME2

            if( arr != null ) {
                if (2 < arr.length && ApptimizeExpManager.TABNAME2.equals(arr[2])) {
                    IntroActivity.parallelHandler.sendMessage(ApptimizeTABNAME2Command.class.getName(), CommandJob.JOB_START);
                    ApptimizeVar<String> dynamicVariable = ApptimizeVar.createString(appver+"_"+target+"_"+exp, ApptimizeExpManager.TYPE_O);
                    String tabNameValue = dynamicVariable.value(); //key값(appver_target_exp)를 통해 얻은 벨류값!
                    if (tabNameValue != null && !TextUtils.isEmpty(tabNameValue)) {
                        String[] valueArray = tabNameValue.split("_"); //valueArray = [301, 490, 메인탭명, 서브탭명] or [301, 490, O]
                        ApptimizeTabNameExp temp = new ApptimizeTabNameExp(appver,target,exp,tabNameValue); //ApptimizeTabNameExp(ALL,ALL,TABNAME,301_490_메인탭명_서브탭)
                        if (valueArray != null && 3 < valueArray.length) {
                            temp.setApptiVer(valueArray[0]); //301
                            //내 버전 >= key값 앱버전 // True
                            if (BuildCheckUtils.isApptimizeApply(temp.getApptiVer())) {
                                temp.setApptiTarget(valueArray[1]); //451
                                temp.setApptiMainTabName(valueArray[2]); //메인탭명
                                temp.setApptiSubTabName(valueArray[3]); //서브탭명
                                ApptimizeExpManager.addExp(temp);
                            } else {
                                temp.setApptiTarget("");
                                temp.setApptiMainTabName(""); //메인탭명
                                temp.setApptiSubTabName(""); //서브탭명
                                ApptimizeExpManager.addExp(temp);
                            }
                        } else if (ApptimizeExpManager.TYPE_O.equals(tabNameValue)) {
                            //디폴트
                            temp.setApptiVer("");
                            temp.setApptiTarget("");
                            temp.setApptiMainTabName(""); //메인탭명
                            temp.setApptiSubTabName(""); //서브탭명
                            ApptimizeExpManager.addExp(temp);
                        } else {
                            temp.setApptiVer("");
                            temp.setApptiTarget("");
                            temp.setApptiMainTabName(""); //메인탭명
                            temp.setApptiSubTabName(""); //서브탭명
                            ApptimizeExpManager.addExp(temp);
                        }
                    }
                    //해당 실험명이 존재하는 경우에만 end 처리
                    IntroActivity.parallelHandler.sendMessage(ApptimizeTABNAME2Command.class.getName(), CommandJob.JOB_END);
                }
            }

        }catch (Exception e){
        }
    }
}