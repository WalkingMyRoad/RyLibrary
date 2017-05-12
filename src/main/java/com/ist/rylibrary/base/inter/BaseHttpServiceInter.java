package com.ist.rylibrary.base.inter;

import com.ist.rylibrary.base.controller.SharedPreferencesController;
import com.ist.rylibrary.base.entity.AllInfoByRobotIdBean;
import com.ist.rylibrary.base.entity.AllSceneResultBean;
import com.ist.rylibrary.base.entity.DefaultResultBean;
import com.ist.rylibrary.base.entity.HttpDefauleResultBean;
import com.ist.rylibrary.base.entity.MallDictsBean;
import com.ist.rylibrary.base.entity.MallDictsResultBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by minyuchun on 2017/4/7.
 */

public interface BaseHttpServiceInter {

    @GET("rrs/getMallDict")
    Observable<MallDictsResultBean<List<MallDictsBean>>> getMallDicts(
            @Query("type") String type
    );

    @GET("rrs/getAllSceneAndBot")
    Observable<AllSceneResultBean> getAllSceneAndBot(
    );

    @FormUrlEncoded
    @POST("rrs/getAllInfoByRobotId")
    Observable<HttpDefauleResultBean<List<AllInfoByRobotIdBean>>> getAllInfoByRobotId(
            @Field("robotNo") String robotNo
    );


    @GET("rrs/addAIDiscern")
    Observable addAIDiscernLog(
            @Query("sceneId") String sceneId,
            @Query("pageId") String pageId,
            @Query("question") String question,
            @Query("answer") String answer,
            @Query("voiceFile") String voiceFile,
            @Query("matchQaId") String matchQaId,
            @Query("matchQuestion") String matchQuestion,
            @Query("matchAnswer") String matchAnswer,
            @Query("actionType") String actionType,
            @Query("actionCode") String actionCode
    );

    @FormUrlEncoded
    @POST("rrs/addAIDiscern")
    Observable<DefaultResultBean> addAIDiscernLog(
            @FieldMap Map<String,String> map
    );

//    @FormUrlEncoded
//    @POST("rrs/addVoiceLog")
//    Observable<DefaultResultBean> addVoiceLog(
//            @Field("iflySemantic") String iflySemantic,
//            @Field("wewinsSementic") String wewinsSementic,
//            @Field("actionCode") String actionCode,
//            @Field("playContent") String playContent,
//            @Field("readResult")String readResult,
//            @Field("mallId") String mallId,
//            @Field("robotNo") String robotNo
//    );


    @FormUrlEncoded
    @POST("rrs/addVoiceLog")
    Observable<DefaultResultBean> addVoiceLog(
            @FieldMap Map<String ,String> map
    );
}
