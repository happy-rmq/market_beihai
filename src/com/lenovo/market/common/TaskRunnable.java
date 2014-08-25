package com.lenovo.market.common;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Message;

import com.lenovo.market.common.TaskConstant.ResultType;
import com.lenovo.market.service.MainService;
import com.lenovo.market.vo.local.RequestVo;

public class TaskRunnable {

    private static LinkedBlockingQueue<Integer> mIdQueue = new LinkedBlockingQueue<Integer>(); // 待执行的任务队列

    public static Runnable solomoRunnable = new Runnable() {

        @Override
        public void run() {
            Integer taskID = mIdQueue.poll();
            if (taskID != null) {
                getDataFromServer(taskID);
            }
        }
    };

    /**
     * 往任务id队列添加任务id
     * 
     * @param taskID
     */
    public static void setTaskID(int taskID) {
        mIdQueue.add(taskID);
    }

    /**
     * 根据任务类型执行网络任务
     * 
     * @param taskID
     *            任务类型
     */
    private static void getDataFromServer(int taskID) {
        Task task = MainService.getTaskByID(taskID);
        if (task == null) {
            return;
        }
        Message message = Message.obtain();
        message.arg1 = taskID;
        if (task.isCancel()) {
            message.what = ResultType.CANCEL;
            MainService.sHandler.sendMessage(message);
            return;
        }
        try {
            RequestVo reqVo = task.getReqVo();
            System.out.println("rpc==============++++++++++++++");
            SoapObject rpc = new SoapObject(reqVo.getNameSpace(), reqVo.getMethodName());// 构建soap对象 参数 命名空间 方法名称
            System.out.println("taskID : " + taskID + "; Method-------------------" + rpc);
            if (reqVo.getMaps() != null) {
                for (Map.Entry<String, Object> mp : reqVo.getMaps().entrySet()) {
                    rpc.addProperty(mp.getKey(), mp.getValue());
                    System.out.println(mp.getKey() + " : " + mp.getValue());
                }
            }
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);// soap对象描述
                                                                                                   // 将先前创建的soap对象设置为它的bodyout
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(reqVo.getRequestUrl());// 构造可以设置webservice的wsdl的url
            System.out.println("taskID : " + taskID + "; service : " + reqVo.getRequestUrl());
            ht.call(null, envelope);// 使用call调用webservice的方法 第一个参数一般为空 第二个参数即为soap对象的描述
            
            SoapObject soapObject = (SoapObject) envelope.bodyIn;// 得到返回结果
            // 也可以用这个
            // SoapObject soapObject2=(SoapObject) envelope.getResponse();
            Object property = soapObject.getProperty(0);
            System.out.println("taskID : " + taskID + "; result======" + property.toString());
            message.what = ResultType.SUCCEED;
            message.arg1 = taskID;
            message.obj = property.toString();
        } catch (Exception e) {
            e.printStackTrace();
            message.what = ResultType.FAILD;
            message.obj = e.getMessage();
        }

        if (message != null) {
            MainService.sHandler.sendMessage(message);
            task.setCcm(null);
        }
    }
}
