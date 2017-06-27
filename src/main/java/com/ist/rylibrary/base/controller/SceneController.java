package com.ist.rylibrary.base.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ist.nativepackage.EyesCtrl;
import com.ist.nativepackage.Motor;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.boardcast.BroadCastSendController;
import com.ist.rylibrary.base.entity.AllSceneResultBean;
import com.ist.rylibrary.base.entity.BaseActionBean;
import com.ist.rylibrary.base.entity.CustomSceneBean;
import com.ist.rylibrary.base.entity.FinalQABean;
import com.ist.rylibrary.base.entity.FinalQADataResult;
import com.ist.rylibrary.base.entity.FinalQAnswerBean;
import com.ist.rylibrary.base.entity.PageBean;
import com.ist.rylibrary.base.entity.PersonInformationBean;
import com.ist.rylibrary.base.entity.Robotsbean;
import com.ist.rylibrary.base.entity.SceneBean;
import com.ist.rylibrary.base.entity.SceneQABean;
import com.ist.rylibrary.base.entity.SceneQasNewAnswerListBean;
import com.ist.rylibrary.base.entity.StereAnswerBean;
import com.ist.rylibrary.base.entity.TipsBean;
import com.ist.rylibrary.base.entity.ToubuBean;
import com.ist.rylibrary.base.entity.YanjingBean;
import com.ist.rylibrary.base.entity.YemianBean;
import com.ist.rylibrary.base.entity.DipanBean;
import com.ist.rylibrary.base.listener.BaseAiuiListener;
import com.ist.rylibrary.base.listener.FaceAddErrorListener;
import com.ist.rylibrary.base.listener.RyRRttsListener;
import com.ist.rylibrary.base.listener.YindaoCompleteListener;
import com.ist.rylibrary.base.module.BaseActivity;
import com.ist.rylibrary.base.service.AiuiService;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.base.util.PinYinUtil;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.base.util.ToolUtil;
import com.wewins.facelibrary.api.rr.RRBusinessApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by minyuchun on 2017/3/23.
 * 场景控制类整理
 */
public class SceneController {
    /**
     * 场景控制实例
     */
    private static SceneController mSceneController;
    /**
     * 通过topicId 来匹配说话内容
     */
    private static final int BY_TOPICID = 1;
    /**
     * 通过问语义 来匹配说话内容
     */
    private static final int BY_QUE = 2;
    /**
     * 初始化场景配置是否完成,作用域休眠页面的跳转，当数据完成时收到有人后自动跳转到菜单页面
     */
    private boolean isCompleteData;
    /**
     * 日志输出类
     */
    private BaseLogUtil log = null;
    /**用于小贴士的说话*/
    private List<TipsBean> mTipsBeen;
    /***场景问答类 用于记录*/
    private List<SceneQABean> jaSceneQas;
    /**
     * 通用类型的问答,SceneId为0 的问答
     */
    private List<SceneQABean> defaultSceneQas;
    /**
     * 机器人数据  用于记录
     */
    private Robotsbean jsRobots;
    /**
     * 场景列表数据
     */
    private List<SceneBean> jaScenes;
    /**
     * 页面列表数据
     */
    private List<PageBean> jaPages;
    /**
     * 自定义场景列表数据
     */
    private List<CustomSceneBean> jaCustomScenes;
    /**
     * 页面场景集合
     */
    private Map<String, PageBean> pageMap;
    /**
     * 自定义场景集合
     */
    private Map<String, CustomSceneBean> customSceneMap; // 自定义场景集合
    /**
     * 当前正在使用的场景
     */
    private SceneBean nowSceneBean;
    /**
     * 场景切换的前缀 内容为：businessCode_classname
     */
    private String pageKey;
    /**
     * 说默认的话 如 不知道您说的是什么 累计次数，用于积累n次一次兜底
     */
    private int speakDefaultAnswerCount;
    /**
     * 需要替换的人名字
     */
    private String facePersonName;
    /**
     * 是否需要客服
     */
    private boolean isKF = false;
    /**
     * 获取系统变量的位置
     */
    private String prop_position = "persist.service.ist.djposition";
    /**
     * 页面动作回调监听
     */
    private pageActionListener mPageActionListener;
    /**
     * 语音播放的信息
     */
    private String voiceMessage;
    /**
     * 匹配到的动作集合 页面动作，眼睛动作，头部动作，底盘动作
     */
    private List<BaseActionBean> mBaseActionBeanList;
    /**
     * 最近一次自定义语义返回的变量
     */
    private static String lastVar = "";
    /**
     * 问题累计的次数
     ***/
    public int questionNum = 0;
    public int maxQuestionNum = 2;
    /**
     * 最后一次的问题
     **/
    public FinalQABean lastQAnswer;
    /**
     * 讯飞最后一次的有效识别
     **/
    public String lastIflySemantic;
    /**
     * 是否询问用户的名字
     ***/
    public boolean isAskUserName = false;
    /**是否新增人脸*/
    public boolean isAddUserFace = false;

    public boolean isUseOldQuestion = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        Toast.makeText(RyApplication.getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 构造函数
     */
    public SceneController() {
        log = new BaseLogUtil(this.getClass());//初始化日志类
    }

    /**
     * 获取实例
     */
    public static SceneController getInstance() {
        if (mSceneController == null) {
            mSceneController = new SceneController();
        }
        return mSceneController;
    }

    //----------------------------------------------------------用户动作处理开始-------------------------------------------------

    /***
     * 执行列表动作
     * @param isVoiceCompletion  是否是在语音播放完成后调用本方法执行
     */
    public void dealActionList(boolean isVoiceCompletion, List<BaseActionBean> beanList) {
        if (beanList != null && beanList.size() > 0) {
            log.d("处理动作 dealActionList" + beanList);
            List<BaseActionBean> list = new ArrayList<>();
            for (BaseActionBean baseActionBean : beanList) {
                if ((isVoiceCompletion && baseActionBean.isActionAfterTalk())
                        || (!isVoiceCompletion && !baseActionBean.isActionAfterTalk())) {
                    log.d("现在处理动作 ");
                    try {
                        if (baseActionBean instanceof YemianBean) {
                            dealPageAction(baseActionBean.getAction());
                        } else if (baseActionBean instanceof YanjingBean) {
                            dealEye(baseActionBean.getAction());
                        } else if (baseActionBean instanceof ToubuBean) {
                            dealHead(baseActionBean.getAction());
                        } else if (baseActionBean instanceof DipanBean) {
                            dealChassisAction(baseActionBean.getAction());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (baseActionBean.getTalkErrorAction() != null && !baseActionBean.getTalkErrorAction().isEmpty()) {
                            AiuiController.getInstance().post(baseActionBean.getTalkErrorAction());
                        }
                    }
                } else {
                    list.add(baseActionBean);
                }
            }
            mBaseActionBeanList = list;
        }
    }

    /**
     * 处理底盘动作
     *
     * @param action 动作集合
     *               yindaoCompleteListener  引导处理完成的监听
     */
    public void dealChassisAction(String action, YindaoCompleteListener yindaoCompleteListener) throws Exception {
        if (action.contains("_yindao") || action.contains("_yingdao")) {
            YinDaoController.getInstance().dealChassisAction(action, yindaoCompleteListener);
        } else {
            ChassisController.getInstance().post(false, action, ChassisController.CHASSIS_TYPE_WALK);
        }
    }

    /**
     * 处理底盘动作
     *
     * @param action 动作集合
     */
    public void dealChassisAction(String action) throws Exception {
        dealChassisAction(action, null);
    }

    /**
     * 眼睛屏处理
     *
     * @param action 眼睛屏幕动作
     */
    public void dealEye(final String action) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.d("眼睛屏动作:" + action);
                int ret = EyesCtrl.getInstance().Eyes(action);
                log.i("眼睛屏结果：" + ret);
            }
        }).start();

    }

    /**
     * 头部处理
     *
     * @param action 动作指令
     */
    public void dealHead(String action) throws Exception {
        final int val = SystemProperties.getInt(prop_position, 0);
        if (val > 0) {
            log.d("头部动作" + action);
            if (action.equals("shake_your_head")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.d("头部动作结束 = " + Motor.action(val, 1));
                        ;
                    }
                }).start();
            } else if (action.equals("turn_right")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.d("头部动作结束 = " + Motor.kan("right"));
                        ;
                    }
                }).start();
            } else if (action.equals("turn_left")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.d("头部动作结束 = " + Motor.kan("left"));
                        ;
                    }
                }).start();
            } else if (action.equals("turn_front")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.d("头部动作结束 =" + Motor.kan("front"));
                        ;
                    }
                }).start();
            }

        }
    }

    /**
     * 处理头部动作  已经弃用
     *
     * @param data 指令 int型
     */
    public void dealHead(final int data) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ret = Motor.ExecAction(data);
            }
        }).start();
    }

    /***
     * 处理页面动作
     * @param pageAction 页面的动作
     * @throws Exception
     */
    private void dealPageAction(String pageAction) throws Exception {
        try {
            log.i("页面动作：" + pageAction);
            if (pageJudge(pageAction)) {
                if ("next_step".equals(pageAction)) {// 下一步
                } else if ("stop_speak".equals(pageAction)) {// 停止说话
                    AiuiController.getInstance().post(AiuiService.AIUI_TYPE_STOP);
                } else if ("stop_tts".equals(pageAction)) {// 停止播放
                    AiuiController.getInstance().post(AiuiService.AIUI_TYPE_STOP);
                } else if ("print".equals(pageAction)) {// 打印的动作
                } else if ("start_move".equals(pageAction)) {
                } else if (pageAction.startsWith("goto")) {
                } else if ("jx".equals(pageAction)) {
                } else if ("btn_stop".equals(pageAction)) {
                } else if (pageAction.startsWith("fw")) {
                } else if (pageAction.startsWith("actioncode_")) {//
                    String[] pageJump = pageAction.split("_");
                    String topActivity = ToolUtil.getInstance().getTopActivity(RyApplication.getContext());
                    if (topActivity != null && !topActivity.isEmpty()) {
                        log.i("指令要我打开其他的业务我也没办法 打开的页面是 " + pageAction);
                        String action = pageJump[1];
                        log.i("打开的activity " + action);
                        Intent intent = new Intent(action);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (pageJump.length == 3) {
                            Bundle bundle = new Bundle();
                            bundle.putString("pageType", pageJump[2]);
                            intent.putExtras(bundle);
                        }
                        RyApplication.getContext().startActivity(intent);
                    }
                } else if (pageAction.equals("signOut")) {//退出页面动作
                    String topActivity = ToolUtil.getInstance().getTopActivity(RyApplication.getContext());
                    if (topActivity != null && !topActivity.isEmpty()) {
                        log.d("接到页面退出指令");
                    } else {
                        BroadCastSendController.getInstance().sendStarBroadCast("star_close");
                    }
                } else if (pageAction.equals("reBack")) {//返回动作
                    if (ActivityController.getInstance().getCount() > 1) {
                        ActivityController.getInstance().finishTopActivity();
                    }
                } else if (pageAction.equals("star_open")) {
                    String topActivity = ToolUtil.getInstance().getTopActivity(RyApplication.getContext());
                    if (topActivity != null && !topActivity.isEmpty()) {
                        ToolUtil.getInstance().startAPKStarFace();
                    }
                } else if (pageAction.startsWith("open_")) {//开启明星脸程序
                    log.d("打开其他相关的程序");
                    if (pageAction.endsWith("starFace")) {
                        ToolUtil.getInstance().startAPKStarFace();
                    }
                } else if (pageAction.startsWith("stop_")) {
                } else {
                    if (mPageActionListener != null) {
                        if (mPageActionListener.pageAction(pageAction)) {
                            mPageActionListener = null;
                        }
                    }
                    if (pageAction.endsWith("_allActivity")) {
                        for (BaseActivity activity : ActivityController.getInstance().getActivities()) {
                            activity.pageAction(pageAction);
                        }
                    }else {
                        ActivityController.getInstance().getTopActivity().pageAction(pageAction);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 页面动作执行前期判断
     * @param pageAction  页面动作类型
     * @return 返回结果
     */
    private boolean pageJudge(String pageAction) {
        if (!AiuiController.getInstance().isAllowInterrupt()) {
            log.d("判断当前场景 "+pageAction);
            if (pageAction.startsWith("stop_")) {
                log.d("判断当前场景头部是停止 "+pageAction);
                if (pageAction.endsWith("music")) {
                    log.d("判断当前场景尾部是音乐 "+pageAction);
                    MediaVoiceController.stopMP3();
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public interface pageActionListener {
        boolean pageAction(String pageAction);
    }

    //----------------------------------------------------------用户动作处理结束-------------------------------------------------

    //----------------------------------------------------------用户讯飞语义匹配的的代码开始-------------------------------------------------

    /**
     * 用户说的话匹配
     *
     * @param asrResultJsonOrCmd 问
     * @param defaultAnswer      默认答
     * @return 返回匹配得到的答案
     */
    public String rrPeople(String asrResultJsonOrCmd, String defaultAnswer, String iflySemantic) {
        return rrPeople(asrResultJsonOrCmd, defaultAnswer, isKF, iflySemantic);
    }

    /**
     * 人机交互识别json或指令 ；"｛"开头是识别结果，其他是指令
     *
     * @param isKf 信息是否推送给客服
     * @return 返回说的话
     */
    @SuppressLint("SimpleDateFormat")
    private String rrPeople(String asrResultJsonOrCmd, String defaultAnswer, boolean isKf, String iflySemantic) {
        try {
            FinalQABean finalQABean = new FinalQABean();
            finalQABean.setText(asrResultJsonOrCmd);
            if (defaultAnswer != null) {
                finalQABean.setAnswer(new FinalQAnswerBean(null, defaultAnswer));
            }
            rrPeople(finalQABean, iflySemantic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param qAnswer 问答类
     * @param iflySemantic 默认的回答
     * @return 返回匹配得到的答案
     */
    public void rrPeople(final FinalQABean qAnswer, final String iflySemantic) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dealrrPeople(qAnswer, iflySemantic);
                }
            }).start();

        } catch (Exception e) {
            try {
                errorSpeak(qAnswer, iflySemantic);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void dealrrPeople(FinalQABean qAnswer, String iflySemantic) {
        RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
        try {
            String asrResultJsonOrCmd = qAnswer.getText();//用户说的话
            List<StringBuilder> matchList = getMatchValue(qAnswer);//拼匹配字符串 topicid
            if (matchList == null){
                return;
            }
            SceneQABean sceneQABean = null;//匹配到的问答类
            List<SceneQABean> qaList = null;//场景问答列表
            log.i("用户说的话：" + asrResultJsonOrCmd);
            int type = -1;
            if (nowSceneBean != null) {
                log.i("当前页面有场景");
                qaList = nowSceneBean.getSceneQABeanList();//获取当前场景中的问答列表
                if (qaList != null && qaList.size() > 0) {
                    for (int i = 0; i < matchList.size(); i++) {
                        StringBuilder matchValue = matchList.get(i);
                        log.d("返回范爷的语音需要进行topcid的值 " + matchValue);
                        if (rrPeopleConditionCheck(matchValue.toString())) {
                            SceneQABean matchBean = matchValue(qaList, matchValue.toString(), BY_TOPICID);//通不过topicid 来匹配相应的内容
                            if(matchBean!=null){
                                sceneQABean = matchBean;
                                type = BY_TOPICID;
                            }
                        }
                        log.d("通过topid匹配 " + sceneQABean);
                        if (sceneQABean != null) {
                            break;
                        }
                    }
                    log.d("用户说的话实时识别 " + asrResultJsonOrCmd);
                    if (rrPeopleConditionCheck(asrResultJsonOrCmd) && sceneQABean == null) {
                        sceneQABean = matchValue(qaList, asrResultJsonOrCmd, BY_QUE);
                        type = BY_QUE;
                        log.d("通过说的话匹配 " + sceneQABean);
                    }
                }
            } else if (defaultSceneQas != null && defaultSceneQas.size() > 0) {//当前场景不存在，使用公共场景中的问答
                log.i("当前场景不存在，使用公共场景中的问答");
                    qaList = defaultSceneQas;
                if(matchList!=null){
                    for (int i = 0; i < matchList.size(); i++) {
                        StringBuilder matchValue = matchList.get(i);
                        SceneQABean matchBean = matchValue(qaList, matchValue.toString(), BY_TOPICID);
                        if(matchBean!=null){
                            sceneQABean = matchBean;
                        }
                    }
                    log.d("通过topid匹配 " + sceneQABean);
                    if (sceneQABean == null) {
                        sceneQABean = matchValue(qaList, asrResultJsonOrCmd, BY_QUE);
                        log.d("通过说的话匹配 " + sceneQABean);
                    }
                }
            }
            if (sceneQABean != null) {//存在匹配内容 处理匹配到的内容
                log.i("匹配到问答！");
                boolean isContiue = dealNewUserQuestion(qAnswer, iflySemantic, ryApplication);
                if (!isContiue) {
                    log.i("机器人主动询问用户的名字！");
                    return;
                }
                dealSceneQA(sceneQABean, asrResultJsonOrCmd, type, iflySemantic);
            } else if (qAnswer.getAnswer() != null || qAnswer.getData() != null) {//不存在匹配内容但是存在 默认的回答
                log.i("不存在匹配内容但是存在 默认的回答");
                if (nowSceneBean != null && nowSceneBean.getExtNlp().equals("0")) {
                    log.d("无场景 存在默认回答但是不播放");
                } else {
                    errorSpeak(qAnswer, iflySemantic);
                }
            } else if (asrResultJsonOrCmd.contains("_yindao") || asrResultJsonOrCmd.contains("_jiangjie")) {
                log.d("指令匹配是引导或者讲解流程");
                if (JiangJieController.getInstance().isInJiangJieProcess()) {
                    if (JiangJieController.getInstance().getJiangJieAiuiListener() != null) {
                        log.d("是讲解流程，没有匹配到内容，并且没有默认回答，直接赋值讲解语音完毕");
                        JiangJieController.getInstance().getJiangJieAiuiListener().onAiuiComplete(true);
                    }
                }
                if (YinDaoController.getInstance().isInYindaoProcess()) {
                    if (YinDaoController.getInstance().getYinDaoAiuiListener() != null) {
                        log.d("是引导流程，没有匹配到内容，并且没有默认回答，直接赋值引导语音完毕");
                        YinDaoController.getInstance().getYinDaoAiuiListener().onAiuiComplete(true);
                    }
                }
            } else if (asrResultJsonOrCmd != null && asrResultJsonOrCmd.length() > 4) {//什么都不存在进行兜底处理
                if (nowSceneBean != null && nowSceneBean.getExtNlp().equals("0")) {

                } else {
                    log.d("兜底说话 兜底累计 " + speakDefaultAnswerCount + " 取膜 " + speakDefaultAnswerCount % 3);
                    if (speakDefaultAnswerCount % 3 == 0) {
                        if (qaList != null) {
                            sceneQABean = matchValue(qaList, "#cmd_default_answer#", BY_QUE);
                        } else if (defaultSceneQas != null) {
                            sceneQABean = matchValue(defaultSceneQas, "#cmd_default_answer#", BY_QUE);
                        }
                        if (sceneQABean != null) {
                            dealSceneQA(sceneQABean, asrResultJsonOrCmd, BY_QUE, iflySemantic);
                        }else{
                            playVoice(null,"我美听懂您的意思",false);
                        }
                    } else {
                        AiuiController.getInstance().post(true, asrResultJsonOrCmd);
                    }
                    speakDefaultAnswerCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理新用户的问答
     **/
    private boolean dealNewUserQuestion(FinalQABean qAnswer, String iflySemantic, RyApplication ryApplication) {
        String asrResultJsonOrCmd = qAnswer.getText();
        if (!ryApplication.isOpenFace()) {
            log.i("不需要认识新老用户！");
            return true;
        }
        final PersonInformationBean person = ryApplication.getPerson();
        if (person!=null && person.isNewPerson() && !isAskUserName) {
            log.i("是新用户，累计问题的次数：" + questionNum);
            if (person.getId() !=null && person.getName() == null) {
                if (!asrResultJsonOrCmd.startsWith("#")) {
                    questionNum++;
                    if (questionNum >= maxQuestionNum) {
                        log.i("问题累积已经达到最大值！" + questionNum);
                        questionNum = 0;
                        lastQAnswer = qAnswer;
                        lastIflySemantic = iflySemantic;
                        isAskUserName = true;
                        AiuiController.getInstance().post(AiuiService.AIUI_TYPE_CLOSE);
                        rrPeople("#cmd_ask_name#", "", "");
                        AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                            @Override
                            public boolean onComplete(String s) {
                                AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
                                return true;
                            }
                        });
                        return false;
                    }
                } else {
                    log.i("是系统指令，不累计问题的次数：");
                }
            } else {
                log.i("已经询问了用户的名字,或者上传图片出错没有faceID没有必要询问！");
            }
        } else {
            if(isAskUserName){
                person.setNewPerson(false);
            }
            log.i("是老用户，累计问题的次数：" + questionNum);
        }
        return true;
    }

    /**
     * 获取讯飞解析对比的字符串,并进行预处理
     *
     * @param qAnswer 最终问答类
     * @return 返回需要对比的字符串
     */
    private List<StringBuilder> getMatchValue(FinalQABean qAnswer) {
        RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
        List<StringBuilder> matchList = new ArrayList<StringBuilder>();
        StringBuilder matchValue = new StringBuilder();
        StringBuilder matchValue2 = null;
        try {
            if (qAnswer.getService() != null) {
                matchValue.append(qAnswer.getService());
            }
            if (qAnswer.getSemantic() != null) {
                if (qAnswer.getSemantic().getSlots() != null) {
                    if (qAnswer.getSemantic().getSlots().getOper() != null) {
                        matchValue.append("_" + qAnswer.getSemantic().getSlots().getOper());
                    }
                    if (qAnswer.getSemantic().getSlots().getObj() != null) {
                        if (qAnswer.getSemantic().getSlots().getObj().getType() != null) {
                            matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getType());
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getItem() != null) {
                            matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getItem());
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getQue() != null) {
                            matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getQue());
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getAct() != null) {
                            matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getAct());
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getActobj() != null) {
                            matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getActobj());
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getVar() != null) {
                            matchValue2 = new StringBuilder(matchValue);//有变量var就新建一个
                            lastVar = qAnswer.getSemantic().getSlots().getObj().getVar();
                            matchValue.append("_" + lastVar);
                        } else {
                            lastVar = "";
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getBank() != null) {
                            if (qAnswer.getSemantic().getSlots().getObj().getBank().getDst() != null) {
                                matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getBank().getDst());
                                if (matchValue2 != null) {
                                    matchValue2.append("_" + qAnswer.getSemantic().getSlots().getObj().getBank().getDst());
                                }

                            }
                        }
                        if (qAnswer.getSemantic().getSlots().getObj().getArea() != null) {
                            if (qAnswer.getSemantic().getSlots().getObj().getArea().getDst() != null) {
                                matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getArea().getDst());
                                if (matchValue2 != null) {
                                    matchValue2.append("_" + qAnswer.getSemantic().getSlots().getObj().getArea().getDst());
                                }
                            }
                            if (qAnswer.getSemantic().getSlots().getObj().getArea().getSrc() != null) {
                                matchValue.append("_" + qAnswer.getSemantic().getSlots().getObj().getArea().getSrc());
                                if (matchValue2 != null) {
                                    matchValue2.append("_" + qAnswer.getSemantic().getSlots().getObj().getArea().getSrc());
                                }
                            }
                        }
                    }
                    if (qAnswer.getSemantic().getSlots().getObject() != null) {
                        if (qAnswer.getSemantic().getSlots().getObject().getNumber() != null) {
                            if (qAnswer.getSemantic().getSlots().getObject().getNumber().getReal() != null) {
                                matchValue.append("_" + qAnswer.getSemantic().getSlots().getObject().getNumber().getReal());
                                if (matchValue2 != null) {
                                    matchValue2.append("_" + qAnswer.getSemantic().getSlots().getObject().getNumber().getReal());
                                }
                            }
                        }
                    }
                }
            }
            matchList.add(matchValue);
            log.d("第一条匹配的topicId：" + matchValue);
            log.d("第二条匹配的topicId：" + matchValue2 + ";上一个需要存储的内容: " + lastVar);
            if (matchValue2 != null) {
                PersonInformationBean person = ryApplication.getPerson();
                matchList.add(matchValue2);
                if (matchValue2.indexOf("仁盈通用_自报家门_姓名_姓名") > -1) {
                    if(person!=null){//将姓名放在缓存中
                        person.setName(lastVar);
                        if(person.getId()!=null){
                            AiuiController.getInstance().post(AiuiService.AIUI_TYPE_CLOSE);
                            log.d("是否已经说过 "+isAskUserName +"是否是新用户 "+person.isNewPerson());
                            if(isAskUserName && person.isNewPerson()){  //需要向下匹配了，判断是否是新用户
                                rrPeople("#cmd_answer_name#","","");
                                AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                                    @Override
                                    public boolean onComplete(String s) {
                                        log.i("播放完成:" + s+",上一次播放的内容是{"+lastQAnswer+","+lastIflySemantic+"}");
                                        if(lastQAnswer != null){
                                            rrPeople(lastQAnswer, lastIflySemantic);
                                            AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                                                @Override
                                                public boolean onComplete(String s) {
                                                    log.i("播放上次的回答完成:" + s);
                                                    AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
                                                    return true;
                                                }
                                            });
                                            return false;
                                        }else{
                                            AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
                                            return true;
                                        }
                                    }
                                });
                                boolean bResult = RRBusinessApi.getInstance().updatePerson(person.getId(), person.getName());
                                log.d("更新姓名信息 " + bResult);
                                if(bResult){
                                    SceneController.getInstance().isAddUserFace = true;
                                    person.setNewPerson(false);
                                }
                            }else{//不需要向下匹配了 返回null
                                rrPeople("#cmd_answer_name_other#","","");
                                AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                                    @Override
                                    public boolean onComplete(String s) {
                                        AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
                                        return true;
                                    }
                                });
                                boolean bResult = RRBusinessApi.getInstance().updatePerson(person.getId(), person.getName());
                                log.d("更新姓名信息 " + bResult);
                                if(bResult){
                                    SceneController.getInstance().isAddUserFace = true;
                                    person.setNewPerson(false);
                                }
                                return null;
                            }
                        }else{
                            if(person.getName()!=null){
                                rrPeople("#cmd_talk_person_name#",null,null);
                            }
                        }
                    }
                    return null;
                }else if(matchValue2.indexOf("银行JS_用户口令_现金业务_取金额")>-1){
                    float money = Float.valueOf(ToolUtil.getInstance().chineseNumber2Int(lastVar));
                    if(money>=100000){
                        rrPeople("#cmd_money_large#",null,null);
                    }else if(money>20000){
                        log.d("金额超过2万");
                        rrPeople("#cmd_money_excess#",null,null);
                    }else if(money<100){
                        log.d("金额少于一百");
                        rrPeople("#cmd_money_less#",null,null);
                    }else{
                        if (money > Math.round(money) || (money%100) > 0 ){
                            log.d("金额符合但不是整数或者不是100的倍数");
                            rrPeople("#cmd_money_pass_error#",null,null);
                        }else {
                            log.d("金额符合是整数");
                            rrPeople("#cmd_money_pass_right#",null,null);
                        }
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchList;
    }

    /**
     * ShufuLiActivity
     * 处理问答类
     * @param sceneQABean 问答类
     */
    private void dealSceneQA(SceneQABean sceneQABean, String asrResultJsonOrCmd, int type, String iflySemantic) {
        try {
            speakDefaultAnswerCount = 0;//兜底策略设置为0
            String answer = null;
            int actionType = 0;
            String actionCode = null;
            log.d("需要处理的场景问答 " + sceneQABean.toString());
            if (sceneQABean.getAnswer() != null) {
                answer = getSpeakContent(sceneQABean.getAnswer());//获取文字回答
                actionType = sceneQABean.getActionType();
                actionCode = sceneQABean.getActionCode();
                log.d("老方式需要处理的场景问答 " + answer);
            } else if (sceneQABean.getAnswerList() != null && sceneQABean.getAnswerList().size() > 0) {
                SceneQasNewAnswerListBean bean = sceneQABean.getAnswerList()
                        .get(new Random().nextInt(sceneQABean.getAnswerList().size()));
                log.d("新方式需要处理的场景问答 " + bean);
                if (bean.getAnswerType().equals("1")) {
                    log.d("立体式回复");
                    if (bean.getAnswer() != null) {
                        dealStereAnswer(bean.getAnswer());
                    }
                    return;
                } else {
                    log.d("新方式需要处理的场景问答;不是立体回复回答");
                    if (bean.getAnswer() != null) {
                        answer = getSpeakContent(bean.getAnswer());
                    }
                    actionType = bean.getActionType();
                    actionCode = bean.getActionCode();
                }
            } else {
                actionType = sceneQABean.getActionType();
                actionCode = sceneQABean.getActionCode();
            }
            log.i("ActionType==" + actionType);
            log.i("actionCode==" + actionCode);
            if (answer == null || answer.isEmpty()) {//无语音内容是 在讲解或者 引导流程中直接赋值完成
                log.d("回答为空");
                if (JiangJieController.getInstance().isInJiangJieProcess()) {
                    if (JiangJieController.getInstance().getJiangJieAiuiListener() != null) {
                        log.d("直接赋值，讲解流程的引导语言完成");
                        JiangJieController.getInstance().getJiangJieAiuiListener().onAiuiComplete(true);
                    }
                }
                if (YinDaoController.getInstance().isInYindaoProcess()) {
                    if (YinDaoController.getInstance().getYinDaoAiuiListener() != null) {
                        log.d("直接赋值，讲解流程的引导语言完成");
                        YinDaoController.getInstance().getYinDaoAiuiListener().onAiuiComplete(true);
                    }
                }
            }else{
                if(answer.equals("cmd_person_ask_own_name")){
                    log.d("匹配到的回答是指令类型-询问自己的名字");
                    RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
                    PersonInformationBean person = ryApplication.getPerson();
                    if(person.getName()!=null){
                        rrPeople("#cmd_talk_person_name#",null,null);
                    }else{
                        rrPeople("#cmd_ask_person_name#",null,null);
                    }
                    return;
                }
                if (answer.length()>5 && !asrResultJsonOrCmd.equals("#cmd_default_answer#")){
                    log.d("存在需要播放的语音并且需要展示屏蔽语音响应:"+answer);
                    BaseAiuiListener.isDealVoice = false;
                }
            }
            if (actionType == 1) {
                //有动作指令，在播放语音完成后 执行相关的动作
//                answer = getSpeakContent(sceneQABean.getAnswer());
                playVoice(sceneQABean.getAnswerFile(), answer, false);
                if (actionCode != null) {
                    mBaseActionBeanList = getBaseActionList(actionCode);
                }
                if (mBaseActionBeanList != null) {
                    dealActionList(false, mBaseActionBeanList);
                }
            } else if (actionType == 0) {
                //无动作指令只是播放语音
                playVoice(sceneQABean.getAnswerFile(), answer, false);
                if (actionCode != null && actionCode.length() > 0) {
                    //也许是子场景切换
                    getSceneInCurrentPage(actionCode, pageKey);
                }
            } else if (actionType == 2) {
                //获取自定义场景
                getCustomScene(actionCode);
            }
            String matchAnswer = "";
            if (type == BY_TOPICID) {
                matchAnswer = sceneQABean.getTopicId();
            } else {
                matchAnswer = sceneQABean.getAnswer();
            }
            addVoiceLog(asrResultJsonOrCmd, answer, sceneQABean.getSceneQaId(), sceneQABean.getQuestions()
                    , matchAnswer, actionType + "", actionCode, iflySemantic);//正确处理动作的日志
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 立体式回复
     *
     * */
    private String dealStereAnswer(String answer) {
        log.d("立体式回复的答案：" + answer);
        String answerStr = "";
        List<StereAnswerBean> stereAnswerBeanList = new ArrayList<>();
        try {
            if (answer.startsWith("[") && answer.endsWith("]")) {
                JSONArray answerJsonArray = new JSONArray(answer);
                TreeMap<String,StereAnswerBean> stereAnswerBeanMap = new TreeMap<>();
                for (int i = 0; i < answerJsonArray.length(); i++) {
                    JSONObject jsonObject = answerJsonArray.getJSONObject(i);
                    String frontCon = jsonObject.getString("frontCon");//前置条件
                    String[] data;
                    if(frontCon.indexOf(",") > -1){
                        data = frontCon.split(",");
                    }else {
                        data = new String[]{frontCon};
                    }
                    String answerId = jsonObject.getString("answerId");
                    String contentType = jsonObject.getString("contentType");
                    String answerContent;
                    if(contentType.equals("1")){
                       answerContent = jsonObject.getJSONArray("answerContent").toString();
                    }else {
                       answerContent = jsonObject.getString("answerContent");
                    }
                    StereAnswerBean stereAnswerBean = new StereAnswerBean();
                    stereAnswerBean.setFrontCon(data);
                    stereAnswerBean.setAnswerContent(answerContent);
                    stereAnswerBean.setId(answerId);
                    stereAnswerBean.setStatus(0);
                    stereAnswerBean.setContentType(Integer.valueOf(contentType));
                    stereAnswerBeanMap.put(answerId, stereAnswerBean);
                }
                getStatus(stereAnswerBeanMap);
            } else {
                log.i("立体式回复的格式不对！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answerStr;
    }

    private static Disposable mDisposable;
    public static boolean  isYuYinCompleted = false;
    public static boolean  isTimeCompleted = false;
    public static boolean  isMusicCompleted = false;
    public static boolean  isDiPanCompleted = false;
    public static boolean  isVideoCompleted = false;
    //处理立体化回复data
    private void dealLthData(Map<String,StereAnswerBean> stereAnswerBeanMap){
        Collection<StereAnswerBean> collections = stereAnswerBeanMap.values();
        int count = 0;
        for(StereAnswerBean bean:collections){
            int status = bean.getStatus();
            int  type = bean.getContentType();  // 1语音  3视频 4音频 5时间 9底盘
            switch (status) {
                case 0:
                    //判断前置条件是否已完成
                    try {
                        String[] front = bean.getFrontCon();
                        boolean isTrue = true;  //是否满足条件
                        for (String key : front) {
                            log.i(bean.getId() + "的前置条件==" + key);
                            StereAnswerBean frontBean = stereAnswerBeanMap.get(key);
                            if (key != null && !key.equals("0") && !key.equals("")) {
                                if (frontBean.getStatus() != 2) {
                                    log.i(bean.getId() + "没有满足前置条件id====" + frontBean.getId());
                                    isTrue = false;
                                    break;
                                }
                            }
                        }
                        if (isTrue) {
                            log.i(bean.getId() + "前置条件已满足");
                            if (type != 1 && type != 3 && type != 4 && type != 5 && type != 9) {
                                count++;
                                bean.setStatus(2);
                            } else {
                                bean.setStatus(1);
                            }
                            dealLth(String.valueOf(bean.getContentType()), bean.getAnswerContent());
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    log.i(bean.getId() + "正在等待执行结果");
                    if (type == 1 && isYuYinCompleted ||
                            type == 3 && isVideoCompleted ||
                            type == 4 && isMusicCompleted ||
                            type == 5 && isTimeCompleted ||
                            type == 9 && isDiPanCompleted) {
                        count++;
                        bean.setStatus(2);
                    }
                    break;
                case 2:
                    log.i(bean.getId()+"已经结束" );
                    try {
                        count ++;
                        if(count >= collections.size()){
                            log.i("立体化任务结束" );
                            if(mDisposable != null&& !mDisposable.isDisposed()){
                                log.i("关闭实时获取状态" );
                                mDisposable.dispose();
                            }
                            BaseAiuiListener.isDealVoice = true;
                            String activity = ToolUtil.getInstance().getTopActivity(RyApplication.getContext());
                            log.i("立体化任务全部结束后最上面的页面是=="+activity );
                            if(activity.indexOf("ImageViewActivity") > -1){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ActivityController.getInstance().finishCurrentActivity("ImageViewActivity");
                                    }
                                },10* 1000);
                            }else if(activity.indexOf("VideoViewActivity") > -1){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ActivityController.getInstance().finishCurrentActivity("ImageViewActivity");
                                        ActivityController.getInstance().finishCurrentActivity("VideoViewActivity");
                                    }
                                },1000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
            }
        }
    }

    //强行停止立体化
    public void stopLth(){
        try {
            if(mDisposable != null&& !mDisposable.isDisposed()){
                log.i("立体化被强行停止了" );
                mDisposable.dispose();
            }
            BaseAiuiListener.isDealVoice = true;
            ActivityController.getInstance().finishCurrentActivity("ImageViewActivity");
            ActivityController.getInstance().finishCurrentActivity("VideoViewActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取结束状态
    private void getStatus(final Map<String,StereAnswerBean> stereAnswerBeanMap){
        BaseAiuiListener.isDealVoice = false;
        log.i("进入该状态" );
        try {
            Observable.interval(1, TimeUnit.SECONDS, Schedulers.trampoline())
                    .subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }

                @Override
                public void onNext(Long value) {
                    dealLthData(stereAnswerBeanMap);
                }

                @Override
                public void onError(Throwable e) {
                    log.i("实时获取状态出错");
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据type具体处理立体化
    private void dealLth(String contentType, String answerContent){
        log.i("wjz立体式回复的类型：" + contentType + ";立体式回复的内容:" + answerContent);
        String answerStr = "";
        try {
            if (contentType.equals("1")) { //语音包
                isYuYinCompleted = false;
                StringBuilder answerBuilder = new StringBuilder();
                if (answerContent.startsWith("[") && answerContent.endsWith("]")) {
                    JSONArray jsonArray = new JSONArray(answerContent);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        String answerString = jsonArray.getString(j);
                        answerBuilder.append(answerString).append("#");
                    }
                } else {
                    answerBuilder.append(answerContent);
                }
                answerStr = getSpeakContent(answerBuilder.toString());
                playVoice("", answerStr, false);
                AiuiController.getInstance().setRobotAnswer(false);
                AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                    @Override
                    public boolean onComplete(String s) {
                        log.i("立体式回复》》》语音结束");
                        AiuiController.getInstance().setRobotAnswer(true);
                        isYuYinCompleted = true;
                        return false;
                    }
                });
            } else if (contentType.equals("2")) { //图片
                log.i("图片》》》运行在最上面的页面" + ToolUtil.getInstance().
                        getTopActivity(RyApplication.getContext()));
                if (ToolUtil.getInstance().getTopActivity(RyApplication.getContext()).indexOf("ImageViewActivity") > -1) {
                    log.i("发送广播！");
                    String ACTION_1 = "com.ist.rr.showImage";
                    Intent mIntent = new Intent(ACTION_1);
                    mIntent.putExtra("imagePath", answerContent);
                    RyApplication.getContext().sendBroadcast(mIntent);
                } else {
                    String action =  ToolUtil.getInstance().getAppPackageName() +".ImageViewActivity";
                    log.i("图片action==="+action);
                    Intent intent = new Intent(action);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("imagePath", answerContent);
                    intent.putExtras(bundle);
                    RyApplication.getContext().startActivity(intent);
                }
            } else if (contentType.equals("3")) {//视频
                isVideoCompleted = false;
                log.i("视频");
              /*  String activity = ToolUtil.getInstance().getTopActivity(RyApplication.getContext());
                log.i("开启视频最上边的activity=="+activity );
                if(activity.indexOf("ImageViewActivity") > -1){
                    ActivityController.getInstance().finishCurrentActivity("ImageViewActivity");
                }*/
                String action =  ToolUtil.getInstance().getAppPackageName() +".VideoViewActivity";
                log.i("视频action==="+action);
                Intent intent = new Intent(action);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("path", answerContent);
                intent.putExtras(bundle);
                RyApplication.getContext().startActivity(intent);
            } else if (contentType.equals("4")) { //音频
                isMusicCompleted = false;
                log.i("音频");
                playMusic(answerContent);
            } else if (contentType.equals("5")) { //等待时间
                isTimeCompleted = false;
                log.i("等待时间");
                int time = Integer.parseInt(answerContent);
                log.i("等待时间=="+ time);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        log.i("立体式回复》》》等待时间结束");
                        isTimeCompleted = true;
                    }
                },time * 1000);
            } else if (contentType.equals("6")) { //页面动作
                log.i("页面动作");
                dealPageAction(answerContent);
            } else if (contentType.equals("7")) { //眼部表情
                log.i("眼部表情");
                dealEye(answerContent);
            } else if (contentType.equals("8")) { //头部
                log.i("头部");
                try {
                    dealHead(answerContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (contentType.equals("9")) {//地盘
                isDiPanCompleted = false;
                log.i("底盘");
                dealChassisAction(answerContent, new YindaoCompleteListener() {
                    @Override
                    public void complete() {
                        log.i("立体式回复》》》引导结束");
                        isDiPanCompleted = true;
                        YinDaoController.getInstance().setYindaoCompleteListener(null);
                    }
                });
            } else if (contentType.equals("10")) {//手部动作
                log.i("手部动作");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * 播放音乐
     * @param path
     */
    private void playMusic(final String path) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log.d("唱歌的路径 " + path);
                    MediaVoiceController.playVoice(path, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            log.i("立体式回复》》》音频结束");
                            isMusicCompleted = true;
                        }
                    }, new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            log.i("立体式回复》》》音频出错，结束");
                            isMusicCompleted = true;
                            return false;
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放返回默认语音
     * 是歌曲时候播放
     * 是天气时播放
     * 存在默认回答时播放
     *
     * @param qAnswer 获取的讯飞解析类
     */
    private String errorSpeak(FinalQABean qAnswer, String iflySemantic) {
        log.d("说讯飞返回默认的话");
        speakDefaultAnswerCount = 0;//兜底策略设置为0
        String asrResultJsonOrCmd = qAnswer.getText();
        String answer = "";
        RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
        if (qAnswer.getService() != null) {
            boolean isContiue = dealNewUserQuestion(qAnswer, iflySemantic, ryApplication);
            if (!isContiue) {
                log.d("机器人在主动询问客户问题！");
                return answer;
            }
            try {
                if (qAnswer.getService().equals("music") && ryApplication.isPlayMusic()){//播放音乐
                    log.d("播放音乐");
                    RyApplication application = (RyApplication) RyApplication.getContext().getApplicationContext();
                    if (application.isPlayMusic()) {
                        log.d("配置的是可以播放音乐");
                        final FinalQADataResult finalQADataResult = getMusicCorrect(qAnswer.getData().getResult());
                        if (finalQADataResult != null) {
                            answer = "请欣赏" + finalQADataResult.getName();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    log.d("唱歌的路径 " + finalQADataResult.getDownloadUrl());
                                    MediaVoiceController.playVoice(finalQADataResult.getDownloadUrl(), new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            log.d("唱歌结束了说一下其他的吧 ");
                                            AiuiController.getInstance().post("您是否还需要听其他的歌曲呢？如果需要，请报歌名！");
                                            AiuiController.getInstance().setAllowInterrupt(true);
                                        }
                                    }, new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                            log.d("唱歌错误结束了说一下其他的吧 ");
                                            AiuiController.getInstance().post("歌曲找不到了，换一首歌吧");
                                            AiuiController.getInstance().setAllowInterrupt(true);
                                            return false;
                                        }
                                    });
                                }
                            }).start();
                        }
                    } else {
                        log.i("不播放音乐；用户说的内容是：" + qAnswer.getText());

                        rrPeople(qAnswer.getText(), null, iflySemantic);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (qAnswer.getService().equals("weather")) {//播放天气
                    log.d("播放天气");
                    FinalQADataResult finalQADataResult = qAnswer.getData().getResult().get(0);
                    answer = finalQADataResult.getCity() + "天气" + finalQADataResult.getWeather()
                            + ";空气质量是" + finalQADataResult.getAirQuality()
                            + ";温度" + finalQADataResult.getTempRange()
                            + ";" + finalQADataResult.getWind();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                log.d("其他的回答");
                if (qAnswer.getAnswer() != null) {
                    if (qAnswer.getAnswer().getText() != null) {
                        answer = qAnswer.getAnswer().getText();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (answer != null && !answer.isEmpty()) {
            playVoice(null, answer, false);
        } else {

        }
        addVoiceLog(asrResultJsonOrCmd, answer, "", "", "", "", "", iflySemantic);//错误回答添加的日志
        return asrResultJsonOrCmd;
    }

    /***
     * 播放的歌曲初步选取
     * @param finalQADataResults  歌曲返回列表
     * @return 返回符合要求歌曲
     */
    private FinalQADataResult getMusicCorrect(List<FinalQADataResult> finalQADataResults) {
        FinalQADataResult finalQADataResult = null;
        try {
            List<FinalQADataResult> correctResults = new ArrayList<>();
            for (FinalQADataResult result : finalQADataResults) {
                if (!result.getDownloadUrl().contains("ctimp3")
                        && !result.getDownloadUrl().contains("vbox.hf")
                        && !result.getDownloadUrl().contains(".aac")
                        && !result.getName().contains("\\")
                        && !result.getName().contains("#")
                        && !result.getName().contains("&")
                        && !result.getName().contains("'")
                        && !result.getName().contains("Don")) {
                    correctResults.add(result);
                }
            }
            if (correctResults.size() > 0) {
                log.d("获取到的列表的大小 " + correctResults.size());
                finalQADataResult = correctResults.get(new Random().nextInt(correctResults.size() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalQADataResult;
    }

    /**
     * 值的方法对比
     *
     * @param qaList     对比列表
     * @param matchValue 对比的值
     * @param type       对比的类型
     * @return 返回对比的内容
     */
    public SceneQABean matchValue(List<SceneQABean> qaList, String matchValue, int type) {
        if (type == BY_TOPICID) {
            return dealWewinsVoiceByTop(qaList, matchValue);
        } else if (type == BY_QUE) {
            return dealWewinsVoiceByQue(qaList, matchValue);
        } else {
            return null;
        }
    }

    /**
     * 通过用户说的内容来匹配 所需要的问答
     *
     * @param qaList     问答列表
     * @param matchValue 匹配的字符串
     * @return 匹配的问答
     */
    private SceneQABean dealWewinsVoiceByQue(List<SceneQABean> qaList, String matchValue) {
        log.i("需要匹配的值是ByQue：" + matchValue);
        String userSayPinYin = "";
        if (!matchValue.startsWith("#")){
            userSayPinYin = PinYinUtil.getPinYinFirstToUpperCase(matchValue);
        }
        SceneQABean sceneQABean = null;
        for (SceneQABean qa : qaList) {//循环场景内容
            try {
                String questionKey = qa.getQuestions().replaceAll("#","");
                // 如果没匹配，则继续循环
                if (matchValue.contains(questionKey)  //文字匹配
                        || userSayPinYin.contains(PinYinUtil.getPinYinFirstToUpperCase(questionKey))) {
                    sceneQABean = qa;
                    if (matchValue.equals(questionKey) || userSayPinYin.equals(PinYinUtil.getPinYinFirstToUpperCase(questionKey))) {//拼音匹配
                        return qa;
                    }
                }
            } catch (Exception e) {
                log.i(e.getMessage());
                e.printStackTrace();
            }
        }
        return sceneQABean;
    }

    /***
     * 通过topicId来匹配 所需要的问答
     * @param qaList 问答列表
     * @param matchValue  匹配的字符串
     * @return 匹配的问答
     */
    private SceneQABean dealWewinsVoiceByTop(List<SceneQABean> qaList, String matchValue) {
        log.i("通过TopId需要匹配的值是什么 " + matchValue);
        for (SceneQABean qa : qaList) {//循环场景内容
            try {
                String topicId = qa.getTopicId();
//                log.i("待匹配的topicId》》》"+topicId);
                if(topicId != null){
                    // 如果没匹配，则继续循环
                    if (matchValue.equals(topicId.trim())) {  //文字匹配
                        return qa;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回要说的话拆分 随机播放
     *
     * @param StartContent 匹配到的内容中机器人说的话机器人
     * @return 返回随机选取一个机器人的回答
     */
    private String getSpeakContent(String StartContent) {
//		Log.i(TAG,"能够匹配到的想说机器人总共想说的话 " + StartContent);
        if (StartContent != null && StartContent.contains("#")) {
            String[] answers = StartContent.split("#");
            StartContent = answers[new Random().nextInt(answers.length)];
            log.d("随机获取的话 " + StartContent);
        }
        return StartContent;
    }

    /***
     * rrReople 语义对话条件检查检查匹配内容
     * @param asrResultJsonOrCmd 用户说的话
     */
    private boolean rrPeopleConditionCheck(String asrResultJsonOrCmd) {
        if (asrResultJsonOrCmd == null || asrResultJsonOrCmd.equals("")) {
            log.d("当前用户说的话或者指令问null");
            return false;
        }
        if (pageMap == null) {
            log.d("页面缓存的pageMap等于null");
            return false;
        }
        if (pageKey == null) {
            log.d("页面的key值pageKey等于null");
            return false;
        }
        if (nowSceneBean == null) {
            log.i("没有主场景！");
            return false;
        }
        return true;
    }

    /**
     * 获取场景对话（当前页面下，指定场景id）子场景切换
     *
     * @param sceneId 场景的ID
     * @param pageKey 页面编码_类名
     * @return 返回当前的场景
     */
    private SceneBean getSceneInCurrentPage(String sceneId, String pageKey) {
        SceneBean retSceneBean = null;
        try {
            if (pageMap != null && pageMap.containsKey(pageKey)) {
                PageBean bean = pageMap.get(pageKey);
                log.i("获取到的场景是：" + bean.getSceneBeanList().size());
                // 获取页面对应的所有场景
                List<SceneBean> sceneBeans = bean.getSceneBeanList();
                // 判断是否获取到场景
                if (sceneBeans != null && sceneBeans.size() > 0) {
                    for (SceneBean sceneBean : sceneBeans) {
                        String tmpIsUseExtNlp = sceneBean.getExtNlp(); // 使用外部语义开关：1
                        // 判断是否匹配的场景id
                        if (sceneId.equals(sceneBean.getSceneId())) {
                            retSceneBean = sceneBean;
                            String guidTxt = sceneBean.getGuideTxt();
                            String guidFile = sceneBean.getGuideFile();
                            playVoice(guidFile, guidTxt, true);// 播放引导语
                            // 切换场景
                            nowSceneBean = sceneBean;
                            break;
                        }
                    }
                }
            } else {
                log.i("没有场景的对话！使用公共场景！");
                if (jaScenes != null) {
                    for (SceneBean sceneBean : jaScenes) {
                        if (sceneBean.getSceneId().equals(sceneId)) {
                            log.i("切换子场景！");
                            nowSceneBean = sceneBean;
                            //    defaultSceneQas=sceneBean.getSceneQABeanList();
                            log.i("切换后的场景：" + nowSceneBean.toString());
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retSceneBean;
    }

    /**
     * 语义播放，如果存在语音文件播放语音文件
     * 如果语音文件不存在，存在txt语音内容合成播放语音内容
     * 都不存在则不播放
     *
     * @param voiceFile   语音文件
     * @param voiceTxt    语音内容
     * @param isPlayGuide 是否是播放引导语
     */
    public void playVoice(String voiceFile, String voiceTxt, boolean isPlayGuide) {

        log.i("待播放的语音 文件 " + voiceFile + ";内容： " + voiceTxt + ";是否播放引导语isPlayGuide = " + isPlayGuide);
        if (!isPlayGuide) {
            if (isKF) {
                log.i("不是引导语音播放，不播放内容，内容需要推送给客服！");
                return;
            }
        }

        if (!AiuiController.getInstance().isAllowInterrupt()) {
            log.i("当前场景不允许打断");
            return;
        }
        //如果存在音频文件需要播放
        if (voiceFile != null && voiceFile.trim().length() > 0) {
            //播放语音的位置
            String path = Environment.getExternalStorageDirectory().getPath().concat("/ist/rr/guideFile/");
            String filename = voiceFile.substring(voiceFile.lastIndexOf("/") + 1);
            File file = new File(path + filename);
            if (file.exists()) {
                try {
                    MediaVoiceController.playVoice(path + filename,
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                }
                            },
                            new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                    return false;
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        //播放文字内容
        if (voiceTxt != null && voiceTxt.trim().length() > 0) {
            voiceTxt = replaceGlobalVariable(voiceTxt);
            AiuiController.getInstance().post(voiceTxt);
        }
    }

    /**
     * 替换播放内容中的一些东西
     *
     * @param aswer 所要替换的内容
     *              return 替换后的内容
     */
    private String replaceGlobalVariable(String aswer) {
        try {
            if (aswer == null || aswer.trim().length() == 0) {
                return "";
            }
            // 替换用户名
            if (aswer.toUpperCase().contains("%VAR%")) {
                aswer = aswer.replaceAll("%VAR%", lastVar);
            }
            // 替换用户名
            if (aswer.toUpperCase().contains("%USERNAME%")) {
                String userName = getFacePersonName().replaceAll("\\d+", "");
                aswer = aswer.replaceAll("%USERNAME%", userName);
            }
            if(aswer.toUpperCase().contains("%SEX%")){
                log.d("存在性别需要替换");
                String sex = getPersonSex().replaceAll("\\d+", "");
                log.d("存在性别需要替换 "+sex);
                aswer = aswer.replaceAll("%SEX%", sex);
            }
            // 替换中午下午
            int am = TimeUtil.getPmAndAm();
            if (am == 0) {// 上午
                aswer = aswer.replaceAll("%AMPM%", "上午");
            } else {
                aswer = aswer.replaceAll("%AMPM%", "下午");
            }
            if (aswer.toUpperCase().contains("%RRNAME%")) {
                try {
                    String smeil = LocalDataController.getInstance().getSERVER_DATA_TXT()
                            + LocalDataController.getInstance().getFILE_NAME_ROBOTS();
                    File file = new File(smeil);
                    if (file.exists()) {
                        String robotstr = ToolUtil.getInstance().readFileSdcardFile(smeil);
                        if (robotstr.startsWith("[")) {
                            JSONArray robots = new JSONArray(robotstr);
                            if (robots.length() > 0) {
                                JSONObject robotJsonObject = robots
                                        .getJSONObject(0);
                                String robotName = robotJsonObject
                                        .getString("robotName");
                                aswer = aswer.replaceAll("%RRNAME%", robotName);

                            }
                        } else {
                            JSONObject robotJsonObject = new JSONObject(robotstr);
                            String robotName = robotJsonObject
                                    .getString("robotName");
                            aswer = aswer.replaceAll("%RRNAME%", robotName);
                        }
                    } else {
                        log.i("机器人的名称不存在！");
                        aswer = aswer.replaceAll("%RRNAME%", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (aswer.contains("%USER_CALL%")) {
                if (voiceMessage != null && !voiceMessage.equals("")) {
                    aswer = aswer.replaceAll("%USER_CALL%",
                            getVoiceMessage() + "");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.i("替换后要播放的语音是==" + aswer);
        return aswer;
    }
//----------------------------------------------------------用户讯飞语义匹配的的代码结束-------------------------------------------------

//----------------------------------------------------------场景切换的的代码开始--------------------------------------------------

    /***
     * 场景切换
     * 默认的 场景编号从配置文件中的 program_code 读取，只传递 当前文件的包名加文件名
     * @param className  场景文件全称路径
     */
    public void changeScene(String className) {
        changeScene(SharedPreferencesController.getInstance().getProgramCode(), className);
    }

    /**
     * 切换场景
     *
     * @param businessCode 场景的编号
     * @param className    页面的名称
     */
    @SuppressLint("SimpleDateFormat")
    public void changeScene(final String businessCode, final String className) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dealChangeScene(businessCode, className);
            }
        }).start();
    }

    /***
     * 场景切换处理类
     * @param businessCode
     * @param className
     */
    private void dealChangeScene(String businessCode, String className) {
        try {
            if (sceneChangeJudge(businessCode, className)) {
                String tmpPageKey = businessCode + "_" + className;
                log.i("切换场景开始...... businessCode_className = " + tmpPageKey);
                pageKey = tmpPageKey;
                //通过场景的匹配值获得切换场景的符合的场景页面数据
                PageBean bean = pageMap.get(tmpPageKey);
                if (bean != null) {
                    // 获取页面对应的场景列表数据
                    List<SceneBean> sceneBeans = bean.getSceneBeanList();
                    if (sceneBeans != null && sceneBeans.size() > 0) {
                        for (SceneBean data : sceneBeans) {
                            String isMain = data.getIsMain(); // 1 主场景 0 非场景
                            int isUsePublicScene = data.getUsePublicScene();// 1 使用
                            String tmpIsUseExtNlp = data.getExtNlp(); // 使用外部语义开关
                            log.i("是否是主场景：" + isMain + ",isUsePublicScene：" + isUsePublicScene + ",isUseExtNlp：" + tmpIsUseExtNlp);
                            log.d("场景数据 "+data.toString());
                            if ("1".equals(isMain)) {// 获取主场景
                                // 如果当前在用的场景和需要切换的场景一样就不切换场景
                                if (nowSceneBean != null) {
                                    if (data.getSceneId().equals(nowSceneBean.getSceneId())) {
                                        log.i("场景一样，不用切换");
                                        break;
                                    }
                                }
                                nowSceneBean = data;//当前场景赋值
                                log.i("nowSceneBean = " + nowSceneBean.toString());
                                String guidTxt = data.getGuideTxt();//引导语
                                String guidFile = data.getGuideFile();//引导语音文件
                                try {
                                    //将引导语放在 缓存中 需要吆喝时播放
                                    RyApplication application = (RyApplication) RyApplication.getContext().getApplicationContext();
                                    application.setGuidText(guidTxt);
                                    playVoice(guidFile, guidTxt, true);//播放引导语
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 场景切换后，推送最新的信息给客服
                                try {
                                    JSONObject sceneJson = new JSONObject();
                                    sceneJson.put("robotNo", SharedPreferencesController.getInstance().getRobotNumber());
                                    sceneJson.put("businessCode", bean.getBusinessCode());
                                    sceneJson.put("businessName", bean.getBusinessName());
                                    sceneJson.put("pageId", bean.getPageId());
                                    sceneJson.put("className", bean.getClassName());
                                    sceneJson.put("pageName", bean.getPageName());
                                    sceneJson.put("sceneId", nowSceneBean.getSceneId());
                                    sceneJson.put("sceneName", nowSceneBean.getSceneName());
                                    sceneJson.put("reqCmd", "rrChangeSceneReq");
                                    sceneJson.put("reqSerial", "");
//                                    WebSocketController.getInstance().post(sceneJson.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            } else {
                                log.d("当前的场景列表中无 isMain 为1 的场景");
                            }
                        }
                    } else {
                        log.d("通过场景页面中获取到的场景数据数据为null 或者 大小为0");
                    }
                } else {
                    log.d("通过场景key值获取到的场景页面数据数据为null，但是会将当前的场景赋值为null，语音使用默认公共语音");
                    nowSceneBean = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换到指定的自定义场景
     *
     * @param customSceneId
     */
    private void getCustomScene(String customSceneId) {
        log.i("自定义场景的ID：" + customSceneId);
        if (customSceneMap != null && customSceneMap.containsKey(customSceneId)) {
            CustomSceneBean bean = customSceneMap.get(customSceneId);
            String busscode = bean.getBusinessCode();
            log.i("自定义场景的busscode：" + busscode);
            if ("tp".equals(busscode)) {// 图片

            }
            SceneBean scene = bean.getSceneBean();
            if (scene != null) {
                nowSceneBean = scene;
            }
        }
    }

    /**
     * 进行场景切换时的条件判断
     *
     * @param businessCode 判断的前缀
     * @param className    判断的后缀
     * @return 返回是否符合场景切换的要求
     */
    public boolean sceneChangeJudge(String businessCode, String className) {
        boolean isAllow = true;
        try {
            if (businessCode == null || businessCode.isEmpty()) {
                isAllow = false;
                log.d("场景切换时传入的 businessCode 不符合要求 " + businessCode);
            }
            if (className == null || className.isEmpty()) {
                isAllow = false;
                log.d("场景切换时传入的 className 不符合要求 " + className);
            }
//            if (pageMap == null || pageMap.size()==0 || !pageMap.containsKey(businessCode+"_"+className)){
//                isAllow = false;
//                log.d("场景切换时传入的 pageMap页面列表 不符合要求 ");
//            }
            if (pageMap == null || pageMap.size() == 0) {
                isAllow = false;
                log.d("场景切换时 pageMap为空 不符合要求 ");
            }
        } catch (Exception e) {
            isAllow = false;
            e.printStackTrace();
        }
        return isAllow;
    }
//----------------------------------------------------------场景切换的的代码结束--------------------------------------------------
//----------------------------------------------------------初始化场景数据缓存的代码开始--------------------------------------------------

    /***
     * 本地文件存储
     * 调用全部情景获取接口成功时调用，将信息存储在 sdcard/包名/data/下的文件 填充数据
     * @param bean  传过来的场景对象
     */
    public void saveSceneData(final AllSceneResultBean bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //延迟两秒执行避免数据冲突
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (LocalDataController.getInstance().saveSceneData(bean)) {
                    mTipsBeen = bean.getTipsData();
                    jaScenes = bean.getScenes();
                    jaSceneQas = bean.getSceneQas();
                    jaPages = bean.getPages();
                    jaCustomScenes = bean.getCustomScenes();
                    jsRobots = bean.getRobots();
                    if (jaScenes != null && jsRobots != null && jaPages != null) {
                        initPage();
                    }
                }
            }
        }).start();
    }

    /***
     * 读取本地文件存储信息
     * 当获取全部情景信息接口失败时调用，读取下载在 sdcard/包名/data/下的文件 填充数据
     */
    public void readSceneData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AllSceneResultBean sceneResultBean = LocalDataController.getInstance().readSceneData();
                    if (sceneResultBean != null) {
                        jaScenes = sceneResultBean.getScenes();
                        jaSceneQas = sceneResultBean.getSceneQas();
                        jaPages = sceneResultBean.getPages();
                        jaCustomScenes = sceneResultBean.getCustomScenes();
                        jsRobots = sceneResultBean.getRobots();
                        mTipsBeen = sceneResultBean.getTipsData();
                    }
                    if (jaScenes != null && jsRobots != null && jaPages != null) {
                        initPage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化场景相关的数据
     * 在 saveSceneData与readSceneData 之后调用，将数据存在缓存当中
     */
    private synchronized void initPage() {
        try {
            initSceneQa();
            initSceneBean();
            initPages();
            log.d("初始化页面配置完成 ");
            // 场景初始华完成后，要更新当前在用场景的信息
            if (nowSceneBean != null) {
                String sceneId = nowSceneBean.getSceneId();
                upateSceneInCurrentPage(sceneId, pageKey);
            }
            initCustomSceneMap();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isCompleteData = true;
        }
    }

    /***
     * 初始化场景页面数据
     */
    private void initPages() {
        pageMap = new HashMap<>();
        try {
            if (jaPages != null) {
                for (int i = 0; i < jaPages.size(); i++) {
                    String businessCode = jaPages.get(i).getBusinessCode();
                    String className = jaPages.get(i).getClassName();
                    String pageId = jaPages.get(i).getPageId();
                    List<SceneBean> list = new ArrayList<>();
                    for (int j = 0; j < jaScenes.size(); j++) {
                        String scenePageId = jaScenes.get(j).getPageId();
                        if (pageId.equals(scenePageId)) {
                            list.add(jaScenes.get(j));
                        }
                    }
                    jaPages.get(i).setSceneBeanList(list);
                    pageMap.put(businessCode + "_" + className, jaPages.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 初始化 场景集合
     * @return 场景集合
     */
    private void initSceneBean() {
        try {
            if (jaScenes != null) {
                for (int i = 0; i < jaScenes.size(); i++) {
                    String sceneId = jaScenes.get(i).getSceneId();//当前场景的id
                    List<SceneQABean> list = new ArrayList<>();
                    for (int j = 0; j < jaSceneQas.size(); j++) {
                        String sceneQaId = jaSceneQas.get(j).getSceneId();
                        if (sceneId.equals(sceneQaId)) {
                            list.add(jaSceneQas.get(j));
                        } else if (jaScenes.get(i).getUsePublicScene() == 1) {
                            if (sceneQaId.equals("0")) {
                                list.add(jaSceneQas.get(j));
                            }
                        }
                        jaScenes.get(i).setSceneQABeanList(list);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化场景问答数据
     * 遍历场景问答若其中存在问中有；隔开的将其拆成两条数据
     */
    public void initSceneQa() {
        if (jaSceneQas != null) {
            List<SceneQABean> list = new ArrayList<>();
            defaultSceneQas = new ArrayList<>();
            for (SceneQABean sceneQa : jaSceneQas) {
                if (sceneQa.getQuestions().contains(";")) {
                    String[] questions = sceneQa.getQuestions().split(";");
                    for (String question : questions) {
                        SceneQABean bean = new SceneQABean();
                        bean.setActionCode(sceneQa.getActionCode());
                        bean.setActionType(sceneQa.getActionType());
                        bean.setAnswer(sceneQa.getAnswer());
                        bean.setAnswerFile(sceneQa.getAnswerFile());
                        bean.setPriority(sceneQa.getPriority());
                        bean.setQuestions(question);
                        bean.setSceneId(sceneQa.getSceneId());
                        bean.setSceneQaId(sceneQa.getSceneQaId());
                        bean.setTopicId(sceneQa.getTopicId());
                        bean.setAnswerList(sceneQa.getAnswerList());
                        bean.setId(sceneQa.getId());
                        list.add(bean);
                        if (sceneQa.getSceneId().equals("0")) {
                            defaultSceneQas.add(bean);
                        }
                    }
                } else {
                    if (sceneQa.getSceneId().equals("0")) {
//                        log.i("sceneQa.getQuestions().contains(\";\")==false=="+sceneQa.getTopicId());
                        defaultSceneQas.add(sceneQa);
                    }
                    list.add(sceneQa);
                }
            }
            jaSceneQas = list;
        }
    }

    /***
     * 初始化自定义场景
     * @return 自定义场景列表
     */
    private void initCustomSceneMap() {
        try {
            if (jaCustomScenes != null) {
                customSceneMap = new HashMap<>();
                for (CustomSceneBean bean : jaCustomScenes) {
                    List<SceneBean> scBeans = new ArrayList<>();
                    String customSceneId = bean.getCustomSceneId();
                    for (SceneBean sceneBean : jaScenes) {
                        String sceneId = sceneBean.getSceneId();
                        if (customSceneId.equals(sceneId)) {
                            scBeans.add(sceneBean);
                        }
                    }
                    if (scBeans.size() > 0) {
                        bean.setSceneBean(scBeans.get(0));
                    }
                    customSceneMap.put(bean.getCustomSceneId(), bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前场景信息
     *
     * @param sceneId 场景id
     * @param pageKey 场景的key值
     * @return 返回匹配的当前场景
     */
    private SceneBean upateSceneInCurrentPage(String sceneId, String pageKey) {
        try {
            if (pageMap != null && pageMap.containsKey(pageKey)) {
                PageBean bean = pageMap.get(pageKey);
                log.i("获取到的场景是：" + bean.getSceneBeanList().size());
                // 获取页面对应的所有场景
                List<SceneBean> sceneBeans = bean.getSceneBeanList();
                // 判断是否获取到场景
                if (sceneBeans != null && sceneBeans.size() > 0) {
                    for (SceneBean sceneBean : sceneBeans) {
                        if (sceneId.equals(sceneBean.getSceneId())) {
                            // 切换场景
                            nowSceneBean = sceneBean;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowSceneBean;
    }

//----------------------------------------------------------初始化场景数据缓存的代码结束--------------------------------------------------


    public List<TipsBean> getTipsBeen(String sceneId) {
        if(sceneId == null){
            log.d("所有接口数据 " + mTipsBeen);
            return mTipsBeen;
        }else{
            List<TipsBean> tipsBeen = new ArrayList<>();
            if(mTipsBeen!=null){
                for (TipsBean bean: mTipsBeen) {
//                    log.d("所有接口数据 " + bean.getSceneId()+"传入的值 == "+sceneId);
                    if (bean.getSceneId().equals(sceneId)){
//                        log.d("添加 ");
                        tipsBeen.add(bean);
                    }
                }
                log.d("添加 "+tipsBeen.toString());
            }
            return tipsBeen;
        }
    }

    public boolean isCompleteData() {

        return isCompleteData;
    }

    public void setCompleteData(boolean completeData) {
        isCompleteData = completeData;
    }

    public void setBaseActionBeanList(List<BaseActionBean> baseActionBeanList) {
        mBaseActionBeanList = baseActionBeanList;
    }

    public synchronized List<BaseActionBean> getBaseActionBeanList() {
        return mBaseActionBeanList;
    }

    private String getFacePersonName() {
        RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
        PersonInformationBean person = ryApplication.getPerson();
        if(person!=null){
            log.d("查看人物的信息 "+person.toString());
            facePersonName = person.getName();
            log.d("当前替换的名称 "+facePersonName);
            if (facePersonName == null || facePersonName.equals("null")){
                facePersonName = "";
            }
        }
        return facePersonName;
    }

    private String getPersonSex(){
        RyApplication ryApplication = (RyApplication) RyApplication.getContext().getApplicationContext();
        PersonInformationBean person = ryApplication.getPerson();
        log.d("替换性别查看人员信息 "+person.toString());
        if(person!=null && person.getName()==null){
            log.d("需要替换性别");
            if(person.getGender()==null){
                return "";
            }else if(person.getGender().equals("Male")){
                return "先生";
            }else if(person.getGender().equals("Female")){
                return "女士";
            }else{
                return "";
            }
        }else{
            return "";
        }
    }

    public void setFacePersonName(String facePersonName) {
        this.facePersonName = facePersonName;
    }

    public pageActionListener getPageActionListener() {
        return mPageActionListener;
    }

    public void setPageActionListener(pageActionListener pageActionListener) {
        mPageActionListener = pageActionListener;
    }

    public String getVoiceMessage() {
        return voiceMessage;
    }

    public void setVoiceMessage(String voiceMessage) {
        this.voiceMessage = voiceMessage;
    }

    /**
     * 获取动作的执行列表
     *
     * @param actionCode 动作集合的字符串
     * @return 返回动作执行列表
     */
    private List<BaseActionBean> getBaseActionList(String actionCode) {
        List<BaseActionBean> baseActionBeanList = null;
        try {
            // 如果动作编码为空，则不处理
            if (actionCode != null || !actionCode.isEmpty()) {
                log.i("处理动作： actionCode = " + actionCode);
                // 有动作
                baseActionBeanList = new ArrayList<>();
                if (actionCode.contains("#")) {
                    //动作分解
                    String[] actionSplit = actionCode.split("#");
                    log.i("动作的长度：" + actionSplit.length);
                    //有页面动作
                    if (actionSplit.length > 0) {
                        String pageAction = actionSplit[0];
                        if (!pageAction.equals("page_action")) {
                            BaseActionBean yemianBean = new YemianBean();
                            yemianBean.setAction(pageAction);
                            yemianBean.setTalkErrorAction("没有当前页面");
                            baseActionBeanList.add(yemianBean);
                        }
                    }
                    //有眼睛动作
                    if (actionSplit.length > 1) {
                        String eyeAction = actionSplit[1];
                        if (!eyeAction.equals("eye_actionCode")) {
                            BaseActionBean yanjingBean = new YanjingBean();
                            yanjingBean.setAction(eyeAction);
                            baseActionBeanList.add(yanjingBean);
                        }
                    }
                    //有头部动作
                    if (actionSplit.length > 2) {
                        String headAction = actionSplit[2];
                        if (!headAction.equals("head_actionCode")) {
                            BaseActionBean toubuBean = new ToubuBean();
                            toubuBean.setAction(headAction);
                            baseActionBeanList.add(toubuBean);
                        }
                    }
                    if (actionSplit.length > 3) {
                        String dipanAction = actionSplit[3];
                        if (!dipanAction.equals("dipan_actionCode")) {
                            BaseActionBean dipanBean = new DipanBean();
                            dipanBean.setAction(dipanAction);
                            if (dipanAction.contains("_yindao")) {
                                dipanBean.setActionAfterTalk(false);
                            }
                            dipanBean.setTalkErrorAction("找不到要去的地方了");
                            baseActionBeanList.add(dipanBean);
                        }
                    }
                } else {
                    BaseActionBean yemianBean = new YemianBean();
                    yemianBean.setAction(actionCode);
                    yemianBean.setActionAfterTalk(false);
                    yemianBean.setTalkErrorAction("没有当前页面");
                    baseActionBeanList.add(yemianBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseActionBeanList;
    }

    /***
     * 增加语音日志
     * @param asrResultJsonOrCmd 用户说的话
     * @param matchAnswer  匹配到的答案
     */
    private void addVoiceLog(String asrResultJsonOrCmd, String answer, String voiceFile, String matchQaId, String matchQuestion,
                             String matchAnswer, String actionType, String actionCode, String iflySemantic) {
        try {
            // 写语音识别日志
            if (asrResultJsonOrCmd != null && answer != null && matchQaId != null && matchQuestion != null
                    && matchAnswer != null && actionType != null && actionCode != null) {
                if (nowSceneBean != null) {
                    HttpController.getInstance().addAIDiscernLog(nowSceneBean.getSceneId(), nowSceneBean.getPageId(),
                            asrResultJsonOrCmd, answer, voiceFile, matchQaId, matchQuestion, matchAnswer, actionType, actionCode, iflySemantic);
                } else {
                    HttpController.getInstance().addAIDiscernLog("0", "当前无场景页面", asrResultJsonOrCmd, answer, voiceFile,
                            matchQaId, matchQuestion, matchAnswer, actionType, actionCode, iflySemantic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVoiceLog(String asrResultJsonOrCmd, String answer, String matchQaId, String matchQuestion,
                             String matchAnswer, String actionType, String actionCode, String iflySemantic) {
        // 写语音识别日志
        addVoiceLog(asrResultJsonOrCmd, answer, "", matchQaId, matchQuestion, matchAnswer, actionType, actionCode, iflySemantic);
    }

    private void addVoiceLog(String asrResultJsonOrCmd, SceneQABean sceneQABean) {
        addVoiceLog(asrResultJsonOrCmd, "", "", sceneQABean.getSceneQaId(),
                sceneQABean.getTopicId(), sceneQABean.getAnswer(), sceneQABean.getActionType() + "", sceneQABean.getActionCode());
    }

    /***
     * 推送消息给客服
     */
    private void pushVoiceMessageToCustomerService(PageBean page, String question, String answer, String voiceFile
            , String matchQaId, String matchQuestion, String matchAnswer, String actionType, String actionCode) {
        try {
            JSONObject jsonVoice = new JSONObject();
            jsonVoice.put("robotNo", SharedPreferencesController.getInstance().getRobotNumber());
            jsonVoice.put("businessCode", page.getBusinessCode());
            jsonVoice.put("businessName", page.getBusinessName());
            jsonVoice.put("pageId", page.getPageId());
            jsonVoice.put("className", page.getClassName());
            jsonVoice.put("pageName", page.getPageName());
            jsonVoice.put("sceneId", nowSceneBean.getSceneId());
            jsonVoice.put("sceneName", nowSceneBean.getSceneName());
            jsonVoice.put("question", question);
            jsonVoice.put("answer", answer);
            jsonVoice.put("voiceFile", voiceFile);
            jsonVoice.put("matchQaId", matchQaId);
            jsonVoice.put("matchQuestion", matchQuestion);
            jsonVoice.put("matchAnswer", matchAnswer);
            jsonVoice.put("actionType", actionType);
            jsonVoice.put("actionCode", actionCode);
            jsonVoice.put("reqCmd", "rrVoiceReq");
            jsonVoice.put("reqSerial", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
