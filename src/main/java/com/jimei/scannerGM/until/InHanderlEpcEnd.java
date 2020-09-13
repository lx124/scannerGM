package com.jimei.scannerGM.until;

import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
/**
 * 6c标签上传结束类 监听结束
 * @author lixin
 *
 */
public class InHanderlEpcEnd implements HandlerTagEpcOver{
	public void log(String arg0, LogBaseEpcOver arg1) {
        System.out.println("HandlerTagEpcOver");
        if(InventoryTagFileIO.printWriter != null) {
        	InventoryTagFileIO.closeWrite();
        	InHanderlEpcStart.logs.clear();
        	InHanderlEpcStart.setCount(0);
        }
	}

}
