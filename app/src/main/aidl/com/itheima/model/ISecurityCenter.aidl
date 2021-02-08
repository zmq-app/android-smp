package com.itheima.model;

interface ISecurityCenter {
    String encrypt(in String content);
    String decrypt(in String password);
}