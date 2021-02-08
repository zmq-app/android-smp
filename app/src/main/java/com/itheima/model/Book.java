package com.itheima.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Subject 实体model Book,通过Parcelable实现序列化和反序列化
 * @Author zhangming
 * @Date 2018-10-04
 */
public class Book implements Parcelable {
    private int bookId;
    private String bookName;

    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    /** 返回当前对象的内容描述,几乎都返回0 **/
    @Override
    public int describeContents() {
        return 0;
    }

    /** 将当前对象写入到序列化结构中,flags几乎所有情况都为0 **/
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }

    private Book(Parcel in){
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>(){
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return bookId + " " + bookName;
    }
}
