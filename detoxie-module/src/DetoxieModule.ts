import { NativeModule, requireNativeModule } from 'expo';

import { DetoxieModuleEvents } from './DetoxieModule.types';

declare class DetoxieModule extends NativeModule<DetoxieModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<DetoxieModule>('DetoxieModule');
