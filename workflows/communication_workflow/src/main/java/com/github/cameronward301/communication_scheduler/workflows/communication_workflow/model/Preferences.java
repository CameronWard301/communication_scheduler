package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.model;

import lombok.*;

import java.time.Duration;

/**
 * Stores the preferences for the workflow and activities
 * Retrieved from the kubernetes cluster and used to build a Temporal RetryPolicy for the activities
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preferences {
    Duration startToCloseTimeout;
    Integer maximumAttempts;
    Double backoffCoefficient;
    Duration initialInterval;
    Duration maximumInterval;
}
