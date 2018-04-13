/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.s2.fugue.example.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.s2.common.fault.ProgramFault;
import org.symphonyoss.s2.fugue.FugueServer;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

public class PubSubExmple extends FugueServer implements IPubSubExmple
{
  public static final String TOPIC_NAME        = "Test-Topic";
  public static final String SUBSCRIPTION_NAME = "Test-Topic-Subscription";
  
  private static final Logger log_ = LoggerFactory.getLogger(PubSubExmple.class);

  private StringBuilder status_ = new StringBuilder("Initializing...\n");
  
  public PubSubExmple()
  {
    super("PubSubExmple", 8080);
    
    registerServlet(new PubServlet(this));
    registerServlet(new SubServlet(this));
  }
  
  @Override
  public void appendStatus(String message)
  {
    status_.append(message);
    if(!message.endsWith("\n"))
      status_.append('\n');
  }
  
  @Override
  public String getStatus()
  {
    return status_.toString();
  }

  @Override
  public FugueServer start()
  {
    super.start();

    // Your Google Cloud Platform project ID
    String projectId = ServiceOptions.getDefaultProjectId();
    
    createTopic(projectId);
    
    return this;
  }

  private void createTopic(String projectId)
  {
    log_.info("About to create topic");
    
    // Create a new topic
    TopicName topicName = TopicName.of(projectId, TOPIC_NAME);
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create())
    {
      Topic topic = topicAdminClient.createTopic(topicName);
      appendStatus("Created topic");
      
      log_.info("Topic {} created.", topic);
      
      createSubscription(projectId, topicName);
    }
    catch (ApiException e)
    {
      switch(e.getStatusCode().getCode())
      {
        case ALREADY_EXISTS:
          appendStatus("Topic already exists");
          createSubscription(projectId, topicName);
          break;
        
        default:
          appendStatus("Cannot create topic: " + e.getStatusCode().getCode());
          log_.error("Failed to create topic, HTTP {} retryable {}", e.getStatusCode().getCode(), 
              e.isRetryable(), e);
          throw new ProgramFault(e);
      }

    }
    catch (Exception e)
    {
      log_.error("Failed to create topic", e);
      throw new ProgramFault(e);
    }
  }
  
  private void createSubscription(String projectId, TopicName topicName)
  {
    log_.info("About to create subscription");

    // Create a new subscription
    SubscriptionName subscriptionName = SubscriptionName.of(projectId, SUBSCRIPTION_NAME);
    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create())
    {
      // create a pull subscription with default acknowledgement deadline
      Subscription subscription =
          subscriptionAdminClient.createSubscription(
              subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
      
      log_.info("Subscription {} created.", subscription);
    }
    catch (ApiException e)
    {
      switch(e.getStatusCode().getCode())
      {
        case ALREADY_EXISTS:
          appendStatus("Subscription already exists");
          break;
        
        default:
          appendStatus("Cannot create subscription: " + e.getStatusCode().getCode());
      }
      log_.error("Failed to create topic, HTTP {} retryable {}", e.getStatusCode().getCode(), 
          e.isRetryable(), e);
    }
    catch (Exception e)
    {
      log_.error("Failed to create topic", e);
    }
    

    
  }

  public static void main(String[] args) throws InterruptedException
  {
    new PubSubExmple().start().join();
  }
}
