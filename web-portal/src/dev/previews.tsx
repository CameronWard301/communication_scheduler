import {ComponentPreview, Previews} from "@react-buddy/ide-toolbox";
import {PaletteTree} from "./palette";
import Preferences from "../pages/Preferences.tsx";

const ComponentPreviews = () => {
  return (
    <Previews palette={<PaletteTree/>}>
      <ComponentPreview path="/PaletteTree">
        <PaletteTree/>
      </ComponentPreview>
      <ComponentPreview path="/Preferences">
        <Preferences/>
      </ComponentPreview>
    </Previews>
  );
};

export default ComponentPreviews;
