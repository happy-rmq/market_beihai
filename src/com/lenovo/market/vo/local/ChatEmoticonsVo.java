package com.lenovo.market.vo.local;

/**
 ****************************************** 
 * @文件描述 : 表情符号实体
 ****************************************** 
 */
public class ChatEmoticonsVo {
    
    private int id;/** 表情资源图片对应的ID */
    private String character;/** 表情资源对应的文字描述 */
    private String fileName;/** 表情资源的文件名 */
   
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
