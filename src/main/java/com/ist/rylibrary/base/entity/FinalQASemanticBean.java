package com.ist.rylibrary.base.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class FinalQASemanticBean {
    private FinalQASemanticSlotsBean slots;

    public FinalQASemanticBean() {
    }

    public FinalQASemanticBean(FinalQASemanticSlotsBean slots) {
        this.slots = slots;
    }

    public FinalQASemanticSlotsBean getSlots() {
        return slots;
    }

    public void setSlots(FinalQASemanticSlotsBean slots) {
        this.slots = slots;
    }

    @Override
    public String toString() {
        return "FinalQASemanticBean{" +
                "slots=" + slots +
                '}';
    }
}
