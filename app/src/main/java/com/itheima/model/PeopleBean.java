package com.itheima.model;


import java.io.Serializable;
import java.util.Objects;

/**
 * 用户bean
 */
public class PeopleBean implements Serializable {
    //    "extendProperty": "",
//    "convertId": "",
//    "text": "毕玉国",
//    "value": "591@14916"
    //sip号码
    public String extendProperty;

    //用户id
    public String convertId;

    //名称
    public String text;

    public String value;

    //组织名称
    public String fullDepName;

    //是否已选
    public boolean isCheck = false;

    //是否显示下划线
    public boolean showLine = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeopleBean that = (PeopleBean) o;
        return extendProperty.equals(that.extendProperty) &&
                convertId.equals(that.convertId) &&
                text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extendProperty, convertId, text);
    }

    @Override
    public String toString() {
        return "PeopleBean{" +
                "extendProperty='" + extendProperty + '\'' +
                ", convertId='" + convertId + '\'' +
                ", text='" + text + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        PeopleBean that = (PeopleBean) o;
//        return extendProperty.equals(that.extendProperty) &&
//                convertId.equals(that.convertId) &&
//                text.equals(that.text) &&
//                value.equals(that.value);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(extendProperty, convertId, text, value);
//    }
}
