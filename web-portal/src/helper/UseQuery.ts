import {useLocation} from "react-router-dom";
import React from "react";

const useQuery = () => {
  const {search} = useLocation();

  return React.useMemo(() => new URLSearchParams(search), [search]);
}

export default useQuery;
