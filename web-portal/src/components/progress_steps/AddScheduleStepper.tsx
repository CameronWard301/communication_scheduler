import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import React, {useCallback} from "react";
import {ScheduleType} from "../../models/Schedules.ts";
import {GenerateScheduleStore} from "../../stores/GenerateScheduleStore.tsx";
import {ProgressStepper} from "./index.ts";
import {BulkActionStore} from "../../stores/BulkActionStore.tsx";
import {CreateScheduleButton} from "../create_schedule";

export interface AddScheduleStepperProps {
  children: React.ReactNode;
}


const AddScheduleStepper = observer(({children}: AddScheduleStepperProps) => {
  const rootStore = useStore();
  const steps = ["Details", "Gateway", "Schedule", "Review"];

  const validateProgress = useCallback((store: GenerateScheduleStore | BulkActionStore) => {
    if (!(store instanceof GenerateScheduleStore)) {
      throw new Error("Invalid store type");
    }
    switch (store.currentStep) {
      case 0:
        if (!store.isUserIdValid()) {
          store.setAllowNext(false);
          return;
        }
        break;
      case 1:
        if (store.gatewaySelectionModel.length === 0) {
          store.setAllowNext(false);
          return;
        }
        break;
      case 2:
        if (store.selectedScheduleType === ScheduleType.Interval && (!rootStore.createScheduleStore.isIntervalFieldsValid() || rootStore.createScheduleStore.isIntervalFieldsAllZero() || !rootStore.createScheduleStore.isIntervalOffsetValid())) {
          store.setAllowNext(false);
          return;
        }
        if ((store.selectedScheduleType === ScheduleType.CalendarMonth || store.selectedScheduleType === ScheduleType.CalendarWeek) && (!rootStore.createScheduleStore.isMonthTypeValid() || !rootStore.createScheduleStore.isDayOfMonthValid() || rootStore.createScheduleStore.scheduleTimeError)) {
          store.setAllowNext(false);
          return;
        }
        if (store.selectedScheduleType === ScheduleType.Cron && (!rootStore.createScheduleStore.isCronStringValid() || rootStore.createScheduleStore.cronParseError)) {
          store.setAllowNext(false);
          return;
        }
    }
    store.setAllowNext(true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.generateScheduleStore.currentStep, rootStore.generateScheduleStore.userId, rootStore.generateScheduleStore.gatewaySelectionModel, rootStore.generateScheduleStore.selectedScheduleType, rootStore.createScheduleStore.intervalOffset, rootStore.createScheduleStore.intervalSeconds, rootStore.createScheduleStore.intervalMinutes, rootStore.createScheduleStore.intervalHours, rootStore.createScheduleStore.intervalDays, rootStore.generateScheduleStore, rootStore.createScheduleStore, rootStore.createScheduleStore.monthType, rootStore.createScheduleStore.dayOfMonth, rootStore.createScheduleStore.cronString, rootStore.createScheduleStore.scheduleTimeError])


  return (
    <>
      <ProgressStepper confirmButton={<CreateScheduleButton/>} steps={steps} store={rootStore.generateScheduleStore}
                       validateProgress={validateProgress}>
        {children}
      </ProgressStepper>
    </>
  )
})

export default AddScheduleStepper;
