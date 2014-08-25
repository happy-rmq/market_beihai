package com.lenovo.market.util;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.lenovo.market.vo.server.UserVo;

public class UpdateVCard {

    public static void updateInformation(XMPPConnection connection, UserVo user) throws XMPPException{
        VCard vcard = new VCard();
        vcard.setField("uid",user.getUid());
        vcard.setField("account",user.getAccount());
        vcard.setField("userName",user.getUserName());
        vcard.setField("sex",user.getSex());
        vcard.setField("uid",user.getUid());
        vcard.setField("picture", user.getPicture());
        vcard.setField("area",user.getArea());
        vcard.setField("sign", user.getSign());
        vcard.setField("qrCode", user.getQrCode());
//        vcard.setField("userType", user.getUserType());
//        vcard.setField("userFlag", user.getUserFlag());
        vcard.save(connection);
    }
}
