package com.example.mghlcs.login.Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mghlcs on 11/2/16.
 */

public class KidneyHotList {
    private String mrn;
    private String abo;
    private String patientName;
    private String meld;
    private String age;
    private String distance;


    private String otherOrgans;
    private String ecd;
    private String dcd;
    private String ird;
    private String epts;
    private String listDate;
    private String listStatus;

    private String waitingTimePoints;
    private String totalPoints;
    private String cpraPoints;
    private String readiness;
    private String readinessDate;
    private String readinessDay;
    private String readinessComment;

    private String currentLocation;
    private String dialysisSchedule;

    private String height;
    private String weight;
    private String preTxBrief;
    private String preTxFull;
    private String refMdBrief;
    private String refMdFull;
    private String txCoorBrief;
    private String txCoorFull;
    private String dx;

    public KidneyHotList(JSONObject item) {
        try {
            this.mrn                = item.getString("mrn");
            this.abo                = item.getString("abo");
            this.patientName        = item.getString("name");
            this.age                = item.getString("age");
            this.distance           = item.getString("distance");
            this.weight             = item.getString("weight");
            this.otherOrgans        = item.getString("other_organs");
            this.readiness          = item.getString("readiness");
            this.waitingTimePoints  = item.getString("waiting_time_points");
            this.cpraPoints         = item.getString("cpra_points");
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String HotListSubTitle() {
        String subTitle = this.getAge() + ", " + this.getDistance() + ", WPTS:" + this.getWaitingTimePoints() + "; CPRA:" + this.getCpraPoints() + "; " + this.getWeight();
        return subTitle;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getAbo() {
        return abo;
    }

    public void setAbo(String abo) {
        this.abo = abo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMeld() {
        return meld;
    }

    public void setMeld(String meld) {
        this.meld = meld;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOtherOrgans() {
        return otherOrgans;
    }

    public void setOtherOrgans(String otherOrgans) {
        this.otherOrgans = "+"+otherOrgans;
    }

    public String getEcd() {
        return ecd;
    }

    public void setEcd(String ecd) {
        this.ecd = ecd;
    }

    public String getDcd() {
        return dcd;
    }

    public void setDcd(String dcd) {
        this.dcd = dcd;
    }

    public String getIrd() {
        return ird;
    }

    public void setIrd(String ird) {
        this.ird = ird;
    }

    public String getEpts() {
        return epts;
    }

    public void setEpts(String epts) {
        this.epts = epts;
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    public String getListStatus() {
        return listStatus;
    }

    public void setListStatus(String listStatus) {
        this.listStatus = listStatus;
    }

    public String getWaitingTimePoints() {
        return waitingTimePoints;
    }

    public void setWaitingTimePoints(String waitingTimePoints) {
        this.waitingTimePoints = waitingTimePoints;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getCpraPoints() {
        return cpraPoints;
    }

    public void setCpraPoints(String cpraPoints) {
        this.cpraPoints = cpraPoints;
    }

    public String getReadiness() {
        return readiness;
    }

    public void setReadiness(String readiness) {
        this.readiness = readiness;
    }

    public String getReadinessDate() {
        return readinessDate;
    }

    public void setReadinessDate(String readinessDate) {
        this.readinessDate = readinessDate;
    }

    public String getReadinessDay() {
        return readinessDay;
    }

    public void setReadinessDay(String readinessDay) {
        this.readinessDay = readinessDay;
    }

    public String getReadinessComment() {
        return readinessComment;
    }

    public void setReadinessComment(String readinessComment) {
        this.readinessComment = readinessComment;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDialysisSchedule() {
        return dialysisSchedule;
    }

    public void setDialysisSchedule(String dialysisSchedule) {
        this.dialysisSchedule = dialysisSchedule;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPreTxBrief() {
        return preTxBrief;
    }

    public void setPreTxBrief(String preTxBrief) {
        this.preTxBrief = preTxBrief;
    }

    public String getPreTxFull() {
        return preTxFull;
    }

    public void setPreTxFull(String preTxFull) {
        this.preTxFull = preTxFull;
    }

    public String getRefMdBrief() {
        return refMdBrief;
    }

    public void setRefMdBrief(String refMdBrief) {
        this.refMdBrief = refMdBrief;
    }

    public String getRefMdFull() {
        return refMdFull;
    }

    public void setRefMdFull(String refMdFull) {
        this.refMdFull = refMdFull;
    }

    public String getTxCoorBrief() {
        return txCoorBrief;
    }

    public void setTxCoorBrief(String txCoorBrief) {
        this.txCoorBrief = txCoorBrief;
    }

    public String getTxCoorFull() {
        return txCoorFull;
    }

    public void setTxCoorFull(String txCoorFull) {
        this.txCoorFull = txCoorFull;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getTxNote() {
        return txNote;
    }

    public void setTxNote(String txNote) {
        this.txNote = txNote;
    }

    public String getKDPI() {
        return KDPI;
    }

    public void setKDPI(String KDPI) {
        this.KDPI = KDPI;
    }

    private String txNote;
    private String KDPI;
}
