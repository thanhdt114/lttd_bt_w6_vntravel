package com.example.vntravel.reqCallBack;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Consumer;

public class PlaceReqCallBack extends UrlRequest.Callback {
    private String result;
    private final Consumer<JSONArray> handler;

    public PlaceReqCallBack(Consumer<JSONArray> handler) {
        this.handler = handler;
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        if (info.getHttpStatusCode() == 200) {
            request.read(ByteBuffer.allocateDirect(10240));
        }
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        if (info.getHttpStatusCode() == 200) {
            request.read(ByteBuffer.allocateDirect(10240));
        }
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        byteBuffer.flip();

        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        this.result = new String(bytes);

        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(this.getClass().getSimpleName(),"Succeeded:");
        Log.i(this.getClass().getSimpleName(),  result);

        try {
            JSONArray jsonArray = new JSONArray(this.result);
            this.handler.accept(jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.e(this.getClass().getSimpleName(), Objects.requireNonNull(error.getMessage()));
    }
}
