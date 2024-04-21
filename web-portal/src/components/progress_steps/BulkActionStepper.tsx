import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import {ProgressStepper} from "./index.ts";
import {BulkActionStore} from "../../stores/BulkActionStore.tsx";
import React, {useCallback, useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {GenerateScheduleStore} from "../../stores/GenerateScheduleStore.tsx";
import {Button} from "@mui/material";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import {SnackbarContext} from "../../context/SnackbarContext.tsx";
import {BulkActionType} from "../../models/Schedules.ts";

export interface BulkActionStepperProps {
  children: React.ReactNode;
}

const BulkActionStepper = observer(({children}: BulkActionStepperProps) => {
  const defaultSteps = ["Selection", "Action", "Review"];
  const gatewaySteps = ["Selection", "Action", "Gateway", "Review"]
  const rootStore = useStore();
  const navigate = useNavigate();
  const snackbar = useContext(SnackbarContext);


  useEffect(() => {
    if (!rootStore.scheduleTableStore.selectedAll && rootStore.scheduleTableStore.checkBoxSelectionModel.length === 0 && !rootStore.bulkActionStore.isUpdateInProgress) {
      navigate("/schedules");
      snackbar.addSnackbar("No schedules selected. Please select a schedule before applying a bulk action", "error");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.scheduleTableStore.selectedAll, rootStore.scheduleTableStore.checkBoxSelectionModel.length]);

  const validateProgress = useCallback((store: BulkActionStore | GenerateScheduleStore) => {
    if (!(store instanceof BulkActionStore)) {
      throw new Error("Invalid store type");
    }

    switch (store.currentStep) {
      case 0:
        navigate("/schedules");
        return;
      case 1:
        if (!store.bulkActionType) {
          store.setAllowNext(false);
          return;
        }
        break;
      case 2:
        if (store.bulkActionType === BulkActionType.Gateway && !store.selectedGateway) {
          store.setAllowNext(false);
          return;
        }
        break;
    }
    store.setAllowNext(true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.bulkActionStore.currentStep, rootStore.bulkActionStore.bulkActionType, rootStore.bulkActionStore.selectedGateway]);

  return (
    <>
      <ProgressStepper confirmButton={<Button id={"apply-bulk-action-button"} variant={"contained"} sx={{ml: 2}}
                                              onClick={() => rootStore.bulkActionStore.setConfirmModalOpen(true)}
                                              endIcon={<CheckRoundedIcon/>}>Apply Action</Button>}
                       steps={rootStore.bulkActionStore.bulkActionType !== BulkActionType.Gateway ? defaultSteps : gatewaySteps}
                       store={rootStore.bulkActionStore} validateProgress={validateProgress}>
        {children}
      </ProgressStepper>
    </>
  )
})

export default BulkActionStepper;
