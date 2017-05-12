package com.ist.rylibrary.base.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;


/**
 * Created by jk.yeo on 16/3/4 15:28.
 * Mail to ykooze@gmail.com
 */
public class BasicParamsInterceptor implements Interceptor {
    private String TAG = "BasicParamsInterceptor";
    /**参数集合*/
    Map<String, String> queryParamsMap = new HashMap<>();
    /**post参数集合*/
    Map<String, String> paramsMap = new HashMap<>();
    /**头部参数集合*/
    Map<String, String> headerParamsMap = new HashMap<>();
    /**头部参数*/
    List<String> headerLinesList = new ArrayList<>();

    private BasicParamsInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d(TAG,"---------请求开始-----------");
        //请求初始时间
        long startTime = System.nanoTime();
        //获取请求
        Request request = getNewRequest(chain.request());
        Response response = getNewResponse(startTime,chain.proceed(request));
        Log.d(TAG,"---------请求结束-----------");
        return response;
    }

    /**
     * 获取新的请求
     * @param request 请求
     * @return 返回新的请求
     */
    private Request getNewRequest(Request request){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            stringBuilder.append("请求模式 : "+request.method()+"\n");
            Request.Builder requestBuilder = request.newBuilder();
            //将头部参数添加到头部中 开始
            Headers.Builder headerBuilder = request.headers().newBuilder();
            if (headerParamsMap.size() > 0) {
                Iterator iterator = headerParamsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }
            }
            if (headerLinesList.size() > 0) {
                for (String line: headerLinesList) {
                    headerBuilder.add(line);
                }
                requestBuilder.headers(headerBuilder.build());
            }
            // 将头部参数添加到头部  结束

            // process queryParams inject whatever it's GET or POST
            // 将参数添加到请求体中，需要区分是 GET 还是 POST

            //如果 queryParamsMap 有值 将参数拼接到url后面（get请求添加参数）
            if (queryParamsMap.size() > 0 && request.method().equals("GET")) {
                request = injectParamsIntoUrl(request.url().newBuilder(), requestBuilder, queryParamsMap);
//                Request.Builder newBuilder= request.newBuilder();
//                newBuilder.header("Content-Encoding", "gzip");//添加头部信息
//                newBuilder.header("Content-Type","application/json;charset=UTF-8");
//                newBuilder.method(request.method(),gzip(request.body()));
            }
            // process post body inject
            // 给post请求体添加参数
            if (paramsMap != null && paramsMap.size() > 0 && request.method().equals("POST")) {
                if (request.body() instanceof FormBody) {
                    FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                    if (paramsMap.size() > 0) {
                        Iterator iterator = paramsMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            String key = (String) entry.getKey();
                            String value = (String) entry.getValue();
                            newFormBodyBuilder.add(key, value);
                            stringBuilder.append(key+"="+value+"&");
                        }
                    }

                    FormBody oldFormBody = (FormBody) request.body();
                    int paramSize = oldFormBody.size();
                    if (paramSize > 0) {
                        for (int i=0;i<paramSize;i++) {
                            if(!paramsMap.containsKey(oldFormBody.name(i))){
                                newFormBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i));
                                stringBuilder.append(oldFormBody.name(i)+"="+oldFormBody.value(i)+"&");
                            }
                        }
                    }

                    requestBuilder.post(newFormBodyBuilder.build());
                    request = requestBuilder.build();
                } else if (request.body() instanceof MultipartBody) {
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                    Iterator iterator = paramsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String key = (String) entry.getKey();
                        String value = (String) entry.getValue();
                        multipartBuilder.addFormDataPart(key, value);
                        stringBuilder.append(key+"="+value+"&");
                    }
                    List<MultipartBody.Part> oldParts = ((MultipartBody)request.body()).parts();
                    if (oldParts != null && oldParts.size() > 0) {
                        for (MultipartBody.Part part : oldParts) {
                            multipartBuilder.addPart(part);
                            stringBuilder.append(part);
                        }
                    }
                    requestBuilder.post(multipartBuilder.build());
                    request = requestBuilder.build();
                }
                if(stringBuilder.toString().endsWith("&")){
                    if(stringBuilder.length()>0){
                        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            Log.d(TAG,"Received request "+stringBuilder.toString());
        }
        return request;
    }

    /**
     * 获取新的响应
     * @param startTime  请求开始时间
     * @param response  请求响应
     * @return  返回新的响应
     */
    private Response getNewResponse(long startTime,Response response){
        if (response!=null){
            long endTime = System.nanoTime();
            Response.Builder builder = response.newBuilder();
            builder.header("Keep-Alive","timeout=30, max=100")
                    .header("Cache-Control","max-age=0");//缓存 60秒
            response = builder.build();
            Log.d(TAG,String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (endTime - startTime) / 1e6d, response.headers()));

        }
        return response;
    }

    private boolean canInjectIntoBody(Request request) {
        if (request == null) {
            return false;
        }
        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }
        RequestBody body = request.body();
        if (body == null) {
            return false;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }
        if (!TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded")) {
            return false;
        }
        return true;
    }

    /**
     *  func to inject params into url
     *  将参数添加到 URL 后面
     * @param httpUrlBuilder  url的创建
     * @param requestBuilder  请求体的创建
     * @param paramsMap  url拼接参数的值
     * @return 返回新的请求
     */
    private Request injectParamsIntoUrl(HttpUrl.Builder httpUrlBuilder, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        if (paramsMap.size() > 0) {
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
            requestBuilder.url(httpUrlBuilder.build());
            return requestBuilder.build();
        }
        return null;
    }

    /**
     * 请求体参数转换成String格式显示
     * @param request  请求体
     * @return  String格式转换
     */
    private static String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    /***
     * 参数设置
     */
    public static class Builder {
        /**拦截器实例*/
        BasicParamsInterceptor interceptor;
        /**构造函数*/
        public Builder() {
            interceptor = new BasicParamsInterceptor();
        }
        /**post 请求，且 body type 为 x-www-form-urlencoded 时，键值对公共参数插入到 body 参数中，其他情况插入到 url query 参数中。*/
        public Builder addParam(String key, String value) {
            interceptor.paramsMap.put(key, value);
            return this;
        }
        /**同上，不过这里用键值对 Map 作为参数批量插入。*/
        public Builder addParamsMap(Map<String, String> paramsMap) {
            interceptor.paramsMap.putAll(paramsMap);
            return this;
        }
        /**在 header 中插入键值对参数。*/
        public Builder addHeaderParam(String key, String value) {
            interceptor.headerParamsMap.put(key, value);
            return this;
        }
        /**在 header 中插入键值对 Map 集合，批量插入。*/
        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            interceptor.headerParamsMap.putAll(headerParamsMap);
            return this;
        }
        /**在 header 中插入 headerLine 字符串，字符串需要符合 -1 != headerLine.indexOf(“:”) 的规则，即可以解析成键值对。*/
        public Builder addHeaderLine(String headerLine) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + headerLine);
            }
            interceptor.headerLinesList.add(headerLine);
            return this;
        }
        /**同上，headerLineList: List 为参数，批量插入 headerLine。*/
        public Builder addHeaderLinesList(List<String> headerLinesList) {
            for (String headerLine: headerLinesList) {
                int index = headerLine.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
                }
                interceptor.headerLinesList.add(headerLine);
            }
            return this;
        }
        /**插入键值对参数到 url query 中。*/
        public Builder addQueryParam(String key, String value) {
            interceptor.queryParamsMap.put(key, value);
            return this;
        }
        /**插入键值对参数 map 到 url query 中，批量插入*/
        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            interceptor.queryParamsMap.putAll(queryParamsMap);
            return this;
        }

        public BasicParamsInterceptor build() {
            return interceptor;
        }

    }

    private RequestBody gzip(final RequestBody body){
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() throws IOException {
                return -1;// We don't know the compressed length in advance!
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
