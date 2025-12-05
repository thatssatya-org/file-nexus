package com.samsepiol.file.nexus.transfer.workflow.activites.impl;


import com.samsepiol.file.nexus.transfer.workflow.activites.ISendEventActivity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@RequiredArgsConstructor
@Slf4j
public class SendEventActivity implements ISendEventActivity {

    

    @Override
    public void sendEvent(String eventName, Map<String, Object> eventMetadata) {
        // TODOeventHelper.publish(eventName, eventMetadata);
    }
}
