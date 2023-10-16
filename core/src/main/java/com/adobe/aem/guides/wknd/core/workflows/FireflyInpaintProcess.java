package com.adobe.aem.guides.wknd.core.workflows;

import com.adobe.granite.workflow.exec.WorkflowProcess;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import com.day.cq.commons.jcr.JcrConstants;

@Component(service = WorkflowProcess.class, immediate = true, property = {
                "process.label = Firefly Inpainting Process" })
public class FireflyInpaintProcess implements WorkflowProcess {
        private static final Logger LOG = LoggerFactory.getLogger(FireflyInpaintProcess.class);

        @Override
        public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
                        throws WorkflowException {
                String[] params = args.get("PROCESS_ARGS", "string").toString().split(",");
                String ffEP = params[0];
                String assetPath = workItem.getWorkflowData().getPayload().toString();
                LOG.info("Firefly EP is " + ffEP + "and assetPath is " + assetPath);

                ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
                // convert the payload path into a Resource
                Resource payloadResource = resourceResolver.resolve(assetPath);
                Resource payloadJcrRes = payloadResource.getChild(JcrConstants.JCR_CONTENT);
                ValueMap valueMap = payloadJcrRes.adaptTo(ValueMap.class);
                String ffPrompt = valueMap.get("fireflyPrompt", String.class);
                LOG.info("Firefly prompt is " + ffPrompt);

                try {
                        String encodedPromptValue = URLEncoder.encode(ffPrompt, StandardCharsets.UTF_8.toString());
                        String encodedAssertPath = URLEncoder.encode(assetPath, StandardCharsets.UTF_8.toString());

                        String fireflyUrl = ffEP.replace("{assetPath}", encodedAssertPath).replace("{prompt}",
                                        encodedPromptValue);
                        LOG.info("Firefly url is " + fireflyUrl);

                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet request = new HttpGet(fireflyUrl);
                        HttpResponse response = httpClient.execute(request);
                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

                        LOG.info("Firefly response: " + responseString);

                } catch (Exception e) {
                        throw new WorkflowException("Failed to execute HTTP request", e);
                }

        }

}
