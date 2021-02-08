package com.itheima.model;

import com.itheima.model.Book;

interface IBookManager {
    void addBook(in Book book);
    List<Book> getBookList();
}
