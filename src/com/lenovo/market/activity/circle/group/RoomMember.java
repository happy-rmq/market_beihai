package com.lenovo.market.activity.circle.group;

import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 群组添加成员
 * 
 * @author zhouyang
 * 
 */
public class RoomMember extends FriendMesVo {
    private static final long serialVersionUID = 1L;
    public static final int FILLING_ITEM = 0;
    public static final int ADD_ITEM = 1;
    public static final int DEL_ITEM = 2;
    /**
     * 0 --- 填充用的item不显示 1----加号 item 2----减号 item
     */
    private int itemType;

    public RoomMember(int type) {
        super("");
        itemType = type;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
