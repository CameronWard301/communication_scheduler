import {ComponentPreview, Previews} from "@react-buddy/ide-toolbox";
import {PaletteTree} from "./palette";
import Preferences from "../pages/Preferences.tsx";
import Gateways from "../pages/Gateways.tsx";

const ComponentPreviews = () => {
  return (
    <Previews palette={<PaletteTree/>}>
      <ComponentPreview path="/PaletteTree">
        <PaletteTree/>
      </ComponentPreview>
      <ComponentPreview path="/Preferences">
        <Preferences/>
      </ComponentPreview>
      <ComponentPreview path="/Gateways">
        <Gateways/>
      </ComponentPreview>
    </Previews>
  );
};

export default ComponentPreviews;
