package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetPreferencesActivity {

    @ActivityMethod
    Preferences getPreferences();
}
