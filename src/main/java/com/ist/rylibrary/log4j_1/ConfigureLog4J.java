/* 
   Copyright 2011 Rolf Kulemann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ist.rylibrary.log4j_1;

import android.os.Environment;

import com.ist.rylibrary.base.controller.LocalDataController;

import org.apache.log4j.Level;

import java.io.File;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 日志设置
 */
public class ConfigureLog4J {

//日志级别优先度从高到低:OFF(关闭),FATAL(致命),ERROR(错误),WARN(警告),INFO(信息),DEBUG(调试),ALL(打开所有的日志，我的理解与DEBUG级别好像没有什么区别得)
//Log4j建议只使用FATAL ,ERROR ,WARN ,INFO ,DEBUG这五个级别。
	// "yyyy-MM-dd");// 日志的输出格式

	public static void configure(boolean isUseLogCat) {
		try{
			final LogConfigurator logConfigurator = new LogConfigurator();
			Date nowtime = new Date();
			// String needWriteMessage = myLogSdf.format(nowtime);
			//日志文件保存路径地址
			String fileName = LocalDataController.getInstance().getBASE_PATH_LOCAL_DATA()+
					File.separator + "log"+ File.separator + "test.log";
//			String fileName = Environment.getExternalStorageDirectory()
//					+ File.separator + "myc" + File.separator + "log"
//					+ File.separator + "test.log";
			//设置文件名
			logConfigurator.setFileName(fileName);
			//设置root日志输出级别 默认为DEBUG
			logConfigurator.setRootLevel(Level.DEBUG);
			// 设置日志输出级别
			logConfigurator.setLevel("org.apache", Level.INFO);
			//设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
			logConfigurator.setFilePattern("%d %-5p [ %c{3} ] %m%n");
			//设置输出到控制台的文字格式 默认%m%n
			logConfigurator.setLogCatPattern("%m%n");
			//设置总文件大小
			logConfigurator.setMaxFileSize(1024 * 1024 * 5);
			//设置最大产生的文件个数
			logConfigurator.setMaxBackupSize(1);
			//设置所有消息是否被立刻输出 默认为true,false 不输出
			logConfigurator.setImmediateFlush(true);
			//是否本地控制台打印输出 默认为true ，false不输出
			logConfigurator.setUseLogCatAppender(isUseLogCat);
			//设置是否启用文件附加,默认为true。false为覆盖文件
			logConfigurator.setUseFileAppender(true);
			//设置是否重置配置文件，默认为true
			logConfigurator.setResetConfiguration(true);
			//是否显示内部初始化日志,默认为false
			logConfigurator.setInternalDebugging(false);

			logConfigurator.configure();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}