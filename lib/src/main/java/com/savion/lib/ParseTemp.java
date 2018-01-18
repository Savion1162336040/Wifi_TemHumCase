package com.savion.lib;

import java.util.Arrays;

/**
 * Created by Administrator on 2018-01-18.
 */

public class ParseTemp {
    public static void main(String[] strings){
        ParseTemp parseTemp = new ParseTemp();
        byte[] bytes = parseTemp.calBytes(new byte[]{1,2,3,4,5,0,4});
//        System.out.print(String.format("before:%s",bytes.toString()));
//        Float f = parseTemp.getTemData(9,1,bytes);
//        System.out.print(String.format("after:%s",f==null?"null":f.toString()));

        Arrays.asList(bytes).stream().forEach(System.out::println);
        System.out.println(bytes.toString());


    }

    public byte[] calBytes(byte[] bytes){
        int[] crc = CRCValidate.calculateCRC(bytes,0,bytes.length);
       // System.out.print(String.format("calCRC:%s",crc==null?"null":crc.toString()));
        byte[] result = new byte[bytes.length+(crc!=null?crc.length:0)];
        System.arraycopy(bytes,0,result,0,bytes.length);
        for (int i=0;i<crc.length;i++){
            result[bytes.length+i] = (byte) crc[i];
        }
        return result;
    }

    public Float getTemData(int rightLen, int nodeNum, byte[] read_buff) {

        Float data =null;
        if (read_buff!=null) {
            // 长度是否正确，节点号是否正确，CRC是否正确
            if ((read_buff.length == rightLen && read_buff[0] == nodeNum)
                    && CRCValidate.isCRCConfig(read_buff)) {
                /******************** CRC校验正确之后做的，解析数据 ********************/
                byte[] data_buff = new byte[2];//存放数据数组
                //数据开始位,第（六）位开始
                int dataOffset = 5;
                //抠出数据，放进data_buff。
                //（要拷贝的数组源，拷贝的开始位置，要拷贝的目标数组，填写的开始位置，拷贝的长度）
                System.arraycopy(read_buff, dataOffset, data_buff, 0, 2);
                //解析数据data_buff（16进制转10进制）
                data = ByteToFloatUtil.hBytesToFloat(data_buff);

                /*********除以10返回数据**********/
                data=(data / 10.0f);
                //在正常范围内才返回
                if(data>0 && data<100) {
                    return data;
                }
            }
        }
        return data;// 返回数据
    }

}
