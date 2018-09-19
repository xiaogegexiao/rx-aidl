package com.xiao.aidlexample;
import com.xiao.aidlexample.MainObject;

interface IMainService {
    MainObject[] listFiles(String path);
}
