package com.fro.wifi_temhumcase;

public class Constant {

	//IP地址
	public static String IP="192.168.0.100";
	public static String SUN_IP="192.168.0.103";
	//端口
	public static int port=4001;
	//温湿度查询命令
	public static String TEMHUM_CHK="01 03 00 14 00 02 84 0f";
	public static int TEMHUM_LEN=9;
	public static int TEMHUM_NUM=1;
	//光照度查询命令
	public static String SUN_CHK="01 03 00 2a 00 01 a5 c2";
	public static int SUN_LEN=7;
	public static int SUN_NUM=1;
	
}
