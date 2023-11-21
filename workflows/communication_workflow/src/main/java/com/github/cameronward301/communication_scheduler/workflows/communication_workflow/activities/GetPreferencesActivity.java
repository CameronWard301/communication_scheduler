package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity interface for getting the preferences from the kubernetes cluster
 */
@ActivityInterface
public interface GetPreferencesActivity {

    /**
     * Gets the preferences from the kubernetes cluster
     * @return The preferences object for building activities retry policy
     */
    @ActivityMethod
    Preferences getPreferences();
}
