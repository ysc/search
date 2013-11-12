/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.apdplat.demo.test;

import java.io.IOException;
import org.apdplat.search.util.baidu.JsoupBaiduInfoUtil;

/**
 *
 * @author JONE
 */
public class JsoupParseDemo {
    public static void main(String[] args) throws IOException{
       JsoupBaiduInfoUtil jb = new JsoupBaiduInfoUtil("钟琼",1);
       
         String totalText = jb.getResultsCountText();
         System.out.println(totalText);
    }
}
