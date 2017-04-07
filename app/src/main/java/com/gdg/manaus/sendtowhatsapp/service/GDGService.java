package com.gdg.manaus.sendtowhatsapp.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class GDGService extends AccessibilityService {

    private final String TAG = this.getClass().getSimpleName();

    private final String waTextFieldID = "com.whatsapp:id/entry";
    private final String waButtonSendID = "com.whatsapp:id/send";
    private final String waBackButtonID = "com.whatsapp:id/back";

    private static String textToSend;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        // get the source node of the event
        AccessibilityNodeInfo nodeInfo = event.getSource();

        if (nodeInfo == null)
            return;

        String packageName = nodeInfo.getPackageName().toString();
        Log.i(TAG, packageName);

        performWhatsAppMessage(nodeInfo);

        // recycle the nodeInfo object
        nodeInfo.recycle();
    }

    private void performWhatsAppMessage(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo textNode = getNodeInfo(rootNode, waTextFieldID);

        if (textNode == null || textToSend == null)
            return;

        Bundle textBundle = new Bundle();
        textBundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE
                , textToSend);

        textNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, textBundle);

        // The send node is available only when after the text is input.
        AccessibilityNodeInfo buttonSend = getNodeInfo(rootNode, waButtonSendID);

        if (buttonSend == null)
            return;

        buttonSend.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        AccessibilityNodeInfo backButton = getNodeInfo(rootNode, waBackButtonID);
        if (backButton != null)
            backButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    private AccessibilityNodeInfo getNodeInfo(AccessibilityNodeInfo rootNode, String viewId) {
        AccessibilityNodeInfo node = null;

        List<AccessibilityNodeInfo> nodes = rootNode
                .findAccessibilityNodeInfosByViewId(viewId);

        if (nodes.size() > 0)
            node = nodes.get(0);

        return node;
    }

    @Override
    public void onInterrupt() {

    }

    public static void setTextToSend(String text) {
        textToSend = text;
    }
}
