package com.ist.rylibrary.base.entity;

import java.util.List;

/**
 * Created by minyuchun on 2017/4/11.
 */

public class FinalQAData {
    private List<FinalQADataResult> result;

    public FinalQAData() {
    }

    public FinalQAData(List<FinalQADataResult> result) {
        this.result = result;
    }

    public List<FinalQADataResult> getResult() {
        return result;
    }

    public void setResult(List<FinalQADataResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FinalQAData{" +
                "numberResult=" + result +
                '}';
    }
}
