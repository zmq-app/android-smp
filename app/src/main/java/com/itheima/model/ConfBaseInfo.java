package com.itheima.model;

public class ConfBaseInfo {
    /**
     * conference size
     * 会议个数
     */
    private int size;

    /**
     * conf ID
     * 会议id
     */
    private String confID;

    /**
     * conference subject
     * 会议主题
     */
    private String subject;

    /**
     * Access number
     * 会议接入号
     */
    private String accessNumber;

    /**
     * chairman pwd
     * 主席密码
     */
    private String chairmanPwd;

    /**
     * guest pwd
     * 普通与会者密码
     */
    private String guestPwd;

    /**
     * conference start time
     * 会议开始时间
     */
    private String startTime;

    /**
     * conference end time
     * 会议结束时间
     */
    private String endTime;
    private String schedulerNumber;
    private String schedulerName;
    private String groupUri;

    /**
     * Lock
     * 是否锁定
     */
    private boolean isLock;

    /**
     * record
     * 是否录制
     */
    private boolean record;

    /**
     * record
     * 是否录制
     */
    private boolean supportRecord;

    /**
     * Mute all
     * 是否全部静音
     */
    private boolean isMuteAll;

    /**
     * Join conference
     * 是否加入会议
     */
    private boolean isJoin;

    /**
     * Allow un mute
     * 是否允许解除静音
     */
    private boolean isAllowUnMute;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber) {
        this.accessNumber = accessNumber;
    }

    public String getChairmanPwd() {
        return chairmanPwd;
    }

    public void setChairmanPwd(String chairmanPwd) {
        this.chairmanPwd = chairmanPwd;
    }

    public String getGuestPwd() {
        return guestPwd;
    }

    public void setGuestPwd(String guestPwd) {
        this.guestPwd = guestPwd;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSchedulerNumber() {
        return schedulerNumber;
    }

    public void setSchedulerNumber(String schedulerNumber) {
        this.schedulerNumber = schedulerNumber;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isSupportRecord() {
        return supportRecord;
    }

    public void setSupportRecord(boolean supportRecord) {
        this.supportRecord = supportRecord;
    }

    public boolean isMuteAll() {
        return isMuteAll;
    }

    public void setMuteAll(boolean muteAll) {
        isMuteAll = muteAll;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

    public boolean isAllowUnMute() {
        return isAllowUnMute;
    }

    public void setAllowUnMute(boolean allowUnMute) {
        isAllowUnMute = allowUnMute;
    }

    @Override
    public String toString() {
        return "ConfBaseInfo{" +
                "size=" + size +
                ", confID='" + confID + '\'' +
                ", subject='" + subject + '\'' +
                ", accessNumber='" + accessNumber + '\'' +
                ", chairmanPwd='" + chairmanPwd + '\'' +
                ", guestPwd='" + guestPwd + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", schedulerNumber='" + schedulerNumber + '\'' +
                ", schedulerName='" + schedulerName + '\'' +
                ", groupUri='" + groupUri + '\'' +
                ", isLock=" + isLock +
                ", record=" + record +
                ", supportRecord=" + supportRecord +
                ", isMuteAll=" + isMuteAll +
                ", isJoin=" + isJoin +
                '}';
    }
}