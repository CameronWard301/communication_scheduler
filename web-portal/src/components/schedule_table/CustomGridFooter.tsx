//Adapted from: https://github.com/mui/mui-x/blob/master/packages/x-data-grid/src/components/GridFooter.tsx
import React from "react";
import {
  gridFilteredTopLevelRowCountSelector,
  GridFooterContainer,
  GridFooterContainerProps,
  GridSelectedRowCount,
  gridTopLevelRowCountSelector,
  selectedGridRowsCountSelector,
  useGridApiContext,
  useGridRootProps,
  useGridSelector
} from "@mui/x-data-grid";
import PropTypes from "prop-types";
import {useStore} from "../../context/StoreContext.tsx";

const CustomGridFooter = React.forwardRef<HTMLDivElement, GridFooterContainerProps>(
  function GridFooter(props, ref) {
    const apiRef = useGridApiContext();
    const rootProps = useGridRootProps();
    const totalTopLevelRowCount = useGridSelector(apiRef, gridTopLevelRowCountSelector);
    const selectedRowCount = useGridSelector(apiRef, selectedGridRowsCountSelector);
    const visibleTopLevelRowCount = useGridSelector(apiRef, gridFilteredTopLevelRowCountSelector);
    const rootStore = useStore();

    const selectedRowCountElement =
      !rootProps.hideFooterSelectedRowCount && selectedRowCount > 0 ? (
        <><GridSelectedRowCount
          selectedRowCount={rootStore.scheduleTableStore.selectedAll ? rootStore.scheduleTableStore.totalCount : selectedRowCount}/></>
      ) : (
        <div/>
      );

    const rowCountElement =
      !rootProps.hideFooterRowCount && !rootProps.pagination ? (
        <rootProps.slots.footerRowCount
          {...rootProps.slotProps?.footerRowCount}
          rowCount={totalTopLevelRowCount}
          visibleRowCount={visibleTopLevelRowCount}
        />
      ) : null;

    const paginationElement = rootProps.pagination &&
      !rootProps.hideFooterPagination &&
      rootProps.slots.pagination && (
        <rootProps.slots.pagination {...rootProps.slotProps?.pagination} />
      );

    return (
      <GridFooterContainer ref={ref} {...props}>
        {selectedRowCountElement}
        {rowCountElement}
        {paginationElement}
      </GridFooterContainer>
    );
  },
);

CustomGridFooter.propTypes = {
  // ----------------------------- Warning --------------------------------
  // | These PropTypes are generated from the TypeScript type definitions |
  // | To update them edit the TypeScript types and run "yarn proptypes"  |
  // ----------------------------------------------------------------------
  sx: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.func, PropTypes.object, PropTypes.bool])),
    PropTypes.func,
    PropTypes.object,
  ]),
} as never;

export {CustomGridFooter};
