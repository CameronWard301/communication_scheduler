import {ComponentPreview, Previews} from "@react-buddy/ide-toolbox";
import {PaletteTree} from "./palette";
import Preferences from "../pages/Preferences.tsx";
import GatewayTable from "../pages/Gateway/GatewayTable.tsx";

const ComponentPreviews = () => {
  return (
    <Previews palette={<PaletteTree/>}>
      <ComponentPreview path="/PaletteTree">
        <PaletteTree/>
      </ComponentPreview>
      <ComponentPreview path="/Preferences">
        <Preferences/>
      </ComponentPreview>
      <ComponentPreview path="/GatewayTable">
        <GatewayTable/>
      </ComponentPreview>
    </Previews>
  );
};

export default ComponentPreviews;
